/*
Panel used for the settings menu
*/

package com.turtledsr.launcher.include.ui.launcher.rootPanels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.settings.IntegerField;
import com.turtledsr.launcher.include.ui.styled.settings.ToggleField;

public final class SettingsPanel extends AbstractRootPanel {
  private static Dimension labelSize = new Dimension(130, 22);
  private static Dimension textInputSize = new Dimension(35, 22);

  private static Font categoryFont;
  private static Font inputFont;

  //UI
  public static JLabel uiLabel;
  public static ToggleField preservePositionField;

  //DEV
  public static JLabel developerLabel;
  public static ToggleField checkForUpdatesField;
  public static IntegerField portField;
  public static ToggleField devModeField;

  public SettingsPanel() {
    super();

    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width, (StyleManager.PANEL_SIZE.height + StyleManager.SELECTOR_HEIGHT)));
    setBackground(StyleManager.background_color);

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 3, 3, 12);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.weighty = 0;

    categoryFont = FontManager.poppins.deriveFont(Font.BOLD, 18f);
    inputFont = FontManager.poppins.deriveFont(10f);

    //UI Settings Section
    c.fill = GridBagConstraints.NONE;
    c.gridy = 0;
    c.gridx += 1;

    uiLabel = new JLabel("UI Settings:");
    uiLabel.setForeground(StyleManager.foreground_color);
    uiLabel.setFont(categoryFont);

    add(uiLabel, c);
    c.gridy += 1;

    preservePositionField = new ToggleField("Preserve Position", SettingsManager.settings.uiSettings.preserveWindowPosition);
    preservePositionField.label.setPreferredSize(labelSize);
    preservePositionField.toggle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SettingsManager.settings.uiSettings.preserveWindowPosition = preservePositionField.toggle.toggled;
        if(!SettingsManager.settings.uiSettings.preserveWindowPosition) {
          SettingsManager.settings.persistenceSettings.windowPosition.x = null;
          SettingsManager.settings.persistenceSettings.windowPosition.y = null;
        }
        SettingsManager.writeSettings();
      }
    });
    add(preservePositionField, c);
    c.gridy += 1;

    //seperator
    c.gridy = 0;
    c.gridx += 1;
    c.fill = GridBagConstraints.VERTICAL;
    JPanel sep = new JPanel();
    sep.setPreferredSize(new Dimension(2, 1));
    sep.setBackground(StyleManager.separator_color);
    add(sep, c);
    c.gridx += 1;

    //Developer Settings Section
    developerLabel = new JLabel("Developer Settings:");
    developerLabel.setForeground(StyleManager.foreground_color);
    developerLabel.setFont(categoryFont);

    add(developerLabel, c);
    c.gridy += 1;

    checkForUpdatesField = new ToggleField("Check For Updates", SettingsManager.settings.developerSettings.checkForUpdates);
    checkForUpdatesField.label.setPreferredSize(labelSize);
    checkForUpdatesField.toggle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SettingsManager.settings.developerSettings.checkForUpdates = checkForUpdatesField.toggle.toggled;
        SettingsManager.writeSettings();
      }
    });
    add(checkForUpdatesField, c);
    c.gridy += 1;

    portField = new IntegerField("Socket Port", SettingsManager.settings.developerSettings.socketPort);
    portField.label.setPreferredSize(labelSize);
    portField.input.setPreferredSize(textInputSize);
    portField.input.setFont(inputFont);
    portField.input.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try{
          portField.input.commitEdit();

          int value = ((Number) portField.input.getValue()).intValue();
          if(value >= 1024 && value <= 65535) {
            SettingsManager.settings.developerSettings.socketPort = value;
            EventManager.triggerEvent("Socket_Port_Changed");
            SettingsManager.writeSettings();
          }
        } catch(Exception ex) {
          Logs.logError("Failed to parse socketPort value: " + ex.getLocalizedMessage(), "SettingsPanel");
        } finally {
          requestFocusInWindow();
        }
      }
    });
    add(portField, c);
    c.gridy += 1;

    devModeField = new ToggleField("Developer Mode", SettingsManager.settings.developerSettings.devMode);
    devModeField.label.setPreferredSize(labelSize);
    devModeField.toggle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SettingsManager.settings.developerSettings.devMode = devModeField.toggle.toggled;
        EventManager.triggerEvent("DevMode_Toggled");
        SettingsManager.writeSettings();
      }
    });
    add(devModeField, c);

    //seperator
    c.gridy = 0;
    c.gridx += 1;
    c.fill = GridBagConstraints.VERTICAL;
    sep = new JPanel();
    sep.setPreferredSize(new Dimension(2, 1));
    sep.setBackground(StyleManager.separator_color);
    add(sep, c);
    c.gridx += 1;

    //SPACING
    c.gridx = 10;
    c.gridy = 10;
    c.weightx = 1;
    c.weighty = 1;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.fill = GridBagConstraints.BOTH;
    add(new JLabel(), c);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
          try{
            portField.input.commitEdit();

            int value = ((Number) portField.input.getValue()).intValue();
            if(value >= 1024 && value <= 65535) {
              SettingsManager.settings.developerSettings.socketPort = value;
              EventManager.triggerEvent("Socket_Port_Changed");
              SettingsManager.writeSettings();
            }
          } catch(Exception ex) {
            Logs.logError("Failed to parse socketPort value: " + ex.getLocalizedMessage(), "SettingsPanel");
          } finally {
            requestFocusInWindow();
          }
        }
      }
    });
  }
}
