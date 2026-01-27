/*
Entry point for the ITT Ranked client
*/

package com.turtledsr.ittr;

import com.turtledsr.ittr.include.process.Process;
import com.turtledsr.ittr.include.timer.Autosplitter;
import com.turtledsr.ittr.include.timer.TimerHandler;
import com.turtledsr.ittr.include.ui.helper.ColorUtils;
import com.turtledsr.ittr.include.ui.helper.FontManager;
import com.turtledsr.ittr.include.ui.helper.ImageManager;
import com.turtledsr.ittr.include.ui.helper.StyleManager;
import com.turtledsr.ittr.include.ui.main.MainFrame;

public final class Main {
  public static final String TITLE = "It Takes Two Ranked";
  public static final float VERSION = 1.0f;

  public static final int RECONNECTION_INTERVAL = 500;
  public static MainFrame mainFrame;

  public static void main(String[] args) throws Exception {
    ImageManager.loadImages();
    FontManager.loadFonts();

    StyleManager.titleColor = ColorUtils.hextoColor("#5f4d36");
    StyleManager.titleAccent = ColorUtils.hextoColor("#4d77a1");

    StyleManager.backgroundColor = ColorUtils.hextoColor("#0d1231");
    StyleManager.foregroundColor = ColorUtils.hextoColor("#e4e4e4");

    StyleManager.app_exit_red = ColorUtils.hextoColor("#a53c3c");
    
    mainFrame = new MainFrame();

    TimerHandler.connect();
    Autosplitter.bind();

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
