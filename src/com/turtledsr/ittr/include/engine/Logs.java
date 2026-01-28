/*
Used for logging debug information

Automatically sends the message to the log view panel
*/

package com.turtledsr.ittr.include.engine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.turtledsr.ittr.include.ui.main.MainFrame;

public final class Logs {
  public static void log(Object message) {
    MainFrame.logPanel.log("[LOG] " + message.toString() + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    System.out.println(message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }

  public static void logError(Object message) {
    MainFrame.logPanel.log("[ERROR] " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    System.err.println(message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }

  public static void log(Object message, String instigator) {
    MainFrame.logPanel.log("[" + instigator + "]: " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    System.out.println("[" + instigator + "]: " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }

  public static void logError(Object message, String instigator) {
    MainFrame.logPanel.log("[ERROR] [" + instigator + "]: " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    System.err.println("[" + instigator + "]: " + message + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
  }
}
