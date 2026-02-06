/*
Helper class for managing processes and similar low level machine control
*/

package com.turtledsr.launcher.include.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.SwingWorker;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.engine.LivesplitManager;
import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.engine.ZipManager;
import com.turtledsr.launcher.include.struct.Mod;
import com.turtledsr.launcher.include.ui.main.launcher.ModsPanel;
import com.turtledsr.launcher.include.ui.main.launcher.ToggleModsButton;

public final class Process {
  private static String gameDirectory;

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
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        disableScriptCache();

        if (ToggleModsButton.toggled) { //mods enabled
          //clean scripts
          Logs.log("Cleaning scripts folder", "PROCESS");
          installMod(Main.getResourceAsStream("mods/Default.zip"));

          //install mods
          for (int i = ModsPanel.mods.size() - 1; i >= 0; i--) { //top mod gets final priority
            if (ModsPanel.mods.get(i).toggled) {
              Logs.log("Installing mod: " + ModsPanel.mods.get(i).name, "PROCESS");
              installMod(getGameDirectory() + "Mods/" + ModsPanel.mods.get(i).name + ".zip");
            }
          }

        } else {
          //remove mods
          Logs.log("Uninstalling mods", "PROCESS");
          installMod(Main.getResourceAsStream("mods/Default.zip"));
        }
        
        launchItTakesTwo();
        return null;
      }
    }.execute();
  }

  public static boolean launchItTakesTwo() { //returns status
    String os = System.getProperty("os.name").toLowerCase();
    String cmd = "steam://rungameid/1426210"; //run It Takes Two command

    try {
      if (os.contains("win")) {
        new ProcessBuilder("cmd.exe", "/c", "start", cmd).start();
      } else if (os.contains("mac")) {
        new ProcessBuilder("open", cmd).start();
      } else {
        new ProcessBuilder("steam", cmd).start();
      }
      Logs.log("Successfully launched game", "PROCESS");
      return true;
    } catch (Exception e) {
      Logs.logError("Failed to launch game" + e.getMessage(), "PROCESS");
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

  public static String getGameDirectory() { //returns the directory of It Takes Two
    if(gameDirectory == null) {
      try {
        String steamDirectory;

        if (Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Valve\\Steam")) {
          steamDirectory = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Valve\\Steam", "InstallPath");
        }

        steamDirectory = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Valve\\Steam", "InstallPath");

        List<String> libraryPaths = new ArrayList<>();
        File vdfFile = new File(steamDirectory, "steamapps/libraryfolders.vdf");
          
        try(BufferedReader reader = new BufferedReader(new FileReader(vdfFile))) {
          String line;
          Pattern pathPattern = Pattern.compile("\"path\"\\s*\"([^\"]+)\"");
          
          while ((line = reader.readLine()) != null) {
            Matcher matcher = pathPattern.matcher(line);
            if (matcher.find()) {
              String path = matcher.group(1).replace("\\\\", "\\");
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

  public static ArrayList<Mod> getModList() {
    try {
      ArrayList<Mod> out = new ArrayList<Mod>();
      ArrayList<Mod> cached = new ArrayList<Mod>(); //for mod cache
      ArrayList<Mod> directory = new ArrayList<Mod>(); //mods in the directory

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

        Logs.log("Found mod: " + name, "PROCESS");

        if (extension.equalsIgnoreCase("zip")) {
          if (!(name.equals("Default") || name.equals("Backup") ||  name.equals("Default-Game-Backup"))) {
            directory.add(new Mod(name)); //check if zip is default scripts folder
          }
        }
      }

      File launcherDirectory = new File(getGameDirectory() + "Launcher/");
      if (!launcherDirectory.exists()) {
        launcherDirectory.mkdir();
      }

      File cacheFile = new File(getGameDirectory() + "Launcher/modlist.cache");
      if (!cacheFile.exists()) {
        cacheFile.createNewFile();
      } else {
        try (Scanner scanner = new Scanner(cacheFile)) {

          while (scanner.hasNextLine()) {
            String name = scanner.nextLine();

            if (!scanner.hasNextLine())
              throw new Exception("MISSING CACHE TOKEN");

            String toggled = scanner.nextLine();

            if (!(toggled.equalsIgnoreCase("true") || toggled.equalsIgnoreCase("false"))) {
              throw new Exception("INVALID CACHE TOKEN");
            }

            Mod current = new Mod(name, toggled.equalsIgnoreCase("true"));

            if (directory.contains(current))
              cached.add(current); //check if the mod is still in the directory
          }

          scanner.close();
        }
      }

      for (int i = 0; i < directory.size(); i++) { //add new mods at the top of the list
        if (!cached.contains(directory.get(i))) {
          out.add(directory.get(i));
        }
      }

      for (int i = 0; i < cached.size(); i++) { // add the rest of the mods in the correct order
        out.add(cached.get(i));
      }

      updateModListCache(out);

      return out;
    } catch (Exception e) {
      Logs.logError("NO MODS FOUND: " + e.getMessage(), "PROCESS");
      return null;
    }
  }

  public static void updateModListCache(ArrayList<Mod> modList) {
    File cacheFile = new File(getGameDirectory() + "Launcher/modlist.cache");
    try {
      if (!cacheFile.exists()) {
        cacheFile.createNewFile();
      } else {
        PrintWriter writer = new PrintWriter(cacheFile);

        for (Mod current : modList) {
          writer.println(current.name + "\n" + current.toggled);
        }

        writer.close();
      }
    } catch (Exception e) {
      Logs.logError("FAILED TO UPDATE CACHE: " + e.getMessage(), "PROCESS"); // probably should never fail
    }
  }

  public static boolean getScriptCacheEnabled() {
    return new File(getGameDirectory() + "Nuts/Script/PrecompiledScript.Cache").exists();
  }

  public static void enableScriptCache() {
    if (getScriptCacheEnabled())
      return;

    File target = new File(getGameDirectory() + "Nuts/Script/PrecompiledScript/");
    if (!target.exists())
      target.mkdir();

    Path source = new File(getGameDirectory() + "Nuts/Script/PrecompiledScript.Cache").toPath();

    try {
      Files.move(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      Logs.logError(e.getMessage(), "PROCESS");
    }
  }

  public static void disableScriptCache() {
    if (!getScriptCacheEnabled())
      return;

    File target = new File(getGameDirectory() + "Nuts/Script/");

    Path source = new File(getGameDirectory() + "Nuts/Script/PrecompiledScript/PrecompiledScript.Cache").toPath();

    try {
      Files.move(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      Logs.logError(e.getMessage(), "PROCESS");
    }
  }

  public static void installMod(InputStream stream) {
    try {
      ZipManager.extractFromStream(stream, getGameDirectory() + "Nuts/Script/");
    } catch (Exception e) {
      Logs.logError("could not install mod: " + e.getMessage(), "PROCESS");
    }
  }

  public static void installMod(String path) {
    try {
      installMod(new FileInputStream(new File(path)));
    } catch (Exception e) {
      Logs.logError("could not install mod: " + e.getMessage(), "PROCESS");
    }
  }
}
