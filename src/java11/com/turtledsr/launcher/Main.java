/*
Entry point for the ITT Ranked client
*/

package com.turtledsr.launcher;

import java.io.InputStream;

import com.turtledsr.launcher.include.control.Autosplitter;
import com.turtledsr.launcher.include.control.TimerHandler;
import com.turtledsr.launcher.include.engine.SingleInstanceManager;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.process.Process;
import com.turtledsr.launcher.include.ui.debug.LogPanel;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.main.MainFrame;

public final class Main {
  public static final String TITLE = "It Takes Two Launcher";
  public static final float VERSION = 1.0f;
  public static final boolean SHOW_CUSTOM_TITLEBAR = true;

  public static final int RECONNECTION_INTERVAL = 500;
  public static boolean scriptCacheEnabled = Process.getScriptCacheEnabled();
  public static MainFrame mainFrame;
  public static boolean lockQueue = false;

  public static boolean timerConnected = false;
  public static boolean gameConnected = false;

  public static void main(String[] args) throws Exception {
    if(SingleInstanceManager.checkIfAlreadyRunning()) System.exit(-1); //check if program is already running

    MainFrame.createLogPanel(); //initialize log panel first so we can log things

    ImageManager.loadImages();
    FontManager.loadFonts();
    //LivesplitManager.extractTimer();

    mainFrame = new MainFrame();

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

  public static InputStream getResourceAsStream(String path) {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
  }
}