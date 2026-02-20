package com.turtledsr.launcher.include.engine;

import java.io.File;
import java.io.IOException;

import com.turtledsr.launcher.include.process.Process;

public class SingleInstanceManager {
  static File file;

  public static boolean checkIfAlreadyRunning() throws IOException {
    file = new File(Process.getGameDirectory() + "Launcher/client.lock");
    if (!file.exists()) {
      file.mkdirs();
      file.createNewFile();
    } else {
      return true;
    }

    ShutdownHook shutdownHook = new ShutdownHook();
    Runtime.getRuntime().addShutdownHook(shutdownHook);

    return false;
  }

  public static void unlockFile() {
    file.delete();
  }

  static class ShutdownHook extends Thread {
    public void run() {
      unlockFile();
    }
  }
}
