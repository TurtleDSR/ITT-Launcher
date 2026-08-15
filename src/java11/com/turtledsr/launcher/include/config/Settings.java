/*
Settings class used to load persistent settings into from json
*/

package com.turtledsr.launcher.include.config;

import com.turtledsr.launcher.include.ui.launcher.rootPanels.MainPanel;

public final class Settings {
  public PersistenceSettings persistenceSettings = new PersistenceSettings();

  public UISettings uiSettings = new UISettings();

  public DeveloperSettings developerSettings = new DeveloperSettings();

  public static class PersistenceSettings {
    public boolean modsEnabled = true;
    public int selectedPanel = MainPanel.LAUNCHER;
    public WindowPosition windowPosition = new WindowPosition();

    public static class WindowPosition {
      public Integer x = null; //null means to go to center of the screen
      public Integer y = null;
    }
  }

  public static class UISettings {
    public int UIScale = 1;
    public boolean preserveWindowPosition = true;
  }

  public static class DeveloperSettings {
    public boolean checkForUpdates = true;
    public boolean devMode = false;
    public boolean showCustomTitleBar = true;
    public int socketPort = 41000;
  }
}