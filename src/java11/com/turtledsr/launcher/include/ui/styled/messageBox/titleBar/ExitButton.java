/*
Exit button for the message box
*/

package com.turtledsr.launcher.include.ui.styled.messageBox.titleBar;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.button.FlatButton;
import com.turtledsr.launcher.include.ui.styled.messageBox.MessageBox;

public final class ExitButton extends FlatButton {
  public ExitButton(MessageBox box) {
    super(StyleManager.title_accent_dark);
    
    if(ImageManager.ui_app_close != null) {
      ImageIcon closeIcon = new ImageIcon(ImageManager.ui_app_close.getScaledInstance(StyleManager.MESSAGE_BOX_TITLEBAR_HEIGHT - 4, StyleManager.MESSAGE_BOX_TITLEBAR_HEIGHT - 4, Image.SCALE_SMOOTH), "ui_close_message");
      setIcon(closeIcon);
    } else {
      setText("ui_close_message");
    }

    setPreferredSize(new Dimension(StyleManager.MESSAGE_BOX_TITLEBAR_HEIGHT + 5, StyleManager.MESSAGE_BOX_TITLEBAR_HEIGHT));
    setMinimumSize(getPreferredSize());

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        setBackground(StyleManager.app_exit_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setBackground(StyleManager.title_accent_dark);
      }
    });

    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        box.dispose();
      }
    });
  }
}
