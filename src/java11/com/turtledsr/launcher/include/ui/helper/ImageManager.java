/*
Imports all the images and stores them in public memory
*/

package com.turtledsr.launcher.include.ui.helper;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.engine.Logs;

public final class ImageManager {
  public static final float FONT_SIZE = 14.0f;

  public static Image ui_app_close;
  public static Image ui_app_minimise;
  public static Image ui_up_arrow;
  public static Image ui_down_arrow;
  public static Image ui_cog;
  public static Image icon;

  public static void loadImages() {
    ui_app_close = loadImageFile("img/ui_app_close.png");
    if(ui_app_close != null) ui_app_close = ui_app_close.getScaledInstance(StyleManager.TITLEBAR_HEIGHT - 6, StyleManager.TITLEBAR_HEIGHT - 6, Image.SCALE_SMOOTH);

    ui_app_minimise = loadImageFile("img/ui_app_minimise.png");
    if(ui_app_minimise != null) ui_app_minimise = ui_app_minimise.getScaledInstance(StyleManager.TITLEBAR_HEIGHT - 8, (StyleManager.TITLEBAR_HEIGHT - 8) / 6, Image.SCALE_SMOOTH);

    ui_up_arrow = loadImageFile("img/ui_up_arrow.png");
    if(ui_up_arrow != null) ui_up_arrow = ui_up_arrow.getScaledInstance(12, 12, Image.SCALE_SMOOTH);

    ui_down_arrow = loadImageFile("img/ui_down_arrow.png");
    if(ui_down_arrow != null) ui_down_arrow = ui_down_arrow.getScaledInstance(12, 12, Image.SCALE_SMOOTH);

    ui_cog = loadImageFile("img/ui_cog.png");
    if(ui_cog != null) ui_cog = ui_cog.getScaledInstance(StyleManager.TITLEBAR_HEIGHT + 1, StyleManager.TITLEBAR_HEIGHT + 1, Image.SCALE_SMOOTH);

    icon = loadImageFile("img/icon.png");
    if(icon != null) icon = icon.getScaledInstance(32, 32, 0);
  }

  private static Image loadImageFile(String path) {
    InputStream is = Main.getResourceAsStream(path);

    if(is == null) {
      Logs.logError("Image file not found: " + path, "IMAGE_MANAGER");
      return null;
    }

    try {return ImageIO.read(is);} catch(IOException e) {
      Logs.logError("There was a problem loading the image file", "IMAGE_MANAGER");
      Logs.logError(e.getMessage(), "IMAGE_MANAGER");
    }

    return null;
  }
}
