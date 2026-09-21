/*
Button used in the title bar to exit the application
*/

package com.turtledsr.launcher.include.ui.launcher.titleBar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.button.FlatButton;

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
    setMinimumSize(getPreferredSize());

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
        if(Process.gameStatus == Process.STOPPED && !Main.updating.get()) { //stop launcher if game is stopped
          Logs.log("CLOSING APPLICATION", "EXITBUTTON");
          System.exit(0);
        } else { //if game is running in any capacity hide to tray rather than close
          Logs.log("HIDING APPLICATION", "EXITBUTTON");
          Main.hideWindow();
        }
      }
    });
  }
}
