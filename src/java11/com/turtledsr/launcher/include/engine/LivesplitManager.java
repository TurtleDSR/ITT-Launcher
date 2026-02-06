/*
Helper class for managing the livesplit timer & the files associated with it
*/

package com.turtledsr.launcher.include.engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.turtledsr.launcher.Main;

public final class LivesplitManager {
  private static final String timerFolderPath = "timer/";
  private static final String splitsFolderPath = timerFolderPath + "splits/";
  public static final String livesplitExecutablePath = timerFolderPath + "livesplit/LiveSplit.exe";

  public static String getLayoutPath(String chapter) {
    return Path.of(splitsFolderPath + chapter + ".lss").toAbsolutePath().toString();
  }

  public static void extractTimer() {
    //check if folder is present, if it is, dont extract
    Path path = Paths.get(timerFolderPath);
    if(Files.exists(path) && Files.isDirectory(path)) {
      Logs.log("timer folder exists, will not extract", "LIVESPLIT_MANAGER");
      return;
    }

    Logs.log("timer folder does not exist, extracting", "LIVESPLIT_MANAGER");
    InputStream in = Main.getResourceAsStream("timer.zip");

    if(in == null) {
      Logs.logError("Could not find ZIP file", "LIVESPLIT_MANAGER");
      Main.lockQueue = true;
      return;
    }

    try{
      ZipManager.extractFromStream(in, timerFolderPath);
    } catch(IOException e) {
      Logs.logError(e.getMessage(), "LIVESPLIT_MANAGER");
    }
  }
}
