/*
Imports all the fonts and stores them in public memory
*/

package com.turtledsr.ittr.include.ui.helper;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import com.turtledsr.ittr.Main;
import com.turtledsr.ittr.include.engine.Logs;

public final class FontManager {
  public static final float FONT_SIZE = 14.0f;

  public static Font poppins;

  public static void loadFonts() {
    poppins = loadFontFile("assets/font/Poppins-Regular.ttf");
    if(poppins != null) poppins = poppins.deriveFont(FONT_SIZE);
  }

  private static Font loadFontFile(String path) {
    InputStream is = Main.class.getResourceAsStream(path);

    if(is == null) {
      Logs.logError("Font file not found: " + path, "FONT_MANAGER");
      return null;
    }

    try {return Font.createFont(Font.TRUETYPE_FONT, is);} catch(FontFormatException e) {
      Logs.logError("There was a problem formatting the font", "FONT_MANAGER");
      Logs.logError(e.getMessage(), "FONT_MANAGER");
    } catch (IOException e) {
      Logs.logError("There was a problem loading the font file", "FONT_MANAGER");
      Logs.logError(e.getMessage(), "FONT_MANAGER");
    }

    return null;
  }
}
