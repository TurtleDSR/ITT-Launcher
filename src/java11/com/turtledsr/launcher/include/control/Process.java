/*
Helper class for managing processes and similar low level machine control
*/

package com.turtledsr.launcher.include.control;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.SwingWorker;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.config.ModCacheManager;
import com.turtledsr.launcher.include.config.gameconfig.GameConfig;
import com.turtledsr.launcher.include.config.gameconfig.GameConfigList;
import com.turtledsr.launcher.include.engine.LivesplitManager;
import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.engine.ZipManager;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.struct.ITTASKeybinds;
import com.turtledsr.launcher.include.struct.Mod;
import com.turtledsr.launcher.include.struct.Mod.Format;
import com.turtledsr.launcher.include.ui.launcher.launch.ModsListPanel;
import com.turtledsr.launcher.include.ui.launcher.launch.ToggleModsButton;

import com.turtledsr.modlib.Context;
import com.turtledsr.modlib.ModLib;

public final class Process {
  private static String gameDirectory;
  private static String gameConfigDirectory;
  private static String steamDirectory;
  private static String shaderCacheHash;

  private static AtomicBoolean cleaningScripts = new AtomicBoolean(false);
  private static Object cleaningLock = new Object();

  private static GameConfigList gameConfigs = null;
  
  public static int STOPPED = 0; //game is fully closed
  public static int STARTING = 1; //launch process has begin locally
  public static int STARTED = 2; //steam launch process has begun
  public static int RUNNING = 3; //game is fully started up *may still be loading*

  public static int gameStatus = STOPPED;

  public static boolean zipFormatPresent = false;

  public static OptionalInt getProcessPID(String processName) {
    return getProcessPID(processName, true);
  }

  public static OptionalInt getProcessPID(String processName, boolean ignoreLetterCase) {
    Predicate<String> matcher = cmd -> (ignoreLetterCase ? cmd.toLowerCase().contains(processName.toLowerCase())
        : cmd.contains(processName));

    try (Stream<ProcessHandle> processes = ProcessHandle.allProcesses()) {
      return processes.filter(p -> p.info().command().filter(matcher).isPresent()).mapToInt(p -> (int) p.pid())
          .findFirst();
    }
  }

  public static long getProcessBaseAddress(int pid) {
    //TH32CS_SNAPMODULE32 is essential even on 64-bit to see the main EXE module
    HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new DWORD(pid));

    if (snapshot == Kernel32.INVALID_HANDLE_VALUE)
      return 0;

    Tlhelp32.MODULEENTRY32W entry = new Tlhelp32.MODULEENTRY32W();
    try {
      if (Kernel32.INSTANCE.Module32FirstW(snapshot, entry)) {
        //the very first module in the list is ALWAYS the .exe itself
        return Pointer.nativeValue(entry.modBaseAddr);
      }
    } finally {
      Kernel32.INSTANCE.CloseHandle(snapshot);
    }
    return 0;
  }

  public static void launchItTakesTwoEx() { //launches with all current launcher settings and returns status
    boolean mods = false;

    if(ToggleModsButton.toggled) {
      for (int i = ModsListPanel.mods.size() - 1; i >= 0; i--) { //top mod gets final priority
        if (ModsListPanel.mods.get(i).toggled) {
          mods = true;
          break;
        }
      }
    }

    final boolean modsEnabled = mods && ToggleModsButton.toggled;
    launchItTakesTwoEx(modsEnabled);
  }

  public static void launchItTakesTwoEx(final boolean modsEnabled) { //launches with all current launcher settings
    try {  
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() {
          EventManager.triggerEvent("game_starting"); //startup event

          try{waitForCleaning();} catch(InterruptedException e) {
            Logs.logError("Failure to wait for script cleaning: " + e.getLocalizedMessage(), "PROCESS");
            EventManager.triggerEvent("game_launch_failure");
            return null;
          }

          if(modsEnabled) {
            disableScriptCache();

            Context context = ModLib.init(getGameDirectory() + "Nuts/Script/");
            if(context == null) {
              EventManager.triggerEvent("mod_inject_failure");
              return null;
            }

            //install mods
            for (int i = ModsListPanel.mods.size() - 1; i >= 0; i--) { //top mod gets final priority
              Mod mod = ModsListPanel.mods.get(i);
              if (mod.toggled) {
                //Logs.log("Installing mod: " + mod.name, "PROCESS");
                //Logs.log("FORMAT: " + mod.format, "PROCESS");
                if(mod.format == Format.asmod) {
                  //Logs.log("ASMOD", "PROCESS");
                  if(!ModLib.install_mod_asmod(context, getGameDirectory() + "Mods/" + ModsListPanel.mods.get(i).name + ".asmod")) {
                    EventManager.triggerEvent("mod_inject_failure");
                    ModLib.destroy(context);
                    return null;
                  }
                } else {
                  //Logs.log("ZIP MOD", "PROCESS");
                  if(!ModLib.install_mod_zip(context, getGameDirectory() + "Mods/" + ModsListPanel.mods.get(i).name + ".zip")) {
                    EventManager.triggerEvent("mod_inject_failure");
                    ModLib.destroy(context);
                    return null;
                  }
                }
              }
            }

            ModLib.destroy(context);

            //patch speedtools to use settings from legacy injector
            Path speedSettingsPath = Paths.get(getGameDirectory(), "Nuts/Script/Speed/SpeedSettings.as");
            if(Files.exists(speedSettingsPath)) {

              File installerPath = new File(System.getenv("LOCALAPPDATA"), "ITTAS-Installer/");
              if(installerPath.exists()) {
                try{
                  File newestSubfolder = Arrays.stream(installerPath.listFiles(File::isDirectory)).max(Comparator.comparingLong(File::lastModified)).orElse(null);
                  File configFile = new File(newestSubfolder, "1.0.0.0/user.config");

                  Element root = FileHelper.readFileAsXMLTree(configFile.toPath());
                  Element userSettingsNode = (Element)(root.getElementsByTagName("userSettings").item(0));
                  Element ITTAS_InstallerPropertiesSettingsNode = (Element)(userSettingsNode.getElementsByTagName("ITTAS_Installer.Properties.Settings").item(0));

                  NodeList settingNodes = ITTAS_InstallerPropertiesSettingsNode.getElementsByTagName("setting");

                  boolean speedtoolsActiveOnLaunch = ((Element) settingNodes.item(1)).getElementsByTagName("value").item(0).getTextContent().equals("True");

                  int save1 = Integer.parseInt(((Element) settingNodes.item(2)).getElementsByTagName("value").item(0).getTextContent());
                  int save2 = Integer.parseInt(((Element) settingNodes.item(3)).getElementsByTagName("value").item(0).getTextContent());

                  int load1 = Integer.parseInt(((Element) settingNodes.item(4)).getElementsByTagName("value").item(0).getTextContent());
                  int load2 = Integer.parseInt(((Element) settingNodes.item(5)).getElementsByTagName("value").item(0).getTextContent());

                  int teleport1 = Integer.parseInt(((Element) settingNodes.item(6)).getElementsByTagName("value").item(0).getTextContent());
                  int teleport2 = Integer.parseInt(((Element) settingNodes.item(7)).getElementsByTagName("value").item(0).getTextContent());

                  List<String> lines = new ArrayList<String>(8);
                  for (int i = 0; i < 8; i++) {
                    lines.add("");
                  }

                  lines.set(0, "const bool SpeedToolsActiveOnLaunch = " + speedtoolsActiveOnLaunch + ";");

                  lines.set(2, "const FName SaveState1 = ActionNames::" + ITTASKeybinds.actionNames[save1] + ";");
                  lines.set(3, "const FName SaveState2 = ActionNames::" + ITTASKeybinds.actionNames[save2] + ";");

                  lines.set(4, "const FName LoadState1 = ActionNames::" + ITTASKeybinds.actionNames[load1] + ";");
                  lines.set(5, "const FName LoadState2 = ActionNames::" + ITTASKeybinds.actionNames[load2] + ";");

                  lines.set(6, "const FName TeleportOther1 = ActionNames::" + ITTASKeybinds.actionNames[teleport1] + ";");
                  lines.set(7, "const FName TeleportOther2 = ActionNames::" + ITTASKeybinds.actionNames[teleport2] + ";");

                  FileHelper.saveFileFromLines(speedSettingsPath, lines);
                } catch(Exception e) {
                  Logs.logError("Failed to patch speedtools: " + e.getMessage(), "PROCESS");
                }
              } else {
                List<String> lines = FileHelper.readFileAsLines(speedSettingsPath);

                lines.set(0, "const bool SpeedToolsActiveOnLaunch = true;");

                FileHelper.saveFileFromLines(speedSettingsPath, lines);
              }

              Logs.log("Speedtools succesfully patched", "PROCESS");
            }
          } else {
            enableScriptCache();
          }

          launchItTakesTwo();
          return null;
        }
      }.execute();
    } catch(Exception e) {
      Logs.logError("Failed to launch game", "PROCESS");
      EventManager.triggerEvent("game_launch_failure");
    }
  }

  public static boolean launchItTakesTwo() { //returns status
    if(steamDirectory == null) getSteamDirectory();
    String appId = "1426210";
    String[] launchArgs = {"-devmenu", "-dx12"};

    ArrayList<String> command = new ArrayList<>();

    command.add(steamDirectory + "/steam.exe");
    command.add("-applaunch");
    command.add(appId);

    for (String arg : launchArgs) { //add arguments
      command.add(arg);
    }
    
    try {
      new ProcessBuilder(command).start();

      Logs.log("Successfully launched game", "PROCESS");
      EventManager.triggerEvent("game_started");
      return true;
    } catch (Exception e) {
      Logs.logError("Failed to launch game: " + e.getMessage(), "PROCESS");
      return false;
    }
  }

  public static boolean launchLivesplit(String chapter) { //returns status
    OptionalInt pid = getProcessPID("LiveSplit.exe");
    while (pid.isPresent()) { //close livesplit if its open
      ProcessHandle.of(pid.getAsInt()).ifPresent(ProcessHandle::destroy);
      pid = getProcessPID("LiveSplit.exe");
    }

    try {
      new ProcessBuilder(LivesplitManager.livesplitExecutablePath, "-s", LivesplitManager.getLayoutPath(chapter))
          .start();
      return true;
    } catch (Exception e) {
      Logs.logError(e.getMessage(), "PROCESS");
      return false;
    }
  }

  public static String getSteamDirectory() { //returns the directory of steam
    if(steamDirectory == null) {
      if (Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Valve\\Steam")) {
        steamDirectory = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Valve\\Steam", "InstallPath");
      } else if(Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Valve\\Steam")) {
        steamDirectory = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Valve\\Steam", "InstallPath");
      }
    }

    return steamDirectory;
  }

  public static String getGameDirectory() { //returns the directory of It Takes Two
    if(gameDirectory == null) {
      try {
        if(steamDirectory == null) getSteamDirectory();

        List<String> libraryPaths = new ArrayList<>();
        File vdfFile = new File(steamDirectory, "steamapps/libraryfolders.vdf");
          
        try(BufferedReader reader = new BufferedReader(new FileReader(vdfFile))) {
          String line;
          Pattern pathPattern = Pattern.compile("\"path\"\\s*\"([^\"]+)\"");
          
          while ((line = reader.readLine()) != null) {
            Matcher matcher = pathPattern.matcher(line);
            if (matcher.find()) {
              String path = matcher.group(1).replace("\\\\", "/");

              libraryPaths.add(path);
            }
          }

          for(int i = 0; i < libraryPaths.size(); i++) {
            gameDirectory = libraryPaths.get(i) + "/steamapps/common/ItTakesTwo/";
            if(new File(gameDirectory).isDirectory()) {
              return gameDirectory;
            }
          }
        }
      } catch (Exception e) {
        Logs.logError("CANNOT READ STEAMDIR REGISTRY", "PROCESS");
        Logs.logError(e.getMessage(), "PROCESS");
        Main.lockQueue = true;
        gameDirectory = null;
      }
    }

    return gameDirectory;
  }

  public static String getGameConfigDirectory() {
    if(gameConfigDirectory == null) {
      gameConfigDirectory = System.getenv("localappdata") + "/ItTakesTwo/Saved/Config/WindowsNoEditor/";
    }

    return gameConfigDirectory;
  }

  public static String getShaderCacheHash() { 
    if(shaderCacheHash != null) return shaderCacheHash;

    File shaderCacheFolder = new File(System.getenv("localappdata") + "/ItTakesTwo/Saved/");

    File[] files = shaderCacheFolder.listFiles();

    if (files == null || files.length == 0) {
      Logs.logError("NO CACHE HASH FOUND", "PROCESS");
      return null;
    }

    for (File f : files) {
      if (f == null)
        continue;
      String fullname = f.getName();
      int partition = fullname.lastIndexOf('.');
      if (partition == -1)
        continue;
      String extension = fullname.substring(partition + 1);
      String name = fullname.substring(0, partition);
      if (extension.equalsIgnoreCase("ushaderprecache")) {
        if(name.startsWith("D3DCompute_")) {
          shaderCacheHash = name.substring(name.indexOf('_') + 1);
          Logs.log("Shader Cache Hash Found: " + shaderCacheHash, "PROCESS");
          return shaderCacheHash;
        }
      }
    }

    Logs.logError("NO CACHE HASH FOUND", "PROCESS");
    return null;
  }

  public static ArrayList<Mod> getModList() {
    try {
      ArrayList<Mod> out = new ArrayList<Mod>();
      List<Mod> cached; //for mod cache
      ArrayList<Mod> directory = new ArrayList<Mod>(); //mods in the directory

      zipFormatPresent = false;

      File modFolder = new File(getGameDirectory() + "Mods/");
      if (!modFolder.exists()) {
        modFolder.mkdir();
      }

      File[] files = modFolder.listFiles();

      if (files == null || files.length == 0) {
        Logs.logError("NO MODS FOUND", "PROCESS");
        return null;
      }

      for (File f : files) {
        if (f == null)
          continue;
        String fullname = f.getName();
        int partition = fullname.lastIndexOf('.');

        if (partition == -1)
          continue;

        String extension = fullname.substring(partition + 1);
        String name = fullname.substring(0, partition);
        Format format = extension.equalsIgnoreCase("asmod") ? Format.asmod : Format.zip;

        if (extension.equalsIgnoreCase("zip") || extension.equalsIgnoreCase("asmod")) {
          if (!(name.equals("Default") || name.equals("Backup") ||  name.equals("Default-Game-Backup"))) {  //check if zip is default scripts folder
            //Logs.log("Found mod formatted ." + format + " name: " + name, "PROCESS");
            directory.add(new Mod(name, false, format));
          }
        }
      }

      File launcherDirectory = new File(getGameDirectory() + "Launcher/");
      if (!launcherDirectory.exists()) {
        launcherDirectory.mkdir();
      }

      ModCacheManager.loadCache();
      if(ModCacheManager.cache == null) ModCacheManager.loadCache(); //try again
      cached = ModCacheManager.cache.mods;

      //Logs.log("Found " + cached.size() + " cached mods", "PROCESS");

      for (int i = 0; i < directory.size(); i++) { //add new mods at the top of the list
        if (!cached.contains(directory.get(i))) {
          Mod current = directory.get(i);
          if(current.format == Format.zip) zipFormatPresent = true;
          out.add(current);
        }
      }

      for (int i = 0; i < cached.size(); i++) { // add the rest of the mods in the correct order
        Mod current = cached.get(i);

        int pos = directory.indexOf(current);
        if(pos == -1) continue;

        Mod direct = directory.get(pos);

        current.format = direct.format; //update format in case it changed

        if(current.format == Format.zip) zipFormatPresent = true;
        out.add(current);
      }

      updateModListCache(out);

      return out;
    } catch (Exception e) {
      Logs.logError("NO MODS FOUND: " + e.getMessage(), "PROCESS");
      return null;
    }
  }

  public static void updateModListCache(ArrayList<Mod> modList) {
    if(modList == null) return;
    ModCacheManager.cache.mods = modList;
    ModCacheManager.writeCache();
  }

  public static boolean getScriptCacheEnabled() {
    return new File(getGameDirectory() + "Nuts/Script/PrecompiledScript.Cache").exists();
  }

  public static void enableScriptCache() {
    Logs.log("Enabling script cache", "PROCESS");
    if (getScriptCacheEnabled())
      return;

    Path target = Paths.get(getGameDirectory() + "Nuts/Script/PrecompiledScript.Cache");
    Path source = Paths.get(getGameDirectory() + "Nuts/Script/PrecompiledScript/PrecompiledScript.Cache");

    try {
      Files.move(source, target);

      File tempDir = new File(getGameDirectory() + "Nuts/Script/PrecompiledScript/");
      tempDir.delete();
    } catch (Exception e) {
      Logs.logError(e.getMessage(), "PROCESS");
    }
  }

  public static void disableScriptCache() {
    Logs.log("Disabling script cache", "PROCESS");
    if (!getScriptCacheEnabled())
      return;

    Path target = Paths.get(getGameDirectory() + "Nuts/Script/PrecompiledScript/PrecompiledScript.Cache");
    Path source = Paths.get(getGameDirectory() + "Nuts/Script/PrecompiledScript.Cache");

    try {
      Files.createDirectories(target.getParent()); //create temp folder

      Files.move(source, target);
    } catch (Exception e) {
      Logs.logError(e.getMessage(), "PROCESS");
    }
  }

  public static void installBackup(InputStream stream) {
    try {
      ZipManager.extractFromStream(stream, getGameDirectory() + "Nuts/Script/");
    } catch (Exception e) {
      Logs.logError("could not install backup: " + e.getMessage(), "PROCESS");
    }
  }

  public static void cleanScripts() {
    setCleaningFlag(true);
    try {
      Logs.log("Cleaning scripts folder", "PROCESS");
      FileHelper.deleteSubfolders(getGameDirectory() + "Nuts/Script/");
      installBackup(Main.getResourceAsStream("mods/Default.zip"));
    } catch (Exception e) {
      Logs.logError("could not clean scripts: " + e.getMessage(), "PROCESS");
    }
    setCleaningFlag(false);
    Logs.log("Scripts folder cleaned", "PROCESS");
  }

  public static void cleanScriptsAsync() {
    Thread cleanerThread = new Thread(() -> cleanScripts());
    cleanerThread.setDaemon(true);
    cleanerThread.start();
  }

  public static void openLink(String link) {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      try{
        Desktop.getDesktop().browse(URI.create(link));
      } catch(Exception e) {
        Logs.logError("Failed to open link '" + link + "' - " + e.getMessage(), "PROCESS");
      }
    }
  }

  public static void downloadFile(String link, String filepath) throws Exception {
    URL url = new URL(link);
    Path path = Paths.get(filepath);

    InputStream in = url.openStream();
    Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
  }

  private static void setCleaningFlag(boolean cleaning) {
    cleaningScripts.set(cleaning);
    synchronized (cleaningLock) {
      cleaningLock.notifyAll();
    }
  }

  private static void waitForCleaning() throws InterruptedException{
    synchronized (cleaningLock) {
      while(cleaningScripts.get()) {
        cleaningLock.wait();
      }
    }
  }

  public static GameConfigList getGameConfigs() {
    readGameConfigs();
    return gameConfigs;
  }

  public static void readGameConfigs() {
    File configDirectory = new File(getGameConfigDirectory());
    gameConfigs = new GameConfigList();
    if(configDirectory.isDirectory()) {
      File[] configFiles = configDirectory.listFiles(new FileFilter() {
        @Override
        public boolean accept(File file) {
          try{
            Scanner scanner = new Scanner(file);
            boolean accept = file.getName().endsWith(".ini") && !scanner.nextLine().trim().equals("");
            scanner.close();

            if(file.getName().equals("Engine.ini") || file.getName().equals("EditorPerProjectUserSettings.ini")) accept = false;
            return accept;
          } catch(Exception e) {return false;}
        }
      });

      for (File file : configFiles) {
        try(Scanner scanner = new Scanner(file)) {
          String category = file.getName().substring(0, file.getName().lastIndexOf('.'));
          String ownerObject = null;

          while(scanner.hasNextLine()) {
            String line = scanner.nextLine().strip();
            if(line.isBlank()) continue;
            if(line.charAt(0) == '[') {
              ownerObject = line.substring(line.lastIndexOf('.') + 1, line.lastIndexOf(']'));
              continue;
            }

            if(ownerObject == null) continue;

            int separator = line.lastIndexOf('=');
            String identifier = line.substring(0, separator);
            String value = line.substring(separator + 1, line.length());

            GameConfig config = new GameConfig(category, ownerObject, identifier, value);
            LinkedHashMap<String, ArrayList<GameConfig>> owners = gameConfigs.get(category);
            if(owners == null) {
              owners = new LinkedHashMap<>();
              gameConfigs.put(category, owners);
            }

            ArrayList<GameConfig> configs = owners.get(ownerObject);
            if(configs == null) {
              configs = new ArrayList<>();
              owners.put(ownerObject, configs);
            }

            configs.add(config);
          }
        } catch(Exception e) {
          Logs.log(String.format("Failed to read config file %s: %s", file.getName(), e.getLocalizedMessage()));
        }
      }
    }
  }

  public static void writeGameConfigs() {
    GameConfigList configs = getGameConfigs();
    if(configs == null) return;
    
    Path configDirectory = Paths.get(getGameConfigDirectory());

    for (String fileKey : configs.keySet()) {
        LinkedHashMap<String, ArrayList<GameConfig>> configFile = configs.get(fileKey);
        String fileContents = "";
        for(String setKey : configFile.keySet()) {
          fileContents += String.format("[/Script/Angelscript.%s]\n", setKey);
          ArrayList<GameConfig> configSet = configFile.get(setKey);
          for(GameConfig config : configSet) {
            fileContents += String.format("%s=%s\n", config.identifier, config.value);
          }
        }

        Path updateFile = configDirectory.resolve(String.format("%s.ini", fileKey));
        try{
          Files.writeString(updateFile, fileContents);
        } catch(Exception e) {
          Logs.logError(String.format("Failed to update %s: %s", updateFile.toString(), e.getLocalizedMessage()), "PROCESS");
        }
      }
  }
}