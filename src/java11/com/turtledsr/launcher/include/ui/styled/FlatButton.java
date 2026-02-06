/*
Button styled to be modern and flat
*/

package com.turtledsr.launcher.include.ui.styled;

import java.awt.Color;

import javax.swing.Action;

import javax.swing.Icon;
import javax.swing.JButton;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class FlatButton extends JButton {
  public FlatButton() {
    super();
    init();
  }

  public FlatButton(String text) {
    super(text);
    init();
  }

  public FlatButton(String text, Color color) {
    super(text);
    init();
    setBackground(color);
  }

  public FlatButton(Icon icon) {
    super(icon);
    init();
  }

  public FlatButton(String text, Icon icon) {
    super(text, icon);
    init();
  }

  public FlatButton(Action action) {
    super(action);
    init();
  }

  public FlatButton(Color color) {
    super();
    init();
    setBackground(color);
  }

  private void init() {
    setBorder(null);
    setContentAreaFilled(false);
    setOpaque(true);
    setFocusable(false);
    setForeground(StyleManager.foreground_color);
  }
}
