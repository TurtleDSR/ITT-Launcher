/*
Styled wrapper for JScrollPane to give it a much more modern look.
*/

package com.turtledsr.launcher.include.ui.styled.scrollPane;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.scrollbar.RoundedFlatScrollbar;

public class FlatScrollPane extends JScrollPane {
  public FlatScrollPane() {
    this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

  public FlatScrollPane(Component view) {
    this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

  public FlatScrollPane(int vsbPolicy, int hsbPolicy) {
    this(null, vsbPolicy, hsbPolicy);
  }

  public FlatScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
    super(view, vsbPolicy, hsbPolicy);
    setVerticalScrollBar(new RoundedFlatScrollbar());
    getVerticalScrollBar().setUnitIncrement(16);
    setHorizontalScrollBar(new RoundedFlatScrollbar());
    getHorizontalScrollBar().setUnitIncrement(16);

    setBackground(StyleManager.background_color);
    setBorder(BorderFactory.createLineBorder(StyleManager.background_color, 0));
  }
}
