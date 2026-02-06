/*
Panel for displaying log information

Gets its logs from the Logs class functions
*/

package com.turtledsr.launcher.include.ui.debug;

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

import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;

public final class LogPanel extends JPanel {
  public static final int MESSAGE_LIMIT = 10;
  public static String[] logMessages = new String[MESSAGE_LIMIT];

  private static JLabel[] logLabels = new JLabel[MESSAGE_LIMIT];
  private static JLabel title;

  public LogPanel() {
    super(new GridBagLayout());

    setPreferredSize(StyleManager.PANEL_SIZE);
    setBackground(StyleManager.background_color);

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.SOUTH;
    c.weighty = 1;
    c.weightx = 1;

    c.gridheight = 1;

    c.insets = new Insets(0, 10, 3, 5);

    c.gridy = 1;
    c.gridx = 0;

    c.fill = GridBagConstraints.HORIZONTAL;

    add(Box.createHorizontalStrut(StyleManager.PANEL_SIZE.width - 10), c);

    title = new JLabel("Logs:");
    if(FontManager.poppins != null) {
      title.setFont(FontManager.poppins.deriveFont(35.0f));
    }
    title.setForeground(StyleManager.foreground_color);

    add(title, c);
    add(Box.createVerticalStrut(54), c);

    JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
    sep.setForeground(StyleManager.foreground_color);
    sep.setBorder(BorderFactory.createLineBorder(StyleManager.foreground_color, 4));

    add(sep, c);
    
    for (int i = 0; i < MESSAGE_LIMIT; i++) {
      c.gridy++;

      logMessages[i] = "";
      logLabels[i] = new JLabel(logMessages[i]);
      logLabels[i].setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width, 25));
      logLabels[i].setForeground(StyleManager.foreground_color);
      
      if(i == MESSAGE_LIMIT - 1) c.insets = new Insets(0, 10, 15, 8);

      add(Box.createVerticalStrut(25), c);
      add(logLabels[i], c);
    }

    setVisible(false);
  }

  public void log(String message) {
    for (int i = 0; i < MESSAGE_LIMIT - 1; i++) {
      logMessages[i] = logMessages[i + 1];
      logLabels[i].setText(logMessages[i]);
    }

    logMessages[MESSAGE_LIMIT - 1] = message;
    logLabels[MESSAGE_LIMIT - 1].setText(message);
  }

  public static void updateStyle() {
    if(FontManager.poppins != null) {
      title.setFont(FontManager.poppins.deriveFont(35.0f));
    }
    title.setForeground(StyleManager.foreground_color);
  }
}
