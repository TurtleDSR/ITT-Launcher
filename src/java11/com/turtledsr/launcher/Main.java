/*
Entry point for the ITT Ranked client
*/

package com.turtledsr.launcher;

import java.io.InputStream;

import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.control.Autosplitter;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.control.TimerHandler;
import com.turtledsr.launcher.include.engine.ShaderManager;
import com.turtledsr.launcher.include.engine.SingleInstanceManager;
import com.turtledsr.launcher.include.engine.events.EventListener;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.ui.debug.LogPanel;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.main.Window;
import com.turtledsr.launcher.include.ui.main.rootPanels.MainPanel;

public final class Main {
  public static final String TITLE = "It Takes Two Launcher";

  public static final int RECONNECTION_INTERVAL = 500;
  public static boolean scriptCacheEnabled = Process.getScriptCacheEnabled();
  public static boolean lockQueue = false;
  
  public static boolean timerConnected = false;
  public static boolean gameConnected = false;
  
  public static Window window;
  
  public static void main(String[] args) throws Exception {
    if(SingleInstanceManager.checkIfAlreadyRunning()) System.exit(-1); //check if program is already running
    MainPanel.createLogPanel(); //initialize log panel first so we can log things

    SettingsManager.loadSettings();

    ImageManager.loadImages();
    FontManager.loadFonts();
    ShaderManager.extractShaders();
    //LivesplitManager.extractTimer();

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        Process.gameStatus = Process.STARTING;
      }
    }, "game_launched");

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        Process.gameStatus = Process.RUNNING;
      }
    }, "game_connected");

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        Process.gameStatus = Process.STOPPED;
      }
    }, "game_disconnected");

    window = new Window();

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