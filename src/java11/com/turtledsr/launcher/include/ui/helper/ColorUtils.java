/*
Helper class used for managing colors
*/

package com.turtledsr.launcher.include.ui.helper;

import java.awt.Color;

public final class ColorUtils {
  public static Color hextoColor(String hex) { //turns hexcode into rgb value and passes it into a new Color object
    return new Color(Integer.parseInt(hex.substring(1, hex.length()), 16));
  }

  public static String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
