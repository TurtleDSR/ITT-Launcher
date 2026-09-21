/*
Widget for editing a single config.
*/

package com.turtledsr.launcher.include.ui.launcher.configs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;

import com.turtledsr.launcher.include.config.gameconfig.GameConfig;
import com.turtledsr.launcher.include.config.gameconfig.GameConfig.ConfigType;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.launcher.configs.filter.FloatFilter;
import com.turtledsr.launcher.include.ui.launcher.configs.filter.IntFilter;
import com.turtledsr.launcher.include.ui.styled.button.ToggleButton;

public class ConfigEdit extends JPanel {
  public GameConfig config;
  protected JLabel label;
  protected JTextField input;
  protected ToggleButton toggle;
  
  public ConfigEdit(GameConfig config) {
    super(new GridBagLayout());
    setPreferredSize(new Dimension((int) StyleManager.PANEL_SIZE.width / 2, 25));
    setBackground(StyleManager.background_color);

    this.config = config;
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.weighty = 1;

    label = new JLabel(config.identifier + ":");
    label.setForeground(StyleManager.foreground_color);
    label.setFont(FontManager.poppins.deriveFont(20.f));

    add(label, c);
    
    c.gridx += 1;
    c.weightx = 1;
    c.anchor = GridBagConstraints.EAST;

    if(config.type == ConfigType._boolean) {
      toggle = new ToggleButton(config.value.equalsIgnoreCase("true"));
      toggle.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          config.value = "" + toggle.toggled;
          Process.writeGameConfigs();
        }
      });

      toggle.setMinimumSize(toggle.getPreferredSize());
      add(toggle, c);
    } else {
      input = new JTextField(config.value);
      if(config.type == ConfigType._float) {
        ((PlainDocument) input.getDocument()).setDocumentFilter(new FloatFilter());
      } else if(config.type == ConfigType._int) {
        ((PlainDocument) input.getDocument()).setDocumentFilter(new IntFilter());
      }
      input.setMinimumSize(new Dimension((int) (StyleManager.PANEL_SIZE.width / 3.5), 25));
      input.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void changedUpdate(DocumentEvent e) {
          update();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
          update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          update();
        }

        public void update() {
          if(config.type == ConfigType._float) {
            config.value = String.format("%.5f", Float.parseFloat(input.getText()));
          } else if(config.type == ConfigType._int) {
            config.value = String.format("%d", Integer.parseInt(input.getText()));
          } else {
            config.value = input.getText();
          }

          Process.writeGameConfigs();
        }
      });

      add(input, c);
    }
  }
}
