/*
Main UI Frame

Controls all of the UI, and is the starting point for visual rendering on launch
*/

package com.turtledsr.launcher.include.ui.main;

import javax.swing.JFrame;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.ui.debug.LogPanel;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.main.launcher.LauncherPanel;
import com.turtledsr.launcher.include.ui.main.panelSelector.PanelSelector;
import com.turtledsr.launcher.include.ui.main.titleBar.TitleBar;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

public final class MainFrame extends JFrame {
  public static final double CORNER_ROUNDING = 12;

  public static final int LAUNCHER = 0;
  public static final int LOGS = 1;

  public static TitleBar titleBar;
  public static PanelSelector panelSelector;

  public static LauncherPanel launcherPanel;
  public static LogPanel logPanel;

  public static int selectedPanel;

  public static boolean draggable = true;

  public MainFrame() {
    this(LAUNCHER);
  }

  public MainFrame(int selectedPanel) {
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

    if(titleBar == null) titleBar = new TitleBar();
    if(panelSelector == null) panelSelector = new PanelSelector();

    if(launcherPanel == null) launcherPanel = new LauncherPanel();
    if(logPanel == null) logPanel = new LogPanel();

    getContentPane().setBackground(StyleManager.background_color);

    if(Main.SHOW_CUSTOM_TITLEBAR) {
      setUndecorated(true);
      add(titleBar, c);
      c.gridy += 1;
    }

    add(panelSelector, c);
    c.gridy += 1;

    setTitle(Main.TITLE);

    add(launcherPanel, c);
    add(logPanel, c);

    setSelectedPanel(selectedPanel);

    setIconImage(ImageManager.icon);
    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //setAlwaysOnTop(true);
    setVisible(true);

    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    addComponentListener(new ComponentAdapter() { //rounded corners
      @Override
      public void componentResized(ComponentEvent e) {
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_ROUNDING, CORNER_ROUNDING));
      }
    });

    pack();

    setLocation((screen.width / 2) - (getWidth() / 2), (screen.height / 2) - (getHeight() / 2)); //go to center of the screen
  }

  private static void setPanelsInvis() {
    launcherPanel.setVisible(false);
    logPanel.setVisible(false);
  }

  public static void setSelectedPanel(int panel) {
    setPanelsInvis();
    if(panel == LAUNCHER) {
      launcherPanel.setVisible(true);
      launcherPanel.requestFocusInWindow();
    }

    if(panel == LOGS) {
      logPanel.setVisible(true);
      logPanel.requestFocusInWindow();
    }

    selectedPanel = panel;

    panelSelector.updateButtons();
  }

  public static void createLogPanel() {
    logPanel = new LogPanel();
  }
}