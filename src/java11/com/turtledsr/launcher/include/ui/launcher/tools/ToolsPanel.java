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
import com.turtledsr.launcher.include.engine.ShaderManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.button.RoundedFlatButton;

public final class ToolsPanel extends JPanel {
  public static RoundedFlatButton shaderButton;
  public static RoundedFlatButton updateCheckButton;

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

    if(shaderButton == null) shaderButton = new RoundedFlatButton("Install DX12/Shaders", StyleManager.tool_button_color);
    shaderButton.setPreferredSize(new Dimension(250, StyleManager.SELECTOR_HEIGHT - 10));
    shaderButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    shaderButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        shaderButton.setBackground(StyleManager.tool_button_hover_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        shaderButton.setBackground(StyleManager.tool_button_color);
      }
    });
    shaderButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ShaderManager.installDX12();
      }
    });
    add(shaderButton, c);

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
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    add(new JLabel(), c);

    setVisible(false);
  }
}
