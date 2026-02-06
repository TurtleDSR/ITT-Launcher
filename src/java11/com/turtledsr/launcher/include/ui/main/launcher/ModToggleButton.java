package com.turtledsr.launcher.include.ui.main.launcher;

import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.ToggleButton;

public final class ModToggleButton extends ToggleButton {
  ModToggleButton(boolean toggled) {
    super(toggled);
  }

  @Override
  public void update() {
    if(ToggleModsButton.toggled) {
      on.setBackground(StyleManager.toggle_on_color);
      off.setBackground(StyleManager.toggle_off_color);
    } else {
      on.setBackground(StyleManager.mods_toggle_off_color);
      off.setBackground(StyleManager.mods_toggle_off_color);
    }

    if(toggled) {
      on.setVisible(true);
      off.setVisible(false);
    } else {
      off.setVisible(true);
      on.setVisible(false);
    }
  }
} 
