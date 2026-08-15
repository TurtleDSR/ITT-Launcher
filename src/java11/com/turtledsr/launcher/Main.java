/*
Entry point for the ITT Ranked client
*/

package com.turtledsr.launcher;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.control.Autosplitter;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.control.TimerHandler;
import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.engine.ShaderManager;
import com.turtledsr.launcher.include.engine.SingleInstanceManager;
import com.turtledsr.launcher.include.engine.events.EventListener;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.launcher.Window;
import com.turtledsr.launcher.include.ui.launcher.debug.LogPanel;
import com.turtledsr.launcher.include.ui.launcher.rootPanels.MainPanel;
import com.turtledsr.launcher.include.ui.styled.messageBox.MessageBox;

public final class Main {
  public static final String TITLE = "It Takes Two Launcher";

  public static final int RECONNECTION_INTERVAL = 500;
  public static boolean scriptCacheEnabled = Process.getScriptCacheEnabled();
  public static boolean lockQueue = false;
  
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
  
  public static void main(String[] args) throws Exception {
    MainPanel.createLogPanel(); //initialize log panel first so we can log things

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
    ShaderManager.extractShaders();
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
      }
    }, "game_disconnected");

    window = new Window();

    addToTray();

    TimerHandler.connect();
    Autosplitter.bind();

    //update log panel to have correct style
    LogPanel.updateStyle();

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
          new MessageBox("Debug", "Test Message!\n\nThe quick brown fox jumps over the lazy dog");
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
}