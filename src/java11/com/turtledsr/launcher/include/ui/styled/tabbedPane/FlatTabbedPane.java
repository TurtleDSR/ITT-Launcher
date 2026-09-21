/*
Wrapper for JTabbedPane that styles it to a more modern look.
*/
package com.turtledsr.launcher.include.ui.styled.tabbedPane;

import javax.swing.JTabbedPane;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class FlatTabbedPane extends JTabbedPane {
  public FlatTabbedPane() {
    this(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
  }
  public FlatTabbedPane(int tabPlacement) {
    this(tabPlacement, JTabbedPane.WRAP_TAB_LAYOUT);
  }

  public FlatTabbedPane(int tabPlacement, int tabLayoutPolicy) {
    super(tabPlacement, tabLayoutPolicy);

    setUI(new FlatTabbedPaneUI());
    setBackground(StyleManager.dark_background_color);
    setForeground(StyleManager.foreground_color);
  }
}
