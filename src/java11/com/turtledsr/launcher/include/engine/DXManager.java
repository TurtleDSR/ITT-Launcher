/*
Helper class for managing the dx12 runtime and files associated with it
*/

package com.turtledsr.launcher.include.engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.engine.events.EventListener;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.ui.styled.messageBox.MessageBox;

public final class DXManager {
  private static Path shaderFolder = Paths.get("dx12/");

  public static void installDX12() {
    Thread installerThread = new Thread(() -> asyncInstallDX12());
    installerThread.setDaemon(true);
    installerThread.start();
  }

  protected static void asyncInstallDX12() { //doesnt actually implement the async logic, thats up to installDX12()
    try(InputStream s = Main.getResourceAsStream("dx12/Engine.ini")) {
      if(s == null) {
        throw new Exception("null stream");
      }
      Files.copy(s, Paths.get(System.getenv("localappdata") + "/ItTakesTwo/Saved/Config/WindowsNoEditor/Engine.ini"), StandardCopyOption.REPLACE_EXISTING);
    } catch(Exception ex) {Logs.logError("Engine.ini not found: " + ex.getMessage(), "DX_MANAGER"); new MessageBox("DX12 Failure", "ERROR: Engine.ini not found"); return;}

    String hash = Process.getShaderCacheHash();
    if(hash == null) {
      new MessageBox(null, "DX12 Partially installed.\nGame will launch, please close the game when it has fully loaded. DX12 will finish installing then."); 
      EventManager.addListener(new EventListener() {
        @Override
        public void eventTriggered() {
          try{
            Thread.sleep(3000);
            asyncInstallDX12();
          } catch(Exception e) {
            Logs.logError("Failed to install DX12: " + e.getMessage(), "DX_MANAGER");
            new MessageBox("DX12 Failure", "ERROR: Failed To Install DX12\nDM me on discord with a screenshot of the logs tab, I will try to help");
          }
        }
      }, "game_disconnected");
      Process.launchItTakesTwoEx(false);
      return;
    } else {
      extractShaders();

      Path compute = Paths.get("dx12/D3DCompute.ushaderprecache");
      Path graphics = Paths.get("dx12/D3DGraphics.ushaderprecache");

      Path computeTarget = Paths.get(System.getenv("localappdata") + "/ItTakesTwo/Saved/D3DCompute_" + hash + ".ushaderprecache");
      Path graphicsTarget = Paths.get(System.getenv("localappdata") + "/ItTakesTwo/Saved/D3DGraphics_" + hash + ".ushaderprecache");

      try{
        Files.copy(compute, computeTarget, StandardCopyOption.REPLACE_EXISTING);
      } catch(Exception e) {
        Logs.logError("Failed to replace compute shader: " + e.getLocalizedMessage(), "DX_MANAGER");
        new MessageBox("DX12 Failure", "ERROR: Failed to replace compute shader cache\nDM me on discord with a screenshot of the logs tab, I will try to help");
        return;
      }

      try{
        Files.copy(graphics, graphicsTarget, StandardCopyOption.REPLACE_EXISTING);
      } catch(Exception e) {
        Logs.logError("Failed to replace graphics shader: " + e.getMessage(), "DX_MANAGER");
        new MessageBox("DX12 Failure", "ERROR: Failed to replace graphics shader cache\nDM me on discord with a screenshot of the logs tab, I will try to help");
        return;
      }

      try (Stream<Path> walk = Files.walk(shaderFolder)) {
        walk.sorted(Comparator.reverseOrder())
        .forEach(path -> {
          try {
            Files.delete(path);
          } catch (IOException e) {
            Logs.logError("Failed to delete subfile: " + e.getLocalizedMessage(), "DX_MANAGER");
          }
        });
      } catch(Exception e) {
        Logs.logError("Failed to remove dx12 folder: " + e.getMessage(), "DX_MANAGER");
        new MessageBox("DX12 Failure", "ERROR: Failed to remove dx12 folder\nDM me on discord with a screenshot of the logs tab, I will try to help");
        return;
      }
      new MessageBox(null, "DX12 Succesfully Installed!");
    }
  }

  private static void extractShaders() {
    //check if folder is present, if it is, dont extract
    if(Files.exists(shaderFolder) && Files.isDirectory(shaderFolder)) {
      Logs.log("dx12 folder exists, will not extract", "DX_MANAGER");
      return;
    }

    InputStream in = Main.getResourceAsStream("dx12/shaders.zip");

    if(in == null) {
      Logs.logError("Could not find ZIP file", "DX_MANAGER");
      return;
    }

    try{
      ZipManager.extractFromStream(in, shaderFolder.toString());
    } catch(IOException e) {
      Logs.logError("Failed to extract shaders: " + e.getMessage(), "DX_MANAGER");
    }
  }
}
