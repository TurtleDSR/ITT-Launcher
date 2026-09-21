/*
Panel for selecting individual config panels and editing them.
*/

package com.turtledsr.launcher.include.ui.launcher.configs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.turtledsr.launcher.include.config.gameconfig.GameConfig;
import com.turtledsr.launcher.include.config.gameconfig.GameConfigList;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.tabbedPane.FlatTabbedPane;

public final class ConfigListPanel extends JPanel {
  JTabbedPane pane;

  public ConfigListPanel() {
    super(new GridBagLayout());
    setPreferredSize(StyleManager.PANEL_SIZE);
    setBackground(StyleManager.dark_background_color);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;
    
    update();

    add(pane, c);
    setVisible(false);
  }

  public void update() {
    if(pane == null) pane = new FlatTabbedPane(JTabbedPane.LEFT);

    pane.removeAll();

    GameConfigList configs = Process.getGameConfigs();

    for (String fileKey : configs.keySet()) {
      LinkedHashMap<String, ArrayList<GameConfig>> configFile = configs.get(fileKey);
      for(String setKey : configFile.keySet()) {
        ConfigPanel configPanel = new ConfigPanel();
        ArrayList<GameConfig> configSet = configFile.get(setKey);
        for(GameConfig config : configSet) {
          configPanel.addConfig(config);
        }
        pane.addTab(setKey, configPanel);
      }
    }

    revalidate();
    repaint();
  }
}
