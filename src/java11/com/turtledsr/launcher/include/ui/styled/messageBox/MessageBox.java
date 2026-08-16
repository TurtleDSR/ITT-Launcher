/*
Class used to display styled messages to the user

very similar to JOptionPane, just more stylised
*/

package com.turtledsr.launcher.include.ui.styled.messageBox;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.geom.RoundRectangle2D;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.launcher.Window;
import com.turtledsr.launcher.include.ui.styled.button.FlatButton;
import com.turtledsr.launcher.include.ui.styled.messageBox.titleBar.TitleBar;

public class MessageBox extends JDialog {
  public TitleBar titleBar;
  public JTextArea messagePanel;
  public FlatButton[] buttonArray;

  public MessageBox(String message) {
    this("", message, new FlatButton[0]);
  }

  public MessageBox(String title, String message) {
    this(title, message, new FlatButton[0]);
  }

  public MessageBox(String title, String message, FlatButton... buttons) {
    super();
    if(buttons == null) {
      buttonArray = new FlatButton[0];
    } else {
      buttonArray = buttons;
    }

    setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 0;

    titleBar = new TitleBar(title, this);
    add(titleBar, c);
    c.gridy += 1;

    c.insets = new Insets(1, 4, 1, 4);
    c.fill = GridBagConstraints.BOTH; 
    if(buttonArray.length == 0) {
      c.anchor = GridBagConstraints.SOUTH;
      c.weighty = 1;
    }

    messagePanel = new JTextArea(message);
    messagePanel.setEditable(false);
    messagePanel.setLineWrap(true);
    messagePanel.setForeground(StyleManager.foreground_color);
    messagePanel.setBackground(StyleManager.background_hover_color);
    add(messagePanel, c);

    c.gridy += 1;

    if(buttonArray.length != 0) {
      c.weighty = 1;
      c.insets = new Insets(5, 0, 12, 0);
      c.anchor = GridBagConstraints.SOUTH;
      c.fill = GridBagConstraints.HORIZONTAL;

      JPanel buttonPanel = new JPanel(new GridBagLayout());
      buttonPanel.setBackground(StyleManager.background_hover_color);
      GridBagConstraints cc = new GridBagConstraints();
      cc.anchor = GridBagConstraints.CENTER;
      cc.fill = GridBagConstraints.NONE;
      cc.insets = new Insets(0, 0, 0, 0);
      cc.gridx = 0;
      cc.gridy = 0;
      cc.weightx = 1;
      cc.weighty = 1;

      for (FlatButton button : buttonArray) {
        buttonPanel.add(button, cc);
        cc.gridx += 1;
      }

      add(buttonPanel, c);
    }

    setSize(StyleManager.MESSAGE_BOX_SIZE);  
    setLocation((Main.window.getLocation().x + (Main.window.getWidth() / 2)) - (getWidth() / 2), (Main.window.getLocation().y + (Main.window.getHeight() / 2) - (getHeight() / 2)));
    getContentPane().setBackground(StyleManager.background_hover_color);

    setResizable(false);
    setAlwaysOnTop(true);
    setUndecorated(true);
    setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), Window.CORNER_ROUNDING, Window.CORNER_ROUNDING));
    
    setVisible(true);
  }
}
