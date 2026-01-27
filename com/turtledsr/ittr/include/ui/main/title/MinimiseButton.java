package com.turtledsr.ittr.include.ui.main.title;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.turtledsr.ittr.Main;
import com.turtledsr.ittr.include.ui.helper.ImageManager;
import com.turtledsr.ittr.include.ui.helper.StyleManager;
import com.turtledsr.ittr.include.ui.main.MainFrame;

public final class MinimiseButton extends JButton {
  public MinimiseButton() {
    if(ImageManager.app_close != null) {
      ImageIcon closeIcon = new ImageIcon(ImageManager.app_minimise, "app_minimise");
      setIcon(closeIcon);

      setFocusable(false);
      setContentAreaFilled(false);
      
      if(MainFrame.titleBar != null) {
        setPreferredSize(new Dimension(MainFrame.titleBar.getHeight(), MainFrame.titleBar.getHeight()));
      } else {
        setPreferredSize(new Dimension(40, 40));
      }

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
    } else {
      setText("app_minimise");
    }
  }
}
