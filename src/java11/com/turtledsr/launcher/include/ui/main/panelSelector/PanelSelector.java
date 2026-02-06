/*
The panel that selects which panel you are currently looking at
*/

package com.turtledsr.launcher.include.ui.main.panelSelector;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.engine.Events.EventListener;
import com.turtledsr.launcher.include.engine.Events.EventManager;
import com.turtledsr.launcher.include.process.Process;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.main.MainFrame;
import com.turtledsr.launcher.include.ui.styled.RoundedFlatButton;

public final class PanelSelector extends JPanel {
  private static PanelSelectorButton launcherButton;
  private static PanelSelectorButton logButton;
  private static RoundedFlatButton launchGameButton;

  public PanelSelector() {
    setLayout(new GridBagLayout());

    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width, StyleManager.SELECTOR_HEIGHT));
    setBackground(StyleManager.selector_color);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    c.gridy = 0;

    c.weightx = 0;
    c.weighty = 0;

    if(launcherButton == null) launcherButton = new PanelSelectorButton("Mods", MainFrame.LAUNCHER);
    add(launcherButton, c);

    c.gridx += 1;

    if(logButton == null) logButton = new PanelSelectorButton("Logs", MainFrame.LOGS);
    add(logButton, c);

    c.gridx += 1;
    c.weightx = 1;
    c.weighty = 1;
    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets(0, 0, 0, 10);

    if(launchGameButton == null) launchGameButton = new RoundedFlatButton(!Main.gameConnected ? "Launch" : "Game Running", StyleManager.launch_button_color, 10);
    launchGameButton.setPreferredSize(new Dimension(125, StyleManager.SELECTOR_HEIGHT - 10));
    launchGameButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    launchGameButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        launchGameButton.setBackground(StyleManager.launch_button_hover_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        launchGameButton.setBackground(StyleManager.launch_button_color);
      }
    });

    launchGameButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Process.launchItTakesTwoEx();
      }
    });

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        PanelSelector.launchGameButton.setText("Game Running");
      }
    }, "game_connected");

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        PanelSelector.launchGameButton.setText("Launch");
      }
    }, "game_disconnected");
    
    add(launchGameButton, c);
  }

  public void updateButtons() {
    launcherButton.update();
    logButton.update();
  }
}
