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
    app_close = loadImageFile("assets\\img\\app_close.png").getScaledInstance(35, 35, 0);
    app_minimise = loadImageFile("assets\\img\\app_minimise.png").getScaledInstance(35, 6, 0);
  }

  private static Image loadImageFile(String path) {
    InputStream is = Main.class.getResourceAsStream(path);

    try {return ImageIO.read(is);} catch(IOException e) {
      Logs.logError("There was a problem loading the image", "IMAGEMANAGER");
			Logs.logError(e.getMessage(), "IMAGEMANAGER");
    }

    return null;
  }
}
