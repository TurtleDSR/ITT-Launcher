/*
Settings Management class

Handles opening, reading and updating of the settings json file.
*/

package com.turtledsr.launcher.include.config;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.process.Process;

public final class SettingsManager {
  public static Settings settings;

  private static ObjectMapper mapper = new ObjectMapper();
  private static File settingsFile = new File(Process.getGameDirectory(), "Launcher/settings.json");

  private static ShutdownHook hook;

  public static void loadSettings() {
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    if(hook == null) {
      hook = new ShutdownHook();
      Runtime.getRuntime().addShutdownHook(hook);
    }

    try{
      if(!settingsFile.exists()) {
        settings = new Settings();

        mapper.writeValue(settingsFile, settings);
      } else {
        settings = mapper.readValue(settingsFile, Settings.class);
        if(settings == null) {
          Logs.logError("Failed to read settings: settings is null", "SETTINGS_MANAGER");
          settings = new Settings();
        }
      }
    } catch (Exception e) {
      Logs.logError("Failed to read settings: " + e.getMessage(), "SETTINGS_MANAGER");
      settings = new Settings();
    }
  }

  public static void writeSettings() {
    try {
      mapper.writeValue(settingsFile, settings);
    } catch (Exception e) {
      Logs.logError("Failed to write settings to drive: " + e.getMessage(), "SETTINGS_MANAGER");
    }
  }

  static class ShutdownHook extends Thread {
    public void run() {
      writeSettings();
    }
  }
}
