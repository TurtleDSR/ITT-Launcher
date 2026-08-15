/*
Panel used for the settings menu
*/

package com.turtledsr.launcher.include.ui.launcher.rootPanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.settings.ToggleField;

public final class SettingsPanel extends AbstractRootPanel {
  private static Dimension fieldSize = new Dimension(130, 22);

  public static ToggleField devModeField;

  public SettingsPanel() {
    super();

    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width, StyleManager.PANEL_SIZE.height + StyleManager.SELECTOR_HEIGHT));
    setBackground(StyleManager.background_color);

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 0, 3, 3);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;

    devModeField = new ToggleField("Developer Mode", SettingsManager.settings.developerSettings.devMode);
    devModeField.label.setPreferredSize(fieldSize);
    devModeField.toggle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SettingsManager.settings.developerSettings.devMode = devModeField.toggle.toggled;
        EventManager.triggerEvent("DevMode_Toggled");
        SettingsManager.writeSettings();
      }
    });
    add(devModeField, c);

    c.gridy += 1;
  }
}
