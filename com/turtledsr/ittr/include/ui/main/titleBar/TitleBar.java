/*
Title Bar UI Panel
*/

package com.turtledsr.ittr.include.ui.main.titleBar;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.ittr.Main;
import com.turtledsr.ittr.include.ui.helper.FontManager;
import com.turtledsr.ittr.include.ui.helper.StyleManager;
import com.turtledsr.ittr.include.ui.main.MainFrame;

public final class TitleBar extends JPanel {
  private static JLabel titleLabel;

  private static int yOffset;
  private static int xOffset;

  public TitleBar() {
    super(new GridBagLayout());
    setPreferredSize(new Dimension(610, 35));
    setBackground(StyleManager.titleColor);

    GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 10, 0, 0);
    c.weightx = 15;
    c.weighty = 1;
    c.gridx = 1;
    c.gridy = 1;

    titleLabel = new JLabel(Main.TITLE);
    titleLabel.setFont(FontManager.poppins.deriveFont(Font.BOLD, 20.0f));
    titleLabel.setForeground(StyleManager.foregroundColor);

    add(titleLabel, c);

    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets(0, 0, 0, 0);
    c.weightx = 1;
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
    });

    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if(MainFrame.draggable && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) Main.mainFrame.setLocation(e.getXOnScreen() - xOffset, e.getYOnScreen() - yOffset);
      }
    });
  }
}
