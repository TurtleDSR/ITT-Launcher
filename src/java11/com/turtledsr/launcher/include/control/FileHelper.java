package com.turtledsr.launcher.include.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

import com.turtledsr.launcher.include.engine.Logs;

public final class FileHelper {
  public static void moveFolders(Path sourceDir, Path destinationDir) throws IOException {
    try{
      if (!Files.exists(destinationDir)) {
        Files.createDirectories(destinationDir);
      }
    } catch(Exception e) {Logs.logError(e.getMessage(), "FileHelper");} //should never fail

    try (Stream<Path> stream = Files.list(sourceDir)) {
      stream.filter(Files::isDirectory)
        .forEach(folderPath -> {
          Path destination = destinationDir.resolve(folderPath.getFileName());
          try {
            Files.move(folderPath, destination, StandardCopyOption.REPLACE_EXISTING);
          } catch (IOException e) {
            Logs.logError("Failed to move folder " + folderPath.getFileName() + ": " + e.getMessage(), "FileHelper");
          }
      });
    }
  }

  public static void deleteSubfolders(String filepath) throws IOException {
    Path root = Paths.get(filepath);

    Files.walk(root)
      .filter(Files::isDirectory)
      .filter(path -> !path.equals(root))
      .sorted(Comparator.reverseOrder())
      .forEach(path -> {
        try {
          deleteContents(path); 
          Files.delete(path);
        } catch (IOException e) {
          Logs.logError("Failed to delete " + path + ": " + e.getMessage(), "FileHelper");
        }
    });
  }

  private static void deleteContents(Path proxy) throws IOException {
    Files.walk(proxy)
      .sorted(Comparator.reverseOrder())
      .filter(p -> !p.equals(proxy))
      .forEach(p -> {
        try { Files.delete(p); } catch (IOException e) {Logs.logError(e.getMessage(), "FileHelper");}
    });
  }
}