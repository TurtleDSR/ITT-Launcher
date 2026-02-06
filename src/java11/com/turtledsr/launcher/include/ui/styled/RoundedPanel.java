/*
Rounded Jpanel for styled paneling
*/

package com.turtledsr.launcher.include.ui.styled;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

public class RoundedPanel extends JPanel {
  protected int radius = 15;

  public RoundedPanel() {
    super();
    setOpaque(false);
  }

  public RoundedPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
    setOpaque(false);
  }

  public RoundedPanel(LayoutManager layout) {
    super(layout);
    setOpaque(false);
  }

  public RoundedPanel(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
    setOpaque(false);
  }

  public RoundedPanel(int radius) {
    this();
    this.radius = radius;
    setOpaque(false);
  }

  public RoundedPanel(LayoutManager layout, int radius) {
    this(layout);
    this.radius = radius;
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getBackground());
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
    g2.dispose();
  }

  @Override
  protected void paintChildren(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
    g2.clip(clip);
    
    super.paintChildren(g2);
    g2.dispose();
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
    repaint();
  }
}
