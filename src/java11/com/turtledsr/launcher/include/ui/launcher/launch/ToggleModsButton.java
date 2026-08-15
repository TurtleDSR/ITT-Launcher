/*
Button for toggling mods on and off
*/
package com.turtledsr.launcher.include.ui.launcher.launch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.turtledsr.launcher.include.config.SettingsManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.button.RoundedFlatButton;

public final class ToggleModsButton extends RoundedFlatButton implements ActionListener, MouseListener {
  public static boolean toggled = SettingsManager.settings.persistenceSettings.modsEnabled;
  private static boolean hovered = false;

  public ToggleModsButton() {
    super("Disable Mods", StyleManager.toggle_mods_on_color, 10);
    update();

    addActionListener(this);
    addMouseListener(this);
  }

  public void update() {
    if(toggled) {
      setText("Disable Mods");
      setBackground(!hovered ? StyleManager.toggle_mods_on_color : StyleManager.toggle_mods_on_hover_color);
    } else {
      setText("Enable Mods");
      setBackground(!hovered ? StyleManager.toggle_mods_off_color : StyleManager.toggle_mods_off_hover_color);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    toggled = !toggled;

    SettingsManager.settings.persistenceSettings.modsEnabled = toggled;
    SettingsManager.writeSettings();

    update();
    ModsPanel.updateModList();
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    hovered = true;
    update();
  }

  @Override
  public void mouseExited(MouseEvent e) {
    hovered = false;
    update();
  }

  @Override
  public void mouseClicked(MouseEvent e) {/*unimplemented*/}
  @Override
  public void mousePressed(MouseEvent e) {/*unimplemented*/}
  @Override
  public void mouseReleased(MouseEvent e) {/*unimplemented*/}
}
