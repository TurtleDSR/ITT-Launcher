package com.turtledsr.launcher.include.ui.launcher.ranked;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class RankedPanel extends JPanel {
  public RankedPanel() {
    super(new GridBagLayout());

    setPreferredSize(StyleManager.PANEL_SIZE);
    setBackground(StyleManager.background_color);
  }
}
