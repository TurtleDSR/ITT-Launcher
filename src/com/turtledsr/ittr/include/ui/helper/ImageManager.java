/*
Imports all the images and stores them in public memory
*/

package com.turtledsr.ittr.include.ui.helper;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.turtledsr.ittr.Main;
import com.turtledsr.ittr.include.engine.Logs;

public final class ImageManager {
  public static final float FONT_SIZE = 14.0f;

  public static Image app_close;
  public static Image app_minimise;

  public static void loadImages() {
    app_close = loadImageFile("assets/img/app_close.png");
    if(app_close != null) app_close = app_close.getScaledInstance(StyleManager.TITLEBAR_HEIGHT - 4, StyleManager.TITLEBAR_HEIGHT - 4, 0);

    app_minimise = loadImageFile("assets/img/app_minimise.png");
    if(app_minimise != null) app_minimise = app_minimise.getScaledInstance(StyleManager.TITLEBAR_HEIGHT - 5, (StyleManager.TITLEBAR_HEIGHT - 5) / 6, 0);
  }

  private static Image loadImageFile(String path) {
    InputStream is = Main.class.getResourceAsStream(path);

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
