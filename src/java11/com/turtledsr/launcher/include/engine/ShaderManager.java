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

import javax.swing.JOptionPane;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.engine.events.EventListener;
import com.turtledsr.launcher.include.engine.events.EventManager;
import com.turtledsr.launcher.include.process.Process;

public final class ShaderManager {
  private static Path shaderFolder = Paths.get("dx12/");

  public static void installDX12() {
    try(InputStream s = Main.getResourceAsStream("dx12/Engine.ini")) {
      if(s == null) {
        throw new Exception("null stream");
      }
      Files.copy(s, Paths.get(System.getenv("localappdata") + "/ItTakesTwo/Saved/Config/WindowsNoEditor/Engine.ini"), StandardCopyOption.REPLACE_EXISTING);
    } catch(Exception ex) {Logs.logError("Engine.ini not found: " + ex.getMessage(), "SHADER_BUTTON"); JOptionPane.showMessageDialog(null, "ERROR: Engine.ini not found"); return;}
      
    String hash = Process.getShaderCacheHash();
    if(hash == null) {
      JOptionPane.showMessageDialog(null, "DX12 Partially installed.\nGame will launch, please close the game when it has fully loaded. DX12 will finish installing then."); 
      EventManager.addListener(new EventListener() {
        @Override
        public void eventTriggered() {
          try{
            Thread.sleep(3000);
            ShaderManager.installDX12();
          } catch(Exception e) {
            Logs.logError("Failed to install DX12: ", e.getMessage());
            JOptionPane.showMessageDialog(null, "ERROR: Failed To Install DX12\nDM me on discord with a screenshot of the logs tab, I will try to help");
          }
        }
      }, "game_disconnected");
      Process.launchItTakesTwoEx(false);
      return;
    } else {
      Path compute = Paths.get("dx12/D3DCompute.ushaderprecache");
      Path graphics = Paths.get("dx12/D3DGraphics.ushaderprecache");

      Path computeTarget = Paths.get(System.getenv("localappdata") + "/ItTakesTwo/Saved/D3DCompute_" + hash + ".ushaderprecache");
      Path graphicsTarget = Paths.get(System.getenv("localappdata") + "/ItTakesTwo/Saved/D3DGraphics_" + hash + ".ushaderprecache");

      try{
        Files.copy(compute, computeTarget, StandardCopyOption.REPLACE_EXISTING);
      } catch(Exception e) {
        Logs.logError("Failed to replace compute shader: ", e.getMessage());
        JOptionPane.showMessageDialog(null, "ERROR: Failed to replace compute shader cache\nDM me on discord with a screenshot of the logs tab, I will try to help");
        return;
      }

      try{
        Files.copy(graphics, graphicsTarget, StandardCopyOption.REPLACE_EXISTING);
      } catch(Exception e) {
        Logs.logError("Failed to replace graphics shader: ", e.getMessage());
        JOptionPane.showMessageDialog(null, "ERROR: Failed to replace graphics shader cache\nDM me on discord with a screenshot of the logs tab, I will try to help");
        return;
      }

      JOptionPane.showMessageDialog(null, "DX12 Succesfully Installed!");
    }
  }

  public static void extractShaders() {
    //check if folder is present, if it is, dont extract
    if(Files.exists(shaderFolder) && Files.isDirectory(shaderFolder)) {
      Logs.log("dx12 folder exists, will not extract", "SHADER_MANAGER");
      return;
    }

    Logs.log("dx12 folder does not exist, extracting", "SHADER_MANAGER");
    InputStream in = Main.getResourceAsStream("dx12/shaders.zip");

    if(in == null) {
      Logs.logError("Could not find ZIP file", "SHADER_MANAGER");
      return;
    }

    try{
      ZipManager.extractFromStream(in, shaderFolder.toString());
    } catch(IOException e) {
      Logs.logError("Failed to extract shaders: " + e.getMessage(), "SHADER_MANAGER");
    }
  }
}
