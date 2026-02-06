/*
Used for logging debug information

Automatically sends the message to the log view panel

Uses Swing Worker to make sure UI is still responsive
*/

package com.turtledsr.launcher.include.engine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.SwingWorker;

import com.turtledsr.launcher.include.ui.main.MainFrame;

public final class Logs {
  public static void log(Object message) {
    MainFrame.logPanel.log("[LOG] " + message.toString() + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    scheduleLog(message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }

  public static void logError(Object message) {
    MainFrame.logPanel.log("[ERROR] " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    scheduleError(message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }

  public static void log(Object message, String instigator) {
    if(!(instigator.equals("AUTOSPLITTER") || instigator.equals("TIMERHANDLER"))) {
      MainFrame.logPanel.log("[" + instigator + "]: " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    scheduleLog("[" + instigator + "]: " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }

  public static void logError(Object message, String instigator) {
    if(!(instigator.equals("AUTOSPLITTER") || instigator.equals("TIMERHANDLER"))) {
      MainFrame.logPanel.log("[ERROR] [" + instigator + "]: " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    scheduleError("[" + instigator + "]: " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }

  private static void scheduleLog(String message) {
    try{new LogWorker(message).execute();} catch(Exception e) {}
  }

  private static void scheduleError(String message) {
    try{new LogWorker(message, true).execute();} catch(Exception e) {}
  }
}

final class LogWorker extends SwingWorker<Void, Void> {
  private String text;
  private boolean error = false;

  public LogWorker(String text) {
    this.text = text;
  }
  public LogWorker(String text, boolean errorMode) {
    this.text = text;
    this.error = errorMode;
  }

  @Override
  protected Void doInBackground() throws Exception {
    if(error) {
      System.err.println(text);
    } else {
      System.out.println(text);
    }
    return null;
  }
}