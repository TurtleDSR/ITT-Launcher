/*
Helper class used for managing colors
*/

package com.turtledsr.ittr.include.ui.helper;

import java.awt.Color;

public final class ColorUtils {
  public static Color hextoColor(String hex) { //turns hexcode into rgb value and passes it into a new Color object
    return new Color(Integer.parseInt(hex.substring(1, hex.length()), 16));
  }
}
