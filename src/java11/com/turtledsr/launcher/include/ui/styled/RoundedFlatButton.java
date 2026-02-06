/*
Button styled to be modern and flat
*/

package com.turtledsr.launcher.include.ui.styled;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Action;

import javax.swing.Icon;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class RoundedFlatButton extends FlatButton {
  protected int radius = 15;

  public RoundedFlatButton() {
    super();
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(String text) {
    super(text);
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(String text, Color color) {
    super(text, color);
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(Icon icon) {
    super(icon);
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(String text, Icon icon) {
    super(text, icon);
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(Action action) {
    super(action);
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(Color color) {
    super(color);
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(int radius) {
    super();
    this.radius = radius;
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(String text, int radius) {
    super(text);
    this.radius = radius;
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(String text, Color color, int radius) {
    super(text, color);
    this.radius = radius;
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(Icon icon, int radius) {
    super(icon);
    this.radius = radius;
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(String text, Icon icon, int radius) {
    super(text, icon);
    this.radius = radius;
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(Action action, int radius) {
    super(action);
    this.radius = radius;
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  public RoundedFlatButton(Color color, int radius) {
    super(color);
    this.radius = radius;
    setOpaque(false);
    setForeground(StyleManager.foreground_color);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getBackground());
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
    g2.dispose();
    super.paintComponent(g);
  }

  @Override
  protected void paintChildren(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
    g2.setClip(clip);
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
