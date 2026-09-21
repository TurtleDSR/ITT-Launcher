/*
Panel used to select mods to launch
*/

package com.turtledsr.launcher.include.ui.launcher.launch;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.struct.Mod;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.launcher.launch.modIndex.ModIndex;

public final class ModsListPanel extends JPanel {
  public static ArrayList<Mod> mods;
  public static ArrayList<ModIndex> modButtons;

  public ModsListPanel() {
    super(new GridBagLayout());

    updateModButtonList();
    update();
  }

  public void update() {
    removeAll();

    setBackground(StyleManager.background_color);
    setPreferredSize(new Dimension(modButtons.get(0).getPreferredSize().width, modButtons.size() * modButtons.get(0).getPreferredSize().height));

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, (ModsListPanel.mods.size() >= 10 ? 8 : 0), 0, (ModsListPanel.mods.size() >= 10 ? 6 : 0));
    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;

    for (ModIndex i : modButtons) {
      add(i, c);
      c.gridy += 1;
    }

    c.weighty = 1;
    add(new JLabel(), c); //weigh the buttons down

    setOpaque(true);
    revalidate();
    repaint();
  }

  public static void updateModButtonList() {
    mods = Process.getModList();

    if(modButtons == null) modButtons = new ArrayList<ModIndex>();
    
    while(modButtons.size() < mods.size()) {
      modButtons.add(null);
    }
    while(modButtons.size() > mods.size()) {
      modButtons.remove(modButtons.size() - 1);
    }

    for (int i = 0; i < modButtons.size(); i++) {
      ModIndex current = modButtons.get(i);
      if(current == null) {
        modButtons.set(i, new ModIndex(mods.get(i)));
      } else {
        current.mod = mods.get(i);
        current.update();
      }
    }

  }

  public static void refreshMods() {
    updateModButtonList();
    ModsPanel.modsPanel.update();
  }
}
