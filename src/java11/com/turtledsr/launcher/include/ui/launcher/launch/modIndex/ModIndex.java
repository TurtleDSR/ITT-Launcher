/*
The panel that shows the mod name and a toggle switch 
*/

package com.turtledsr.launcher.include.ui.launcher.launch.modIndex;

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

import com.turtledsr.hazemodlib.ModFormat;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.struct.ModHandle;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.launcher.launch.ModsListPanel;
import com.turtledsr.launcher.include.ui.styled.panel.RoundedPanel;

public final class ModIndex extends RoundedPanel implements ActionListener {
  public ModHandle mod;

  private final static int CORNER_ROUNDING = 12;

  private JLabel versionLabel;
  private JLabel nameLabel;
  private ModToggleButton toggle;

  public ModIndex(ModHandle mod) {
    super(new GridBagLayout(), CORNER_ROUNDING);

    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width - (ModsListPanel.mods.size() >= 10 ? 33 : 20),
      (StyleManager.PANEL_SIZE.height - StyleManager.LAUNCH_PANEL_HEIGHT - 7) / 10));
    setBackground(StyleManager.background_color);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 7, 0, 0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    versionLabel = new JLabel();
    versionLabel.setFont(FontManager.poppins.deriveFont(Font.BOLD, 12f));
    versionLabel.setForeground(StyleManager.foreground_color);
    versionLabel.setPreferredSize(new Dimension(40, getPreferredSize().height));
    
    nameLabel = new JLabel();
    nameLabel.setFont(FontManager.poppins.deriveFont(Font.BOLD, 16f));
    nameLabel.setForeground(StyleManager.foreground_color);
    nameLabel.setPreferredSize(new Dimension(593, getPreferredSize().height));

    toggle = new ModToggleButton(false);
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

    if(mod != null) {
      toggle.toggled = mod.toggled;
      toggle.update();
    }

    add(versionLabel, c);

    c.insets = new Insets(0, 0, 0, 0);
    c.gridx += 1;

    add(nameLabel, c);

    c.weightx = 0;
    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets(0, 0, 0, 5);
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

    if(mod == null) setVisible(false);
    update();
  }

  public void update() {
    if(mod == null) {
      setVisible(false);
      return;
    }

    setVisible(true);
    if(mod != null) {
      nameLabel.setText(mod.properties.getName());

      if(mod.properties.getFormat() == ModFormat.zip) {
        versionLabel.setFont(FontManager.poppins.deriveFont(Font.BOLD, 20f));
        versionLabel.setText("<html><font color='red'>&nbsp;&nbsp;•</html>");
      } else {
        versionLabel.setFont(FontManager.poppins.deriveFont(Font.BOLD, 12f));
        StringBuilder labelBuilder = new StringBuilder("<html><font color='yellow'>");
        if(mod.properties.getVersion() != null) {
          labelBuilder.append(mod.properties.getVersion());
        }
        labelBuilder.append("</font></html>");
        versionLabel.setText(labelBuilder.toString());
      }
    }
    toggle.toggled = mod.toggled;
    toggle.update();

    setPreferredSize(new Dimension(StyleManager.PANEL_SIZE.width - (ModsListPanel.mods.size() >= 10 ? 33 : 20),
      (StyleManager.PANEL_SIZE.height - StyleManager.LAUNCH_PANEL_HEIGHT - 7) / 10));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    mod.toggled = toggle.toggled;
    Process.updateModListCache(ModsListPanel.mods);
  }
}

class DragAdapter extends MouseAdapter {
  ModIndex parent;
  private int activeSlotIndex = -1;

  public DragAdapter(ModIndex button) {
    this.parent = button;
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if(e.getButton() != MouseEvent.BUTTON1) {
      activeSlotIndex = -1;
      return;
    }
    if(parent.mod == null) return;

    for (int i = 0; i < ModsListPanel.modButtons.size(); i++) {
      if (ModsListPanel.modButtons.get(i) == parent) {
        activeSlotIndex = i;
        break;
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if(e.getButton() == MouseEvent.BUTTON3) {
      //implement mod info widget
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if(parent.mod == null) return;
    if (activeSlotIndex == -1) return;

    Point p = SwingUtilities.convertPoint(parent, e.getPoint(), parent.getParent());
    int slotHeight = parent.getHeight();

    int targetSlot = p.y / slotHeight;

    if (targetSlot < 0) {
      targetSlot = 0;
    }
    if (targetSlot >= ModsListPanel.modButtons.size()) {
      targetSlot = ModsListPanel.modButtons.size() - 1;
    }

    if (targetSlot != activeSlotIndex && targetSlot < ModsListPanel.mods.size() && targetSlot >= 0) {
      ModHandle movingMod = ModsListPanel.mods.remove(activeSlotIndex);
      ModsListPanel.mods.add(targetSlot, movingMod);

      activeSlotIndex = targetSlot;
      Process.updateModListCache(ModsListPanel.mods);
      ModsListPanel.refreshMods();

      parent.getParent().revalidate();
      parent.getParent().repaint();
    }
  }
}