/*
Makes sure only one instance of the launcher can run at once
*/

package com.turtledsr.launcher.include.engine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.turtledsr.launcher.include.control.Process;

public class SingleInstanceManager {
  private static File lockFile;
  private static FileLock lock;
  private static FileChannel channel;
  private static RandomAccessFile raf;

  public static boolean checkIfAlreadyRunning() throws IOException {
    File launcherDir = new File(Process.getGameDirectory(), "Launcher/");
    lockFile = new File(launcherDir,"client.lock");

    if (!launcherDir.exists()) {
      launcherDir.mkdirs();
    }

    if(!lockFile.exists()) {
      lockFile.createNewFile();
    }

    try {
      raf = new RandomAccessFile(lockFile, "rw");

      channel = raf.getChannel();
      lock = channel.tryLock();

      if(lock == null) {
        if(channel != null) channel.close();
        if(raf != null) raf.close();
        return true;
      }

    } catch(Exception e) {
      if(channel != null) channel.close();
      if(raf != null) raf.close();
      return true;
    }

    ShutdownHook shutdownHook = new ShutdownHook();
    Runtime.getRuntime().addShutdownHook(shutdownHook);

    return false;
  }

  public static void unlockFile() {
    try{
      if(channel != null) channel.close();
      if(raf != null) raf.close();
    } catch(Exception e) {
      lockFile.delete();
    }
  }

  static class ShutdownHook extends Thread {
    public void run() {
      unlockFile();
    }
  }
}
