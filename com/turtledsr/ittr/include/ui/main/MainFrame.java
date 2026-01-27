/*
Main UI Frame

Controls all of the UI, and is the starting point for visual rendering on launch
*/

package com.turtledsr.ittr.include.ui.main;

import javax.swing.JFrame;

import com.turtledsr.ittr.include.ui.debug.LogPanel;
import com.turtledsr.ittr.include.ui.helper.StyleManager;
import com.turtledsr.ittr.include.ui.main.titleBar.TitleBar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public final class MainFrame extends JFrame {
  public static final double CORNER_ROUNDING = 12;

  public static TitleBar titleBar;
  public static LogPanel logPanel;

  public static boolean draggable = true;

  public MainFrame() {
    super();

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;

    titleBar = new TitleBar();

    logPanel = new LogPanel();

    setUndecorated(true);
    getContentPane().setBackground(StyleManager.backgroundColor);

    add(titleBar, c);
    c.gridy += 1;
    add(logPanel, c);

    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setAlwaysOnTop(true);
    setVisible(true);

    addComponentListener(new ComponentAdapter() { //rounded corners
      @Override
      public void componentResized(ComponentEvent e) {
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_ROUNDING, CORNER_ROUNDING));
      }
    });

    pack();
  }
}