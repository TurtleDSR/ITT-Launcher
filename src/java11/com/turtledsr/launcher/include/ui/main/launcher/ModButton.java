/*
The panel that shows the mod name and a toggle switch 
*/

package com.turtledsr.launcher.include.ui.main.launcher;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.turtledsr.launcher.include.process.Process;
import com.turtledsr.launcher.include.struct.Mod;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.styled.RoundedPanel;

public final class ModButton extends RoundedPanel implements ActionListener {
  public Mod mod;

  private final static int CORNER_ROUNDING = 12;

  private JLabel modLabel;
  private ModToggleButton toggle;

  public ModButton(Mod mod) {
    super(new GridBagLayout(), CORNER_ROUNDING);

    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width - 15,
        (StyleManager.PANEL_SIZE.height - StyleManager.LAUNCH_PANEL_HEIGHT - 7) / ModsPanel.MODCOUNT));
    setBackground(StyleManager.background_color);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 10, 0, 0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    modLabel = new JLabel(mod.name);
    modLabel.setFont(FontManager.poppins.deriveFont(Font.BOLD, 16f));
    modLabel.setForeground(StyleManager.foreground_color);

    toggle = new ModToggleButton(mod.toggled);
    toggle.addActionListener(this);
    toggle.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        setBackground(StyleManager.background_hover_color);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setBackground(StyleManager.background_color);
      }
    });

    add(modLabel, c);

    c.weightx = 0;
    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets(0, 10, 0, 10);
    c.gridx += 1;
    add(toggle, c);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        setBackground(StyleManager.background_hover_color);
        setCursor(new Cursor(Cursor.MOVE_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setBackground(StyleManager.background_color);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
    });

    DragAdapter adapter = new DragAdapter(this);
    addMouseListener(adapter);
    addMouseMotionListener(adapter);

    this.mod = mod;
  }

  public void update() {
    modLabel.setText(mod.name);
    toggle.toggled = mod.toggled;
    toggle.update();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    mod.toggled = toggle.toggled;
    Process.updateModListCache(ModsPanel.mods);
  }
}

class DragAdapter extends MouseAdapter {
  ModButton parent;
  private int activeSlotIndex = -1;

  public DragAdapter(ModButton button) {
    this.parent = button;
  }

  @Override
  public void mousePressed(MouseEvent e) {
    for (int i = 0; i < ModsPanel.MODCOUNT; i++) {
      if (ModsPanel.modButtons[i] == parent) {
        activeSlotIndex = i;
        break;
      }
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (activeSlotIndex == -1) return;

    Point p = SwingUtilities.convertPoint(parent, e.getPoint(), parent.getParent());
    int slotHeight = parent.getHeight();

    int targetSlot = p.y / slotHeight;

    if (targetSlot < 0) {
      targetSlot = 0;
    }
    if (targetSlot >= ModsPanel.MODCOUNT) {
      targetSlot = ModsPanel.MODCOUNT - 1;
    }

    int currentActualIndex = activeSlotIndex + ModsPanel.startingModIndex;
    int targetActualIndex = targetSlot + ModsPanel.startingModIndex;

    if (targetSlot != activeSlotIndex && targetActualIndex < ModsPanel.mods.size() && targetActualIndex >= 0) {
      Mod movingMod = ModsPanel.mods.remove(currentActualIndex);
      ModsPanel.mods.add(targetActualIndex, movingMod);

      activeSlotIndex = targetSlot;
      ModsPanel.updateModList();
      Process.updateModListCache(ModsPanel.mods);

      parent.getParent().revalidate();
      parent.getParent().repaint();
    }
  }
}