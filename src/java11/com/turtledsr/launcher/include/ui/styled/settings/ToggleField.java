/*
Field for toggling settings, hosts a label and a toggle button.
*/

package com.turtledsr.launcher.include.ui.styled.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.ToggleButton;

public class ToggleField extends JPanel {
  public JLabel label;
  public ToggleButton toggle;

  public ToggleField() { //defaults to no text and toggled off
    this("", false);
  }

  public ToggleField(String text) { //defaults to toggled off
    this(text, false);
  }

  public ToggleField(boolean toggled) { //defaults to no text
    this("", toggled);
  }

  public ToggleField(String text, boolean toggled) {
    super(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 2, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;

    setBackground(StyleManager.background_hover_color);

    label = new JLabel(text);
    label.setForeground(StyleManager.foreground_color);

    add(label, c);
    c.gridx += 1;

    toggle = new ToggleButton(toggled, 1.25f);
    add(toggle, c);
  }
}
