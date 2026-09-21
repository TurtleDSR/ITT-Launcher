/*
Title Bar for the message box
*/

package com.turtledsr.launcher.include.ui.styled.messageBox.titleBar;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.messageBox.MessageBox;

public final class TitleBar extends JPanel {
  private static JLabel titleLabel;

  private static int yOffset;
  private static int xOffset;

  public TitleBar(MessageBox box) {
    this("", box);
  }

  public TitleBar(String title, MessageBox box) {
    super(new GridBagLayout());
    setPreferredSize(new Dimension(StyleManager.MESSAGE_BOX_SIZE.width, StyleManager.MESSAGE_BOX_TITLEBAR_HEIGHT));
    setBackground(StyleManager.title_accent_dark);

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 4, 0, 0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 1;
    c.gridy = 1;

    titleLabel = new JLabel(title);
    if(FontManager.poppins != null) {
      titleLabel.setFont(FontManager.poppins.deriveFont(Font.BOLD, 13.5f));
    }
    titleLabel.setForeground(StyleManager.foreground_color);

    add(titleLabel, c);

    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx += 1;
    c.weightx = 0;

    add(new ExitButton(box), c);

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
        box.setLocationWithBounds(box.getLocation());
      }
    });

    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
          int x = e.getXOnScreen() - xOffset;
          int y = e.getYOnScreen() - yOffset;

          box.setLocation(x, y);
        }
      }
    });

    
  }
}
