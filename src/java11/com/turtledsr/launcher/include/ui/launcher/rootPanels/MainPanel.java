/*
Main Root UI Panel

The most used root panel, controls all the general use tabs and UI
*/

package com.turtledsr.launcher.include.ui.launcher.rootPanels;

import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.launcher.configs.ConfigListPanel;
import com.turtledsr.launcher.include.ui.launcher.debug.LogPanel;
import com.turtledsr.launcher.include.ui.launcher.launch.ModsPanel;
import com.turtledsr.launcher.include.ui.launcher.panelSelector.PanelSelector;
import com.turtledsr.launcher.include.ui.launcher.ranked.RankedPanel;
import com.turtledsr.launcher.include.ui.launcher.tools.ToolsPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public final class MainPanel extends AbstractRootPanel {
  public static final int LAUNCHER = 0;
  public static final int CONFIGS = 1;
  public static final int RANKED = 2;
  public static final int TOOLS = 3;
  public static final int LOGS = 4;

  public static PanelSelector panelSelector;

  public static ModsPanel modsPanel;
  public static RankedPanel rankedPanel;
  public static ConfigListPanel configsPanel;
  public static ToolsPanel toolsPanel;
  public static LogPanel logPanel;

  public static int selectedPanel;

  public MainPanel() {
    this(LAUNCHER);
  }

  public MainPanel(int selectedPanel) {
    super();

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;

    if(panelSelector == null) panelSelector = new PanelSelector();

    if(modsPanel == null) modsPanel = new ModsPanel();
    if(rankedPanel == null) rankedPanel = new RankedPanel();
    if(configsPanel == null) configsPanel = new ConfigListPanel();
    if(toolsPanel == null) toolsPanel = new ToolsPanel();
    if(logPanel == null) logPanel = new LogPanel();

    setBackground(StyleManager.background_color);

    add(panelSelector, c);
    c.gridy += 1;

    add(modsPanel, c);
    add(rankedPanel, c);
    add(configsPanel, c);
    add(toolsPanel, c);
    add(logPanel, c);

    setSelectedPanel(selectedPanel);

    setVisible(true);
  }

  private static void setPanelsInvis() {
    modsPanel.setVisible(false);
    rankedPanel.setVisible(false);
    configsPanel.setVisible(false);
    toolsPanel.setVisible(false);
    logPanel.setVisible(false);
  }

  public static void setSelectedPanel(int panel) {
    setPanelsInvis();
    if(panel == LAUNCHER) {
      modsPanel.setVisible(true);
      modsPanel.requestFocusInWindow();
    }

    if(panel == RANKED) {
      rankedPanel.setVisible(true);
      rankedPanel.requestFocusInWindow();
    }

    if(panel == CONFIGS) {
      configsPanel.setVisible(true);
      configsPanel.requestFocusInWindow();
    }

    if(panel == TOOLS) {
      toolsPanel.setVisible(true);
      toolsPanel.requestFocusInWindow();
    }

    if(panel == LOGS) {
      logPanel.setVisible(true);
      logPanel.requestFocusInWindow();
    }

    selectedPanel = panel;
    SettingsManager.settings.persistenceSettings.selectedPanel = selectedPanel;

    panelSelector.updateButtons();
  }

  public static void createLogPanel() {
    logPanel = new LogPanel();
  }
}