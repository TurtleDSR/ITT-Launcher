/*
Helper class for zip archives
*/

package com.turtledsr.launcher.include.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

public final class ZipManager {
  public static void extractFromStream(InputStream inputStream, String destination) throws IOException {
    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
      LocalFileHeader localFileHeader;
      byte[] readBuffer = new byte[4096];
      int readLen;

      while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
        File extractedFile = new File(destination + localFileHeader.getFileName());

        if (localFileHeader.isDirectory()) {
          extractedFile.mkdirs();
          continue;
        }

        try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
          while ((readLen = zipInputStream.read(readBuffer)) != -1) {
            outputStream.write(readBuffer, 0, readLen);
          }
        }
      }
    }
  }

  public static void extractFromStream(InputStream inputStream, String destination, String ignorePath) throws IOException {
    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
      LocalFileHeader localFileHeader;
      byte[] readBuffer = new byte[4096];
      int readLen;

      while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
        File extractedFile = new File(destination + localFileHeader.getFileName());

        String normalizedFileName = extractedFile.getAbsolutePath().replace("\\", "/");
        String normalizedIgnorePath = ignorePath.replace("\\", "/");

        if (normalizedFileName.equalsIgnoreCase(normalizedIgnorePath) && extractedFile.exists()) {
          continue; 
        }

        if (localFileHeader.isDirectory()) {
          extractedFile.mkdirs();
          continue;
        }

        try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
          while ((readLen = zipInputStream.read(readBuffer)) != -1) {
            outputStream.write(readBuffer, 0, readLen);
          }
        }
      }
    }
  }
}
