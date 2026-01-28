/*
Entry point for the ITT Ranked client
*/

package com.turtledsr.ittr;

import javax.swing.JOptionPane;

import com.turtledsr.ittr.include.control.Autosplitter;
import com.turtledsr.ittr.include.control.TimerHandler;
import com.turtledsr.ittr.include.engine.LivesplitManager;
import com.turtledsr.ittr.include.process.Process;
import com.turtledsr.ittr.include.ui.debug.LogPanel;
import com.turtledsr.ittr.include.ui.helper.FontManager;
import com.turtledsr.ittr.include.ui.helper.ImageManager;
import com.turtledsr.ittr.include.ui.main.MainFrame;

public final class Main {
  public static final String TITLE = "It Takes Two Ranked";
  public static final float VERSION = 1.0f;
  public static final boolean SHOW_CUSTOM_TITLEBAR = true;

  public static final int RECONNECTION_INTERVAL = 500;
  public static MainFrame mainFrame;
  public static boolean lockQueue = false;

  public static void main(String[] args) throws Exception {
    if(!checkVersion()) {
      JOptionPane.showMessageDialog(null, "[ERROR]: Invalid Java version, please use Java 11+");
      return;
    }

    MainFrame.createLogPanel(); // initialize log panel first so we can log things

    ImageManager.loadImages();
    FontManager.loadFonts();
    LivesplitManager.extractTimer();

    mainFrame = new MainFrame();

    TimerHandler.connect();
    Autosplitter.bind();

    // update log panel to have correct style
    LogPanel.updateStyle();

    Process.launchLivesplitTimer("shed");

    while (true) {
      tick();
    }
  }

  private static void tick() {
    if (Process.getProcessPID("Livesplit.exe").isEmpty()) {
      if (!TimerHandler.awaitingReconnection) {
        TimerHandler.scheduleReconnect(RECONNECTION_INTERVAL);
      }
    }

    if (Autosplitter.process == null) {
      if (!Autosplitter.awaitingReconnection) {
        Autosplitter.scheduleBind(RECONNECTION_INTERVAL);
      }

      return;
    }

    Autosplitter.tick();
  }

  public static boolean checkVersion() { //checks if version is compatible
    String javaVersion = System.getProperty("java.version");
    if (javaVersion == null) {
      System.err.println("Could not determine Java version from system properties.");
      return true;
    }

    try {
      final int majorVersion;
      if (javaVersion.startsWith("1.")) {
        majorVersion = Integer.parseInt(javaVersion.substring(2, 3));
      } else {
        int dotIndex = javaVersion.indexOf('.');
        String versionNum = (dotIndex == -1) ? javaVersion : javaVersion.substring(0, dotIndex);
        majorVersion = Integer.parseInt(versionNum);
      }

      return !(majorVersion < 11);

    } catch (NumberFormatException e) {
      System.err.println("Error parsing Java version: " + javaVersion);
      e.printStackTrace();
      return true;
    }
  }
}
