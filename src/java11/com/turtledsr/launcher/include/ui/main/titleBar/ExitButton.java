/*
Button used in the title bar to exit the application
*/

package com.turtledsr.launcher.include.ui.main.titleBar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.FlatButton;

public final class ExitButton extends FlatButton {
  public ExitButton() {
    super(StyleManager.title_color);
    
    if(ImageManager.ui_app_close != null) {
      ImageIcon closeIcon = new ImageIcon(ImageManager.ui_app_close, "ui_app_close");
      setIcon(closeIcon);
    } else {
      setText("ui_app_exit");
    }

    setPreferredSize(new Dimension(StyleManager.TITLEBAR_HEIGHT + 8, StyleManager.TITLEBAR_HEIGHT));

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        setBackground(StyleManager.app_exit_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setBackground(StyleManager.title_color);
      }
    });

    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Logs.log("CLOSING APPLICATION", "EXITBUTTON");
        System.exit(0);
      }
    });
  }
}
