package com.turtledsr.launcher.include.ui.styled.scrollbar;

import java.awt.Dimension;

import javax.swing.JScrollBar;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class RoundedFlatScrollbar extends JScrollBar {
  public RoundedFlatScrollbar() {
    setUI(new RoundedFlatScrollbarUI());
    setOpaque(false);

    setForeground(StyleManager.separator_color);
    setBackground(StyleManager.background_color);
    setPreferredSize(new Dimension(5, 0));
  }
}
