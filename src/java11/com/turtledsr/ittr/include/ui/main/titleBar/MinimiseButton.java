/*
Button used in the title bar to minimise the application
*/

package com.turtledsr.ittr.include.ui.main.titleBar;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.turtledsr.ittr.Main;
import com.turtledsr.ittr.include.ui.helper.ImageManager;
import com.turtledsr.ittr.include.ui.helper.StyleManager;

public final class MinimiseButton extends JButton {
  public MinimiseButton() {
    if(ImageManager.app_close != null) {
      ImageIcon closeIcon = new ImageIcon(ImageManager.app_minimise, "app_minimise");
      setIcon(closeIcon);
    } else {
      setText("app_minimise");
    }

    setFocusable(false);

    setPreferredSize(new Dimension(StyleManager.TITLEBAR_HEIGHT, StyleManager.TITLEBAR_HEIGHT));
    
    setContentAreaFilled(false);
    setOpaque(true);
    setBackground(StyleManager.titleColor);
    setBorder(null);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        setBackground(StyleManager.titleAccent);
      }
      @Override
      public void mouseExited(MouseEvent e) {
        setBackground(StyleManager.titleColor);
      }
      @Override
      public void mousePressed(MouseEvent e) {
        Main.mainFrame.setState(Frame.ICONIFIED);
      }
    });
  }
}
