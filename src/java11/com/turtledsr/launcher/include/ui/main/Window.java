/*
UI Window

Controls all of the UI, and is the starting point for visual rendering on launch
*/

package com.turtledsr.launcher.include.ui.main;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.ui.helper.ImageManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.main.rootPanels.MainPanel;
import com.turtledsr.launcher.include.ui.main.rootPanels.SettingsPanel;
import com.turtledsr.launcher.include.ui.main.titleBar.TitleBar;

public final class Window extends JFrame {
  public static final double CORNER_ROUNDING = 12;

  public static TitleBar titleBar;
  
  public static boolean draggable = true;

  public static int activePanel = 0;
  public static int lastActivePanel = 0;

  public static MainPanel mainPanel;
  public static SettingsPanel settingsPanel;

  public static final int MAIN = 0;
  public static final int SETTINGS = 1;

  public Window() {
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

    getContentPane().setBackground(StyleManager.background_color);

    if(SettingsManager.settings.developerSettings.showCustomTitleBar) {
      setUndecorated(true);
      add(titleBar, c);
      c.gridy += 1;
    }

    if(mainPanel == null) mainPanel = new MainPanel(SettingsManager.settings.persistenceSettings.selectedPanel);
    add(mainPanel, c);
    c.gridy += 1;

    if(settingsPanel == null) settingsPanel = new SettingsPanel();
    add(settingsPanel, c);

    setIconImage(ImageManager.icon);
    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setAlwaysOnTop(false);
    
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    
    addComponentListener(new ComponentAdapter() { //rounded corners
      @Override
      public void componentResized(ComponentEvent e) {
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), Window.CORNER_ROUNDING, Window.CORNER_ROUNDING));
      }
    });
    
    setTitle(Main.TITLE);
    
    setActivePanel(MAIN);

    setVisible(true);
    
    pack();
    
    setLocation((screen.width / 2) - (getWidth() / 2), (screen.height / 2) - (getHeight() / 2)); //go to center of the screen
  }

  private static void setPanelsInvis() {
    mainPanel.setVisible(false);
    settingsPanel.setVisible(false);
  }

  public static void setActivePanel(int panel) {
    lastActivePanel = activePanel;
    setPanelsInvis();
    if(panel == MAIN) {
      mainPanel.setVisible(true);
      mainPanel.requestFocusInWindow();
    }

    if(panel == SETTINGS) {
      settingsPanel.setVisible(true);
      settingsPanel.requestFocusInWindow();
    }

    activePanel = panel;
  }
}
