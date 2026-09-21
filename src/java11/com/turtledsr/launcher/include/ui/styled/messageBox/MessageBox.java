/*
Class used to display styled messages to the user

very similar to JOptionPane, just more stylised
*/

package com.turtledsr.launcher.include.ui.styled.messageBox;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.JEditorPane;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.ui.helper.ColorUtils;
import com.turtledsr.launcher.include.ui.helper.FontManager;
import com.turtledsr.launcher.include.ui.helper.StyleManager;
import com.turtledsr.launcher.include.ui.launcher.Window;
import com.turtledsr.launcher.include.ui.styled.button.FlatButton;
import com.turtledsr.launcher.include.ui.styled.messageBox.titleBar.TitleBar;

public class MessageBox extends JDialog {
  public static List<MessageBox> messageBoxes = new ArrayList<MessageBox>();

  public TitleBar titleBar;
  public JEditorPane messagePanel;
  public FlatButton[] buttonArray;

  public MessageBox(String message) {
    this("", message, new FlatButton[0]);
  }

  public MessageBox(String title, String message) {
    this(title, message, new FlatButton[0]);
  }

  public MessageBox(String title, String message, FlatButton... buttons) {
    super();
    if(buttons == null) {
      buttonArray = new FlatButton[0];
    } else {
      buttonArray = buttons;
    }

    setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 0;

    titleBar = new TitleBar(title, this);
    add(titleBar, c);
    c.gridy += 1;

    c.insets = new Insets(3, 4, 1, 4);
    c.fill = GridBagConstraints.BOTH; 
    if(buttonArray.length == 0) {
      c.anchor = GridBagConstraints.SOUTH;
      c.weighty = 1;
    }

    HTMLEditorKit kit = new HTMLEditorKit();
    StyleSheet styleSheet = kit.getStyleSheet();

    styleSheet.addRule( //paragraph format
      String.format(
        "body { color: %s; font-family: '%s'; font-size: 10px; }", 
        ColorUtils.colorToHex(StyleManager.foreground_color),
        FontManager.poppins.getFamily()
      )
    );

    styleSheet.addRule(
      String.format(
        "a { color: %s; }", 
        ColorUtils.colorToHex(StyleManager.launch_button_hover_color)
      )
    );

    messagePanel = new JEditorPane("text/html", "");
    messagePanel.setEditorKit(kit);

    messagePanel.setText(
      String.format(
        "<body>%s</body>",
        message
      )
    );

    messagePanel.setEditable(false);
    messagePanel.setFocusable(false);
    messagePanel.setPreferredSize(new Dimension(StyleManager.MESSAGE_BOX_SIZE.width - 8, StyleManager.MESSAGE_BOX_MESSAGE_HEIGHT));
    messagePanel.setBackground(StyleManager.background_hover_color);

    messagePanel.addHyperlinkListener(new HyperlinkListener() { //link listener
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if(e.getEventType() != EventType.ACTIVATED) return;

        Process.openLink(e.getURL().toString());
      }
    });

    add(messagePanel, c);

    c.gridy += 1;

    if(buttonArray.length != 0) {
      c.weighty = 1;
      c.insets = new Insets(5, 0, 12, 0);
      c.anchor = GridBagConstraints.SOUTH;
      c.fill = GridBagConstraints.HORIZONTAL;

      JPanel buttonPanel = new JPanel(new GridBagLayout());
      buttonPanel.setBackground(StyleManager.background_hover_color);
      GridBagConstraints cc = new GridBagConstraints();
      cc.anchor = GridBagConstraints.CENTER;
      cc.fill = GridBagConstraints.NONE;
      cc.insets = new Insets(0, 0, 0, 0);
      cc.gridx = 0;
      cc.gridy = 0;
      cc.weightx = 1;
      cc.weighty = 1;

      for (FlatButton button : buttonArray) {
        button.setPreferredSize(StyleManager.MESSAGE_BOX_BUTTON_SIZE);
        buttonPanel.add(button, cc);
        cc.gridx += 1;
      }

      add(buttonPanel, c);
    }

    setSize(StyleManager.MESSAGE_BOX_SIZE);

    Point targetPos = new Point((Main.window.getLocation().x + (Main.window.getWidth() / 2)) - (getWidth() / 2), (Main.window.getLocation().y + (Main.window.getHeight() / 2) - (getHeight() / 2)));
    setLocationWithBounds(targetPos);
    boolean matchingPos = true;
    while(matchingPos) {
      matchingPos = false;
      for (MessageBox box : messageBoxes) {
        if(this.getLocation().equals(box.getLocation())) {
          Point newPos = new Point(this.getX() + 20, this.getY() + 20);
          setLocationWithBounds(newPos);

          if(!this.getLocation().equals(newPos)) {
            newPos = new Point(targetPos.x + 100, targetPos.y);
            setLocationWithBounds(newPos);
          }

          if(this.getLocation().equals(box.getLocation())) { //still matches position with another messagebox
            break; //break out early and deal with overlap
          }

          matchingPos = true;
          break;
        }
      }
    }

    getContentPane().setBackground(StyleManager.background_hover_color);

    setResizable(false);
    setAlwaysOnTop(true);
    setUndecorated(true);
    setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), Window.CORNER_ROUNDING, Window.CORNER_ROUNDING));
    
    setVisible(true);
    toFront();

    messageBoxes.add(this);
  }

  @Override
  public void dispose() {
    messageBoxes.remove(this);
    super.dispose();
  }

  public void setLocationWithBounds(int newx, int newy) {
    int x = newx; 
    int y = newy;
    int width = getWidth();
    int height = getHeight();
    Rectangle bounds = new Rectangle(x, y, width, height);

    GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    
    boolean intersectsAnyMonitor = false;
    Rectangle closestMonitorBounds = null;
    double closestDistance = Double.MAX_VALUE;

    for (GraphicsDevice device : environment.getScreenDevices()) {
      GraphicsConfiguration configuration = device.getDefaultConfiguration();
      Rectangle deviceBounds = configuration.getBounds();
      Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration);

      insets.bottom += 10; //add small buffer zone so you dont have a single pixel remaining on screen before snapping
      insets.top += 10;
      insets.left += 10;
      insets.right += 10;

      Rectangle viewableBounds = new Rectangle(
        deviceBounds.x + insets.left,
        deviceBounds.y + insets.top,
        deviceBounds.width - insets.left - insets.right,
        deviceBounds.height - insets.top - insets.bottom
      );

      if (viewableBounds.intersects(bounds)) {
        intersectsAnyMonitor = true;
        break; 
      }

      double distanceX = Math.max(0, Math.max(viewableBounds.x - (x + width), x - (viewableBounds.x + viewableBounds.width)));
      double distanceY = Math.max(0, Math.max(viewableBounds.y - (y + height), y - (viewableBounds.y + viewableBounds.height)));
      double totalDistance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

      if (totalDistance < closestDistance) {
        closestDistance = totalDistance;
        closestMonitorBounds = viewableBounds;
      }
    }

    if (!intersectsAnyMonitor && closestMonitorBounds != null) {
      if (x + width > closestMonitorBounds.x + closestMonitorBounds.width) {
        x = (closestMonitorBounds.x + closestMonitorBounds.width) - width;
      }
      if (y + height > closestMonitorBounds.y + closestMonitorBounds.height) {
        y = (closestMonitorBounds.y + closestMonitorBounds.height) - height;
      }
      if (x < closestMonitorBounds.x) {
        x = closestMonitorBounds.x;
      }
      if (y < closestMonitorBounds.y) {
        y = closestMonitorBounds.y;
      }
    }
    super.setLocation(x, y);
  }

  public void setLocationWithBounds(Point point) { //add bounds checking
    setLocationWithBounds(point.x, point.y);
  }
}
