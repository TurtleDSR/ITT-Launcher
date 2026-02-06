/*
Panel used to select mods to launch
*/

package com.turtledsr.launcher.include.ui.main.launcher;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.process.Process;
import com.turtledsr.launcher.include.struct.Mod;
import com.turtledsr.launcher.include.ui.helper.StyleManager;

public final class ModsPanel extends JPanel implements MouseWheelListener {
  public static final int MODCOUNT = 10;

  public static ArrayList<Mod> mods;
  public static ModButton[] modButtons = new ModButton[MODCOUNT];

  public static int startingModIndex = 0; //starting point for the mod list

  public ModsPanel() {
    super(new GridBagLayout());

    if(mods == null) mods = Process.getModList();

    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width, StyleManager.PANEL_SIZE.height - StyleManager.LAUNCH_PANEL_HEIGHT - 7));
    setBackground(StyleManager.background_color);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;

    for (int i = 0; i < MODCOUNT; i++) {
      if(mods != null && i < mods.size()) {
        modButtons[i] = new ModButton(mods.get(i));
      } else {
        break;
      }

      add(modButtons[i], c);
      c.gridy++;
    }

    c.weighty = 1;
    add(new JLabel(), c); //weigh the buttons down

    addMouseWheelListener(this);

    setOpaque(true);
  }

  public static void updateModList() {
    for (int i = 0; i < MODCOUNT; i++) {
      if(mods != null && i < mods.size()) {
        if(modButtons[i] == null) {
          modButtons[i] = new ModButton(mods.get(i + startingModIndex));
        } else {
          modButtons[i].mod = mods.get(i + startingModIndex);
        }
        modButtons[i].update();
      } else {
        break;
      }
    }
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if(mods.size() > 10) {
      startingModIndex += e.getWheelRotation();

      if(startingModIndex < 0) startingModIndex = 0;
      if(startingModIndex + 9 >= mods.size()) startingModIndex = mods.size() - 10;

      updateModList();
      Logs.log("MOUSE MOVED");
    }
  }
}
