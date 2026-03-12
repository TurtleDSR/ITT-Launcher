package com.turtledsr.launcher.include.ui.main.launcher;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JPanel;

import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.process.Process;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.RoundedFlatButton;

public final class LaunchButtonPanel extends JPanel {
  public static RoundedFlatButton launchRankedButton;
  public static ToggleModsButton toggleModsButton;

  public static RoundedFlatButton modsFolderButton;
  public static RoundedFlatButton refreshModsButton;

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

    if(modsFolderButton == null) modsFolderButton = new RoundedFlatButton("Edit Mods", StyleManager.launch_button_color);
    modsFolderButton.setPreferredSize(new Dimension(125, StyleManager.LAUNCH_PANEL_HEIGHT - 15));
    modsFolderButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    modsFolderButton.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        modsFolderButton.setBackground(StyleManager.launch_button_hover_color);
      };

      public void mouseExited(MouseEvent e) {
        modsFolderButton.setBackground(StyleManager.launch_button_color);
      };
    });
    modsFolderButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try{
          Desktop.getDesktop().open(new File(Process.getGameDirectory() + "/Mods/"));
        } catch(Exception ex) {
          Logs.logError("Cannot open mods folder: " + ex.getMessage(), "MODS_FOLDER_BUTTON");
        }
      }
    });

    if(refreshModsButton == null) refreshModsButton = new RoundedFlatButton("Refresh Mods", StyleManager.launch_button_color);
    refreshModsButton.setPreferredSize(new Dimension(125, StyleManager.LAUNCH_PANEL_HEIGHT - 15));
    refreshModsButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    refreshModsButton.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        refreshModsButton.setBackground(StyleManager.launch_button_hover_color);
      };

      public void mouseExited(MouseEvent e) {
        refreshModsButton.setBackground(StyleManager.launch_button_color);
      };
    });
    refreshModsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ModsPanel.refreshMods();
      }
    });

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 10, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.weighty = 1;

    add(modsFolderButton, c);
    c.gridx += 1;

    add(refreshModsButton, c);
    c.gridx += 1;

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
