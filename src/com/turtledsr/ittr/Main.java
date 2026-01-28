/*
Entry point for the ITT Ranked client
*/

package com.turtledsr.ittr;

import com.turtledsr.ittr.include.engine.LivesplitManager;
import com.turtledsr.ittr.include.process.Process;
import com.turtledsr.ittr.include.timer.Autosplitter;
import com.turtledsr.ittr.include.timer.TimerHandler;
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
    MainFrame.createLogPanel(); //initialize log panel first so we can log things

    ImageManager.loadImages();
    FontManager.loadFonts();
    LivesplitManager.extractTimer();

    mainFrame = new MainFrame();

    TimerHandler.connect();
    Autosplitter.bind();

    Process.launchLivesplitTimer("snowglobe");

    //update log panel to have correct style
    LogPanel.updateStyle();

    while(true) {
      tick();
    }
  }

  private static void tick() {
    if(Process.getProcessPID("Livesplit.exe").isEmpty()) {
      if(!TimerHandler.awaitingReconnection) {
        TimerHandler.scheduleReconnect(RECONNECTION_INTERVAL);
      }
    }

    if(Autosplitter.process == null) {
      if(!Autosplitter.awaitingReconnection) {
        Autosplitter.scheduleBind(RECONNECTION_INTERVAL);
      }

      return;
    }

    Autosplitter.tick();
  }
}
