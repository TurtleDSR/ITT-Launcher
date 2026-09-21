/*
Shows a classes configs and allows editing.
*/

package com.turtledsr.launcher.include.ui.launcher.configs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.config.gameconfig.GameConfig;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.scrollPane.FlatScrollPane;

public final class ConfigPanel extends FlatScrollPane {
  JPanel p;
  List<GameConfig> configs;

  public ConfigPanel() {
    super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);

    configs = new ArrayList<GameConfig>();

    p = new JPanel(new GridBagLayout());

    p.setBackground(StyleManager.background_color);
    setViewportView(p);
  }

  public void addConfig(GameConfig config) {
    configs.add(config);
    update();
  }

  public void update() {
    p.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(5, 10, 5, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 0;

    for (GameConfig config : configs) {
      p.add(new ConfigEdit(config), c);
      c.gridy += 1;
    }

    c.weighty = 1;
    p.add(new JLabel(), c);

    p.revalidate();
    p.repaint();
  }
}
