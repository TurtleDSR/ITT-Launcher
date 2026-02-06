/*
Toggle button class,
The typical switch button
*/

package com.turtledsr.launcher.include.ui.styled;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.StyleManager;

public class ToggleButton extends JPanel implements MouseListener {
  public boolean toggled;

  protected ActionListener actionListener;

  protected JPanel on;
  protected JPanel off;

  public ToggleButton() {
    this(false);
  }

  public ToggleButton(boolean toggled) {
    super(new GridBagLayout());

    setPreferredSize(new Dimension(30, 18));
    setBackground(StyleManager.toggle_background_color);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(2, 2, 2, 0);
    c.weightx = 1;
    c.gridx = 0;
    c.gridy = 0;

    on = new JPanel();
    on.setPreferredSize(new Dimension((int) (getPreferredSize().width / 2) - (c.insets.left * 2), getPreferredSize().height - (c.insets.top * 2)));
    on.setBackground(StyleManager.toggle_on_color);

    off = new JPanel();
    off.setPreferredSize(new Dimension((int) (getPreferredSize().width / 2) - (c.insets.left * 2), getPreferredSize().height - (c.insets.top * 2)));
    off.setBackground(StyleManager.toggle_off_color);

    add(off, c);
    add(new JLabel(), c); //weight

    c.anchor = GridBagConstraints.EAST;
    c.gridx++;

    c.insets = new Insets(c.insets.top, c.insets.right, c.insets.bottom, c.insets.left); //swap left and right insets

    add(on, c);
    add(new JLabel(), c); //weight

    if(toggled) {
      off.setVisible(false);
    } else {
      on.setVisible(false);
    }

    this.toggled = toggled;
    addMouseListener(this);
  }

  public void update() {
    if(toggled) {
      on.setVisible(true);
      off.setVisible(false);
    } else {
      off.setVisible(true);
      on.setVisible(false);
    }
  }

  public void addActionListener(ActionListener l) {
    actionListener = l;
  }

  @Override
  public void mouseReleased(MouseEvent e) { //use mouseReleased to avoid missed clicks
    if(contains(e.getPoint())) {
      toggled = !toggled;
      update();
    }
    actionListener.actionPerformed(new ActionEvent(this, 0, "Toggle"));
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {/* Unimplemented */}
  
  @Override
  public void mousePressed(MouseEvent e) {/* Unimplemented */}

  @Override
  public void mouseEntered(MouseEvent e) {/* Unimplemented */}
  
  @Override
  public void mouseExited(MouseEvent e) {/* Unimplemented */}
}