/*
Entry point for the ITT Ranked client
*/

package com.turtledsr.launcher;

import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.technicjelle.UpdateChecker;
import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.control.Autosplitter;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.control.TimerHandler;
import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.engine.SingleInstanceManager;
import com.turtledsr.launcher.include.engine.events.EventListener;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.launcher.Window;
import com.turtledsr.launcher.include.ui.launcher.debug.LogPanel;
import com.turtledsr.launcher.include.ui.launcher.rootPanels.MainPanel;
import com.turtledsr.launcher.include.ui.styled.button.RoundedFlatButton;
import com.turtledsr.launcher.include.ui.styled.messageBox.MessageBox;

public final class Main {
  public static final String TITLE = "It Takes Two Launcher";
  public static final String VERSION = "2.0.0";

  public static final int RECONNECTION_INTERVAL = 500;
  public static boolean scriptCacheEnabled = Process.getScriptCacheEnabled();
  public static boolean lockQueue = false;
  public static AtomicBoolean updating = new AtomicBoolean(false);
  
  public static boolean timerConnected = false;
  public static boolean gameConnected = false;
  
  public static SystemTray tray;
  public static TrayIcon trayicon;
  public static PopupMenu trayMenu;

  public static MenuItem restoreButton;
  public static MenuItem hideButton;
  public static MenuItem exitButton;

  public static Window window;

  public static boolean serverThreadSafe = true; //indicates if the server thread needs to close
  public static Thread serverThread;
  public static ServerSocket serverSocket;
  public static ExecutorService socketThreadPool;

  public static String[] args;

  private static WatchService modWatcher;
  private static WatchService gameConfigWatcher;
  
  public static void main(String[] args) throws Exception {
    MainPanel.createLogPanel(); //initialize log panel first so we can log things

    String allArgs = "";
    for (String arg : args) {
      allArgs += arg + " ";
    }

    Main.args = args;

    Logs.log(String.format("Arguments (%s): %s", args.length, allArgs), "MAIN");

    if(SingleInstanceManager.checkIfAlreadyRunning()) { //check if program is already running
      SettingsManager.loadSettings(false); //dont hook settings to update on close

      Socket messenger = new Socket("localhost", SettingsManager.settings.developerSettings.socketPort); //tell launcher to restore window if its in the tray
      messenger.getOutputStream().write("restore_window".getBytes());
      messenger.getOutputStream().flush();
      messenger.close();
      System.exit(0); //close program
    };

    SettingsManager.loadSettings(); //load settings from settings.json file

    serverThread = new Thread(() -> startServerSocketThread());
    serverThread.setDaemon(true);
    serverThread.start();

    ImageManager.loadImages();
    FontManager.loadFonts();
    //LivesplitManager.extractTimer();

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        Process.gameStatus = Process.STARTING;
      }
    }, "game_starting");

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        Process.gameStatus = Process.STARTED;
      }
    }, "game_started");

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        Process.gameStatus = Process.RUNNING;
      }
    }, "game_connected");

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        Process.gameStatus = Process.STOPPED;
        Process.cleanScriptsAsync();
      }
    }, "game_disconnected");

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        new MessageBox("Mod Injection Failure", "One or more mods failed to install!");
        Process.gameStatus = Process.STOPPED;
        Process.cleanScriptsAsync();
      }
    }, "mod_inject_failure");

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        new MessageBox("Game Launch Failure", "Game failed to launch!");
        Process.gameStatus = Process.STOPPED;
        Process.cleanScriptsAsync();
      }
    }, "game_launch_failure");

    window = new Window();
    LogPanel.updateStyle();

    addToTray();

    TimerHandler.connect();
    Autosplitter.bind();

    if(SettingsManager.settings.developerSettings.checkForUpdates) { //check for updates
      checkForUpdates(false);
    }

    if(Process.zipFormatPresent && !argumentPresent("-suppress_messages")) new MessageBox(
      "Zip format mods found",
      "All mods with a <span style=\"color: 'red';\">*</span> are outdated and use the zip format. " +
      "Either update to the newest version of the mod or ask the developer to make a .asmod port."
    );

    if(argumentPresent("-update") && !argumentPresent("-suppress_messages")) {
      new MessageBox(
        "Update installed",
        "The newest update has now been installed.\nThank you for supporting ITT-Launcher!"
      );
    }

    Process.cleanScriptsAsync();

    Process.getGameConfigs();

    try {
      modWatcher = FileSystems.getDefault().newWatchService();
      Paths.get(Process.getGameDirectory() + "Mods/").register(modWatcher,
        StandardWatchEventKinds.ENTRY_CREATE, 
        StandardWatchEventKinds.ENTRY_DELETE
      );
      Thread modWatchThread = new Thread(() -> {
        try {
          while(true) {
            WatchKey key = modWatcher.take();
            key.pollEvents();
            EventManager.triggerEvent("Mod_Folder_Update");
            key.reset();
          }
        } catch(Exception e) {Logs.logError("Failure to watch for Mods folder change: " + e.getLocalizedMessage(), "MOD_WATCH_THREAD");}
      });
      modWatchThread.setDaemon(true);
      modWatchThread.start();

      gameConfigWatcher = FileSystems.getDefault().newWatchService();
      Paths.get(Process.getGameConfigDirectory()).register(gameConfigWatcher, 
        StandardWatchEventKinds.ENTRY_CREATE, 
        StandardWatchEventKinds.ENTRY_DELETE, 
        StandardWatchEventKinds.ENTRY_MODIFY
      );
      Thread configWatchThread = new Thread(() -> {
        try {
          while(true) {
            WatchKey key = gameConfigWatcher.take();
            key.pollEvents();
            EventManager.triggerEvent("Config_Folder_Update");
            key.reset();
          }
        } catch(Exception e) {Logs.logError("Failure to watch for Game Config folder change: " + e.getLocalizedMessage(), "CONFIG_WATCH_THREAD");}
      });
      configWatchThread.setDaemon(true);
      configWatchThread.start();
    } catch(Exception e) {
      Logs.logError("Failed to register file watchers: " + e.getLocalizedMessage(), "MAIN");
    }

    while(true) {
      tick();
    }
  }

  protected static void tick() {
    if (Process.getProcessPID("Livesplit.exe").isEmpty()) {
      if(timerConnected) {
        EventManager.triggerEvent("timer_disconnected");
      }
      timerConnected = false;
      if (!TimerHandler.awaitingReconnection) {
        TimerHandler.scheduleReconnect(RECONNECTION_INTERVAL);
      }
    } else {
      if(!timerConnected) {
        EventManager.triggerEvent("timer_connected");
      }
    
      timerConnected = true;
    }

    if (Process.getProcessPID("ItTakesTwo.exe").isEmpty()) {
      if(gameConnected) {
        EventManager.triggerEvent("game_disconnected");
      }
      gameConnected = false;

      if (!Autosplitter.awaitingReconnection) {
        Autosplitter.scheduleBind(RECONNECTION_INTERVAL);
      }
    } else {
      if(!gameConnected) {
        EventManager.triggerEvent("game_connected");
      }
      gameConnected = true;
    }
  }

  public static void addToTray() {
    if(tray == null) tray = SystemTray.getSystemTray();

    createTrayMenu();

    if(trayicon == null) {
      trayicon = new TrayIcon(ImageManager.icon, "ITT Launcher", trayMenu);
      trayicon.setImageAutoSize(true);
    }
    try{
      tray.add(trayicon);
    } catch (Exception e) {
      Logs.logError("Failed to add icon to tray: " + e.getMessage(), "MAIN");
    }
  }

  public static void hideWindow() {
    window.setVisible(false);
    createTrayMenu();
  }

  public static void restoreWindow() {
    window.setVisible(true);
    createTrayMenu();
  }

  public static void checkForUpdates() {
    checkForUpdates(true);
  }

  public static void checkForUpdates(boolean sendMessageIfNoUpdate) {
    Thread updateThread = new Thread(() -> startUpdateThread(sendMessageIfNoUpdate));
    updateThread.setDaemon(true);
    updateThread.start();
  }

  public static InputStream getResourceAsStream(String path) {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
  }

  private static void handleClientSocket(Socket clientSocket) {
    try(
      Socket client = clientSocket;
      BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    ){
      String input;
      while((input = reader.readLine()) != null) {
        Logs.log("ServerSocket Recieved Message: " + input, "SERVER_SOCKET_THREAD");

        if(input.equalsIgnoreCase("show_debug_message")) {
          new MessageBox("Debug", "Test Message!<br><br>The quick brown fox jumps over the lazy dog");
        }

        if(input.equalsIgnoreCase("hide_window")) {
          hideWindow();
        }

        if(input.equalsIgnoreCase("restore_window")) {
          restoreWindow();
        }

        if(input.equalsIgnoreCase("shutdown_application")) {
          System.exit(0);
        }
      }
    } catch(Exception e) {
      Logs.logError("Client Socket Failure: " + e.getLocalizedMessage(), "SERVER_SOCKET_THREAD");
    }
  }

  private static void startServerSocketThread() {
    try {
      Logs.log("Starting ServerSocket on Port " + SettingsManager.settings.developerSettings.socketPort, "SERVER_SOCKET_THREAD");

      serverSocket = new ServerSocket(SettingsManager.settings.developerSettings.socketPort);
      socketThreadPool = Executors.newCachedThreadPool();

      while(serverThreadSafe) {
        Socket client = serverSocket.accept();
        if(socketThreadPool != null) {
          socketThreadPool.submit(() -> handleClientSocket(client));
        } else {
          client.close();
        }
      }
    } catch(Exception e) {
      Logs.logError("Server Socket Failure: " + e.getLocalizedMessage(), "SERVER_SOCKET_THREAD");
      socketThreadPool.shutdown();
      socketThreadPool = null;

      serverThreadSafe = false;
    }
  }

  private static void startUpdateThread(boolean sendMessageIfNoUpdate) {
    try{
      UpdateChecker updateChecker = new UpdateChecker("TurtleDSR", "ITT-Launcher", VERSION);

      if(updateChecker.isUpdateAvailable()) {
        String message = String.format(
          "New update available: (%s) -> (%s)" +
          "<br>" +
          "<br>" +
          "Manually download it on <a href=\"%s\">Github</a> or:" +
          "<br>" +
          "Click below to install automatically.",

          VERSION, updateChecker.getLatestVersion(), updateChecker.getUpdateUrl()
        );

        RoundedFlatButton installButton = new RoundedFlatButton("Install", StyleManager.launch_button_color);
        installButton.setPreferredSize(new Dimension(80, 25));
        installButton.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) {
            installButton.setBackground(StyleManager.launch_button_hover_color);
          }
          @Override
          public void mouseExited(MouseEvent e) {
            installButton.setBackground(StyleManager.launch_button_color);
          }
        });
        installButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String link = String.format("https://github.com/TurtleDSR/ITT-Launcher/releases/download/v%s/ITT-Launcher.zip", updateChecker.getLatestVersion());
            String download_path = "Launcher-Update.zip";

            installButton.setText("Downloading!");

            updating.set(true);

            Thread updateThread = new Thread(() -> {
              for(int i = 0; i < 3; i++) {
                try{
                  Process.downloadFile(link, download_path);
                  installButton.setText("Installing!");

                  ProcessBuilder process = new ProcessBuilder("update.exe");
                  process.start();
                  System.exit(0);

                } catch(Exception ex) {
                  Logs.logError("Failed to fetch update files: " + ex.getLocalizedMessage(), "MAIN");
                  installButton.setText("Failure!");
                }
              }

              updating.set(false);
            });

            updateThread.setDaemon(true);
            updateThread.start();
          }
        });
        if(!argumentPresent("-suppress_messages")) new MessageBox("Update Available", message, installButton);
      } else if(sendMessageIfNoUpdate) {
        new MessageBox("No Update Available", "Latest version installed: (" + VERSION + ")");
      }
    } catch(Exception e) {
      Logs.logError("Failed to check for updates: " + e.getLocalizedMessage(), "UPDATE_THREAD");
    }
  }

  private static void createTrayMenu() {
    trayMenu = new PopupMenu();

    if(restoreButton == null) {
      restoreButton = new MenuItem("Restore Window");
      restoreButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          restoreWindow();
        }
      });
    }
    if(!window.isVisible()) trayMenu.add(restoreButton);

    if(hideButton == null) {
      hideButton = new MenuItem("Hide Window");
      hideButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          hideWindow();
        }
      });
    }
    if(window.isVisible()) trayMenu.add(hideButton);

    if(exitButton == null) {
      exitButton = new MenuItem("Exit");
      exitButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if(Process.gameStatus == Process.STOPPED) System.exit(0);
        }
      });
    }
    trayMenu.add(exitButton);

    if(trayicon != null) trayicon.setPopupMenu(trayMenu);
  }

  public static boolean argumentPresent(String argument) {
    return Arrays.stream(args).anyMatch(arg -> arg.equals(argument));
  }
}