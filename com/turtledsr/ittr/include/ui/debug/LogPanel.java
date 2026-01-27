/*
Panel for displaying log information

Gets its logs from the Logs class functions
*/

package com.turtledsr.ittr.include.ui.debug;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.turtledsr.ittr.include.ui.helper.FontManager;
import com.turtledsr.ittr.include.ui.helper.StyleManager;

public final class LogPanel extends JPanel {
  public static final int MESSAGE_LIMIT = 10;
  public static String[] logMessages = new String[MESSAGE_LIMIT];

  private static JLabel[] logLabels = new JLabel[MESSAGE_LIMIT];

  public LogPanel() {
    super(new GridBagLayout());

    setPreferredSize(new Dimension(610, 280));
    setBackground(StyleManager.backgroundColor);

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.SOUTH;
    c.weighty = 1;
    c.weightx = 1;

    c.gridheight = 1;

    c.insets = new Insets(0, 10, 3, 5);

    c.gridy = 1;
    c.gridx = 0;

    c.fill = GridBagConstraints.HORIZONTAL;

    add(Box.createHorizontalStrut(600), c);

    JLabel label = new JLabel("Logs:");
    label.setFont(FontManager.poppins.deriveFont(30.0f));
    label.setForeground(StyleManager.foregroundColor);

    add(label, c);
    add(Box.createVerticalStrut(40), c);

    JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
    sep.setForeground(StyleManager.foregroundColor);
    sep.setBorder(BorderFactory.createLineBorder(StyleManager.foregroundColor, 2));

    add(sep, c);
    
    for (int i = 0; i < MESSAGE_LIMIT; i++) {
      c.gridy++;

      logMessages[i] = "";
      logLabels[i] = new JLabel(logMessages[i]);
      logLabels[i].setPreferredSize(new Dimension(600, 18));
      logLabels[i].setForeground(StyleManager.foregroundColor);
      
      if(i == MESSAGE_LIMIT - 1) c.insets = new Insets(0, 10, 10, 5);

      add(Box.createVerticalStrut(20), c);
      add(logLabels[i], c);
    }
  }

  public void log(String message) {
    for (int i = 0; i < MESSAGE_LIMIT - 1; i++) {
      logMessages[i] = logMessages[i + 1];
      logLabels[i].setText(logMessages[i]);
    }

    logMessages[MESSAGE_LIMIT - 1] = message;
    logLabels[MESSAGE_LIMIT - 1].setText(message);
  }
}
