/*
Button for selecting the panel

Uses a JPanel under the hood for better styling
*/

package com.turtledsr.launcher.include.ui.main.panelSelector;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.main.MainFrame;

public final class PanelSelectorButton extends JPanel implements MouseListener {
  private JLabel label;
  private int boundPanel;

  private JSeparator sep;

  public PanelSelectorButton() {
    this("", -1);
  }

  public PanelSelectorButton(int panel) {
    this("", panel);
  }

  public PanelSelectorButton(String text) {
    this(text, -1);
  }

  public PanelSelectorButton(String text, int panel) {
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    
    setBackground(StyleManager.selector_color);
    setPreferredSize(new Dimension(120, StyleManager.SELECTOR_HEIGHT));
    
    boundPanel = panel;

    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;

    sep = new JSeparator(SwingConstants.HORIZONTAL);
    sep.setForeground(StyleManager.selector_color);
    sep.setBorder(BorderFactory.createLineBorder(StyleManager.selector_color, 10));

    label = new JLabel(text);
    label.setFont(FontManager.poppins.deriveFont(Font.BOLD, 16.5f));
    label.setForeground(StyleManager.foreground_color);

    add(label);

    c.anchor = GridBagConstraints.SOUTH;
    add(sep, c);

    addMouseListener(this);
  }

  public void update() {
    if(MainFrame.selectedPanel == boundPanel) {
      sep.setForeground(StyleManager.selector_underline_color);
      sep.setBorder(BorderFactory.createLineBorder(StyleManager.selector_underline_color, 10));
    } else {
      sep.setForeground(StyleManager.selector_color);
      sep.setBorder(BorderFactory.createLineBorder(StyleManager.selector_color, 10));
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    setBackground(StyleManager.selector_hovered_color);
  }
  
  @Override
  public void mouseExited(MouseEvent e) {
    setBackground(StyleManager.selector_color);
  }

  @Override
  public void mouseReleased(MouseEvent e) { //use mouseReleased to avoid missed clicks
    if(contains(e.getPoint())) {
      if(boundPanel == -1) return;
      MainFrame.setSelectedPanel(boundPanel);
    }
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {/* Unimplemented */}
  
  @Override
  public void mousePressed(MouseEvent e) {/* Unimplemented */}

}
