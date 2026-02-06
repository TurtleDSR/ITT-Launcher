/*
Button used in the title bar to minimise the application
*/

package com.turtledsr.launcher.include.ui.main.titleBar;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.ImageIcon;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.FlatButton;

public final class MinimiseButton extends FlatButton {
  public MinimiseButton() {
    super(StyleManager.title_color);

    if(ImageManager.ui_app_minimise != null) {
      ImageIcon minimiseIcon = new ImageIcon(ImageManager.ui_app_minimise, "ui_app_minimise");
      setIcon(minimiseIcon);
    } else {
      setText("ui_app_minimise");
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
        Main.mainFrame.setState(Frame.ICONIFIED);
      }
    });
  }
}
