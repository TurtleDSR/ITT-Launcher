package com.turtledsr.launcher.include.ui.styled.scrollbar;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class RoundedFlatScrollbarUI extends BasicScrollBarUI {
  public RoundedFlatScrollbarUI() {}

  @Override
  protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(c.getForeground());

    g2.fillRoundRect(thumbBounds.x, thumbBounds.y + (int)(thumbBounds.height * 0.025), thumbBounds.width, (int)(thumbBounds.height * 0.95), 6, 6);
    g2.dispose();
  }

  @Override
  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(c.getBackground());

    g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    g2.dispose();
  }

  @Override
  protected Rectangle getThumbBounds() {
    Rectangle bounds = super.getThumbBounds();

    if(bounds == null || bounds.isEmpty()) {
      return super.getTrackBounds();
    }

    return bounds;
  }

  @Override
  protected JButton createDecreaseButton(int orientation) {
    return createZeroButton();
  }

  @Override
  protected JButton createIncreaseButton(int orientation) {
    return createZeroButton();
  }

  private JButton createZeroButton() {
    JButton button = new JButton();
    button.setPreferredSize(new Dimension(0, 0));
    button.setMinimumSize(new Dimension(0, 0));
    button.setMaximumSize(new Dimension(0, 0));
    return button;
  }
}
