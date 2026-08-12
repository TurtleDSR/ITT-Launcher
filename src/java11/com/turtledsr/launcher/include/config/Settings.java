/*
Settings class used to load persistent settings into from json
*/

package com.turtledsr.launcher.include.config;

import com.turtledsr.launcher.include.ui.main.rootPanels.MainPanel;

public final class Settings {
  public PersistenceSettings persistenceSettings = new PersistenceSettings();

  public DeveloperSettings developerSettings = new DeveloperSettings();

  public static class PersistenceSettings {
    public Boolean modsEnabled = true;
    public Integer selectedPanel = MainPanel.LAUNCHER;
  }

  public static class DeveloperSettings {
    public Boolean devMode = false;
    public Boolean showCustomTitleBar = true;
  }
}