package com.turtledsr.launcher.include.ui.main.titleBar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.FlatButton;

public class SettingsButton extends FlatButton {
  public SettingsButton() {
    super(StyleManager.title_color);

    if(ImageManager.ui_cog != null) {
      ImageIcon cogIcon = new ImageIcon(ImageManager.ui_cog, "ui_cog");
      setIcon(cogIcon);
    } else {
      setText("ui_cog");
    }

    setPreferredSize(new Dimension(StyleManager.TITLEBAR_HEIGHT + 8, StyleManager.TITLEBAR_HEIGHT));

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        setBackground(StyleManager.title_accent);
      }
      @Override
      public void mouseExited(MouseEvent e) {
        setBackground(StyleManager.title_color);
      }
    });

    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
      }
    });
  }
}
