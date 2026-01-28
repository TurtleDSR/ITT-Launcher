/*
Helper class for managing processes
*/

package com.turtledsr.ittr.include.process;

import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.turtledsr.ittr.include.engine.LivesplitManager;
import com.turtledsr.ittr.include.engine.Logs;

public final class Process {
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
    // TH32CS_SNAPMODULE32 is essential even on 64-bit to see the main EXE module
    HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new DWORD(pid));

    if (snapshot == Kernel32.INVALID_HANDLE_VALUE)
      return 0;

    Tlhelp32.MODULEENTRY32W entry = new Tlhelp32.MODULEENTRY32W();
    try {
      if (Kernel32.INSTANCE.Module32FirstW(snapshot, entry)) {
        // The very first module in the list is ALWAYS the .exe itself
        return Pointer.nativeValue(entry.modBaseAddr);
      }
    } finally {
      Kernel32.INSTANCE.CloseHandle(snapshot);
    }
    return 0;
  }

  public static boolean launchItTakesTwo() { // returns status
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
      return true;
    } catch (Exception e) {
      Logs.logError(e.getMessage(), "PROCESS");
      return false;
    }
  }

  public static boolean launchLivesplitTimer(String chapter) { // returns status
    OptionalInt pid = getProcessPID("LiveSplit.exe");
    while (pid.isPresent()) { // close livesplit if its open
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
}
