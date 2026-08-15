/*
Field for int settings, hosts a label and a integer text field.
*/

package com.turtledsr.launcher.include.ui.styled.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class IntegerField extends JPanel {
  public JLabel label;
  public JFormattedTextField input;

  public IntegerField() { //defaults to no text and value of 0
    this("", 0);
  }

  public IntegerField(String text) { //defaults to value of 0
    this(text, 0);
  }

  public IntegerField(int value) { //defaults to no text
    this("", value);
  }

  public IntegerField(String text, int value) {
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

    NumberFormat format = NumberFormat.getIntegerInstance();
    format.setGroupingUsed(false);

    input = new JFormattedTextField(format);
    input.setValue(value);

    add(input, c);
  }
}
