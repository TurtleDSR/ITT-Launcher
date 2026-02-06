/*
Main panel for the game/livesplit launcher
*/

package com.turtledsr.launcher.include.ui.main.launcher;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public final class LauncherPanel extends JPanel {
  public static LaunchButtonPanel launchButtonPanel;
  public static ModsPanel modsPanel;

  public LauncherPanel() {
    super(new GridBagLayout());

    setPreferredSize(StyleManager.PANEL_SIZE);
    setBackground(StyleManager.background_color);

    if(launchButtonPanel == null) launchButtonPanel = new LaunchButtonPanel();
    if(modsPanel == null) modsPanel = new ModsPanel();

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
