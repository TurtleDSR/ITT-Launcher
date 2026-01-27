package com.turtledsr.ittr.include.ui.main.title;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.turtledsr.ittr.include.engine.Logs;
import com.turtledsr.ittr.include.ui.helper.ImageManager;
import com.turtledsr.ittr.include.ui.helper.StyleManager;
import com.turtledsr.ittr.include.ui.main.MainFrame;

public final class ExitButton extends JButton {
  public ExitButton() {
    if(ImageManager.app_close != null) {
      ImageIcon closeIcon = new ImageIcon(ImageManager.app_close, "app_close");
      setIcon(closeIcon);

      setFocusable(false);

      if(MainFrame.titleBar != null) {
        setPreferredSize(new Dimension(MainFrame.titleBar.getHeight() + 5, MainFrame.titleBar.getHeight()));
      } else {
        setPreferredSize(new Dimension(45, 40));
      }

      setContentAreaFilled(false);
      setOpaque(true);
      setBackground(StyleManager.titleColor);
      setBorder(null);

      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
          setBackground(StyleManager.app_exit_red);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          setBackground(StyleManager.titleColor);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
          Logs.log("CLOSING APPLICATION", "EXITBUTTON");
          System.exit(0);
        }
      });
    } else {
      setText("app_exit");
    }
  }
}
