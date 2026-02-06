package com.turtledsr.launcher.include.ui.styled;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.ui.helper.ImageManager;

public class DirectionPanel extends JPanel {
  protected JButton up;
  protected JButton down;
  protected ActionListener listener;

  public DirectionPanel() {
    super(new GridBagLayout());

    setPreferredSize(new Dimension(20, 20));
    setOpaque(false);
    setBackground(new Color(0xFFFFFFFF));

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    up = new JButton(new ImageIcon(ImageManager.ui_up_arrow));
    up.setBorder(null);
    up.setPreferredSize(new Dimension(20, 10));
    up.setFocusable(false);
    up.setOpaque(false);
    up.setBorderPainted(false);
    up.setContentAreaFilled(false);

    up.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        triggerAction("up");
      }
    });

    down = new JButton(new ImageIcon(ImageManager.ui_down_arrow));
    down.setBorder(null);
    down.setPreferredSize(new Dimension(20, 10));
    down.setFocusable(false);
    down.setOpaque(false);
    down.setBorderPainted(false);
    down.setContentAreaFilled(false);

    down.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        triggerAction("down");
      }
    });

    add(up, c);
    c.gridy += 1;
    
    add(down, c);
  }

  public void addActionListener(ActionListener listener) {
    this.listener = listener;
  }

  private void triggerAction(String button) {
    listener.actionPerformed(new ActionEvent(this, 0, button));
  }
}
