package com.turtledsr.launcher.include.ui.main.launcher;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.RoundedFlatButton;

public final class LaunchButtonPanel extends JPanel {
  public static RoundedFlatButton launchRankedButton;
  public static ToggleModsButton toggleModsButton;

  public LaunchButtonPanel() {
    super(new GridBagLayout());
    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width, StyleManager.LAUNCH_PANEL_HEIGHT));
    setBackground(StyleManager.background_color);

    //if(launchRankedButton == null) launchRankedButton = new RoundedFlatButton("Play Ranked", StyleManager.launch_button_color, 10);
    //launchRankedButton.setPreferredSize(new Dimension(125, StyleManager.LAUNCH_PANEL_HEIGHT - 15));
    //launchRankedButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    //launchRankedButton.addMouseListener(new MouseAdapter() {
    //  @Override
    //  public void mouseEntered(MouseEvent e) {
    //    launchRankedButton.setBackground(StyleManager.launch_button_hover_color);
    //  }
    //
    //  @Override
    //  public void mouseExited(MouseEvent e) {
    //    launchRankedButton.setBackground(StyleManager.launch_button_color);
    //  }
    //});

    if(toggleModsButton == null) toggleModsButton = new ToggleModsButton();
    toggleModsButton.setPreferredSize(new Dimension(125, StyleManager.LAUNCH_PANEL_HEIGHT - 15));
    toggleModsButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 10, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.weighty = 1;

    if(launchRankedButton != null) {
      add(launchRankedButton, c);
      c.gridx += 1;
    }

    c.insets = new Insets(0, 10, 0, 10);
    c.weightx = 1;
    c.anchor = GridBagConstraints.EAST;
    add(toggleModsButton, c);
  }
}
