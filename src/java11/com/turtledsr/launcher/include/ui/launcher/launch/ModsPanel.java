/*
Main panel for the game/livesplit launcher
*/

package com.turtledsr.launcher.include.ui.launcher.launch;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import com.turtledsr.launcher.include.engine.events.EventListener;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.scrollPane.FlatScrollPane;

public final class ModsPanel extends JPanel {
  public static ModsToolbar launchButtonPanel;
  public static ModsListPanel modsPanel;
  private static FlatScrollPane modsScrollPane; //wraps the modsPanel

  public ModsPanel() {
    super(new GridBagLayout());

    setPreferredSize(StyleManager.PANEL_SIZE);
    setBackground(StyleManager.background_color);

    update();

    setVisible(false);

    EventManager.addListener(new EventListener() {
      @Override
      public void eventTriggered() {
        update();
      }
    }, "Mod_Folder_Update");
  }

  public void update() {
    removeAll();

    if(launchButtonPanel == null) launchButtonPanel = new ModsToolbar();
    if(modsPanel == null) modsPanel = new ModsListPanel();
    if(modsScrollPane == null) {
      modsScrollPane = new FlatScrollPane();
      modsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      modsScrollPane.getHorizontalScrollBar().setValue(modsScrollPane.getHorizontalScrollBar().getMinimum());
      modsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      modsScrollPane.setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width - 20, StyleManager.PANEL_SIZE.height - StyleManager.LAUNCH_PANEL_HEIGHT - 13));
    }

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 0;

    add(launchButtonPanel, c);

    c.anchor = GridBagConstraints.SOUTH;
    c.insets = new Insets(0, 10, 5, 10);
    c.gridy += 1;

    JPanel sep = new JPanel();
    sep.setPreferredSize(new Dimension(1, 2));
    sep.setBackground(StyleManager.separator_color);
    add(sep, c);

    c.anchor = GridBagConstraints.NORTHWEST;
    c.gridy += 1;
    
    ModsListPanel.refreshMods();

    if(ModsListPanel.mods.size() >= 10) {
      c.insets = new Insets(0, 0, 5, 12);
    } else {
      c.insets = new Insets(0, 0, 0, 0);
    }

    if(ModsListPanel.mods.size() >= 10) {
      modsScrollPane.setViewportView(modsPanel);
      add(modsScrollPane, c);
    } else {
      add(modsPanel, c);
      c.gridy += 1;
      
      c.weighty = 1;
      add(new JLabel(), c); //weigh the rest down
    }

    revalidate();
    repaint();
  }
}
