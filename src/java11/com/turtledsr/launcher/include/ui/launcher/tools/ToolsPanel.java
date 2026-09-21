/*
Panel that holds tools for the game
*/

package com.turtledsr.launcher.include.ui.launcher.tools;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.engine.DXManager;
import com.turtledsr.launcher.include.engine.ReshadeManager;
import com.turtledsr.launcher.include.engine.events.EventListener;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.button.RoundedFlatButton;

public final class ToolsPanel extends JPanel {
  public static RoundedFlatButton dxButton;
  public static RoundedFlatButton reshadeButton;
  public static RoundedFlatButton updateCheckButton;
  public static RoundedFlatButton cleanScriptsButton;

  //developer tools

  public ToolsPanel() {
    super(new GridBagLayout());

    setPreferredSize(StyleManager.PANEL_SIZE);
    setBackground(StyleManager.background_color);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(5, 5, 5, 5);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 0;

    if(dxButton == null) dxButton = new RoundedFlatButton("Install DX12/Shaders", StyleManager.tool_button_color);
    dxButton.setPreferredSize(new Dimension(250, StyleManager.SELECTOR_HEIGHT - 10));
    dxButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    dxButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        dxButton.setBackground(StyleManager.tool_button_hover_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        dxButton.setBackground(StyleManager.tool_button_color);
      }
    });
    dxButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DXManager.installDX12();
      }
    });
    add(dxButton, c);
    c.gridy += 1;

    if(reshadeButton == null) reshadeButton = new RoundedFlatButton("Install Reshade", StyleManager.tool_button_color);
    reshadeButton.setPreferredSize(new Dimension(250, StyleManager.SELECTOR_HEIGHT - 10));
    reshadeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    reshadeButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        reshadeButton.setBackground(StyleManager.tool_button_hover_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        reshadeButton.setBackground(StyleManager.tool_button_color);
      }
    });
    reshadeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ReshadeManager.installReshade();
      }
    });
    add(reshadeButton, c);
    c.gridy += 1;

    if(updateCheckButton == null) updateCheckButton = new RoundedFlatButton("Check for updates", StyleManager.tool_button_color);
    updateCheckButton.setPreferredSize(new Dimension(250, StyleManager.SELECTOR_HEIGHT - 10));
    updateCheckButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    updateCheckButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        updateCheckButton.setBackground(StyleManager.tool_button_hover_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        updateCheckButton.setBackground(StyleManager.tool_button_color);
      }
    });
    updateCheckButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Main.checkForUpdates();
      }
    });
    add(updateCheckButton, c);
    c.gridy += 1;

    if(cleanScriptsButton == null) cleanScriptsButton = new RoundedFlatButton("Clean Scripts", StyleManager.tool_button_color);
    cleanScriptsButton.setPreferredSize(new Dimension(250, StyleManager.SELECTOR_HEIGHT - 10));
    cleanScriptsButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    cleanScriptsButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        cleanScriptsButton.setBackground(StyleManager.tool_button_hover_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        cleanScriptsButton.setBackground(StyleManager.tool_button_color);
      }
    });
    cleanScriptsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Process.cleanScriptsAsync();
      }
    });
    add(cleanScriptsButton, c);
    c.gridy += 1;

    if(!SettingsManager.settings.developerSettings.devMode) {
    }
    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
      }
    }, "DevMode_Toggled");

    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    add(new JLabel(), c);

    setVisible(false);
  }
}
