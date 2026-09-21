/*
UI manager for the FlatTabbedPane component
*/
package com.turtledsr.launcher.include.ui.styled.tabbedPane;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class FlatTabbedPaneUI extends BasicTabbedPaneUI {
  @Override
  protected void installDefaults() {
    super.installDefaults();
    contentBorderInsets = new Insets(0, 0, 0, 0);
    selectedTabPadInsets = new Insets(0, 0, 0, 0);
    tabAreaInsets = new Insets(0, 0, 0, 0);
  }

  @Override
  protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(StyleManager.dark_background_color);
    g2.fillRect(x, y, w, h);
    g2.dispose();
  }

  @Override
  protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
    /* UNIMPLEMENTED */
  }

  @Override
  protected void layoutLabel(int tabPlacement, FontMetrics metrics, int tabIndex, String title, Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
    super.layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, false);
  }

  @Override
  protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    if(isSelected) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2.setStroke(new BasicStroke(2));
      g2.setColor(StyleManager.selector_underline_color);

      g2.drawLine(x, y + h, x + w, y + h);

      g2.dispose();
    }
  }

  @Override
  protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
    /* UNIMPLEMENTED */
  }
}
