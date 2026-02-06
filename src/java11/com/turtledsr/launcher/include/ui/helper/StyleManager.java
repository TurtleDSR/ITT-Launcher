/*
Manages and stores style information
*/

package com.turtledsr.launcher.include.ui.helper;

import java.awt.Color;
import java.awt.Dimension;

public final class StyleManager {
  public static final Dimension PANEL_SIZE = new Dimension(710, 380);

  public static final int TITLEBAR_HEIGHT = 30;

  public static final int SELECTOR_HEIGHT = 35;

  public static final int LAUNCH_PANEL_HEIGHT = 50;

  public static Color title_color = ColorUtils.hextoColor("#151516");
  public static Color title_accent = ColorUtils.hextoColor("#272830");

  public static Color background_color = ColorUtils.hextoColor("#1d1f2e");
  public static Color foreground_color = ColorUtils.hextoColor("#e4e4e4");

  public static Color separator_color = ColorUtils.hextoColor("#52556d");

  public static Color background_hover_color = ColorUtils.hextoColor("#272a3d");
  
  public static Color app_exit_color = ColorUtils.hextoColor("#b43b3b");
  public static Color launch_button_color = ColorUtils.hextoColor("#1e378b");
  public static Color launch_button_hover_color = ColorUtils.hextoColor("#324ea8");

  public static Color toggle_mods_on_color = ColorUtils.hextoColor("#3b7a45");
  public static Color toggle_mods_on_hover_color = ColorUtils.hextoColor("#548d5e");
  public static Color toggle_mods_off_color = ColorUtils.hextoColor("#572b2b");
  public static Color toggle_mods_off_hover_color = ColorUtils.hextoColor("#6b3c3c");

  public static Color selector_color = ColorUtils.hextoColor("#161722");
  public static Color selector_hovered_color = ColorUtils.hextoColor("#1e2030");
  public static Color selector_underline_color = ColorUtils.hextoColor("#1d4ef0");

  public static Color toggle_background_color = ColorUtils.hextoColor("#0e0f16");
  public static Color toggle_on_color = ColorUtils.hextoColor("#253ce7");
  public static Color toggle_off_color = ColorUtils.hextoColor("#0d154e");
  public static Color mods_toggle_off_color = ColorUtils.hextoColor("#3a1d1d");
}