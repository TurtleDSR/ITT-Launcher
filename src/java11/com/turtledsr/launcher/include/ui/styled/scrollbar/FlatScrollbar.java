package com.turtledsr.launcher.include.ui.styled.scrollbar;

import java.awt.Dimension;

import javax.swing.JScrollBar;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class FlatScrollbar extends JScrollBar {
  public FlatScrollbar() {
    setUI(new FlatScrollbarUI());
    setOpaque(false);

    setForeground(StyleManager.separator_color);
    setBackground(StyleManager.background_color);
    setPreferredSize(new Dimension(5, 0));
  }
}
