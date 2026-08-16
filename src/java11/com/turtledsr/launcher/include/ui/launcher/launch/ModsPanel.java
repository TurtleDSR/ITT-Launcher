/*
Main panel for the game/livesplit launcher
*/

package com.turtledsr.launcher.include.ui.launcher.launch;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public final class ModsPanel extends JPanel {
  public static ModsToolbar launchButtonPanel;
  public static ModsListPanel modsPanel;

  public ModsPanel() {
    super(new GridBagLayout());

    setPreferredSize(StyleManager.PANEL_SIZE);
    setBackground(StyleManager.background_color);

    if(launchButtonPanel == null) launchButtonPanel = new ModsToolbar();
    if(modsPanel == null) modsPanel = new ModsListPanel();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 0;

    add(launchButtonPanel, c);

    c.anchor = GridBagConstraints.SOUTH;
    c.insets = new Insets(0, 10, 5, 10);
    c.gridy += 1;

    JPanel sep = new JPanel();
    sep.setPreferredSize(new Dimension(1, 2));
    sep.setBackground(StyleManager.separator_color);
    add(sep, c);

    c.anchor = GridBagConstraints.NORTHWEST;
    c.gridy += 1;
    c.insets = new Insets(0, 0, 0, 0);
    add(modsPanel, c);

    setVisible(false);
  }
}
