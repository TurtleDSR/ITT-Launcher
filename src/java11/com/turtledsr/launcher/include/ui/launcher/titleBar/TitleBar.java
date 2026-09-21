/*
Title Bar UI Panel
*/

package com.turtledsr.launcher.include.ui.launcher.titleBar;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.config.Settings.PersistenceSettings;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.launcher.Window;

public final class TitleBar extends JPanel {
  private static JLabel titleLabel;

  private static int yOffset;
  private static int xOffset;

  public TitleBar() {
    super(new GridBagLayout());
    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width, StyleManager.TITLEBAR_HEIGHT));
    setBackground(StyleManager.title_color);

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 10, 0, 0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 1;
    c.gridy = 1;

    titleLabel = new JLabel(Main.TITLE);
    if(FontManager.poppins != null) {
      titleLabel.setFont(FontManager.poppins.deriveFont(Font.BOLD, 18.0f));
    }
    titleLabel.setForeground(StyleManager.foreground_color);

    add(titleLabel, c);

    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx += 1;
    c.weightx = 0;

    add(new SettingsButton(), c);
    c.gridx += 1;

    add(new MinimiseButton(), c);
    c.gridx += 1;

    add(new ExitButton(), c);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
          yOffset = e.getY();
          xOffset = e.getX();
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
          Main.window.setLocationWithBounds(Main.window.getLocation());

          if(SettingsManager.settings.uiSettings.preserveWindowPosition) {
            if(SettingsManager.settings.persistenceSettings.windowPosition == null) SettingsManager.settings.persistenceSettings.windowPosition = new PersistenceSettings.WindowPosition();
            SettingsManager.settings.persistenceSettings.windowPosition.x = Main.window.getLocation().x;
            SettingsManager.settings.persistenceSettings.windowPosition.y = Main.window.getLocation().y;
            SettingsManager.writeSettings();
          }
        }
      }
    });

    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if(Window.draggable && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
          int x = e.getXOnScreen() - xOffset;
          int y = e.getYOnScreen() - yOffset;

          Main.window.setLocation(x, y);
        }
      }
    });
  }
}
