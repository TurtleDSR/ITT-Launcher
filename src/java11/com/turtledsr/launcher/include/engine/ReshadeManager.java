/*
Helper class for managing the reshade runtime and files associated with it
*/

package com.turtledsr.launcher.include.engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.ui.styled.messageBox.MessageBox;

public class ReshadeManager {
  public static void installReshade() {
    //dxgi.dll
    try(InputStream s = Main.getResourceAsStream("reshade/dxgi.dll")) {
      if(s == null) {
        throw new Exception("null stream");
      }
      Files.copy(s, Paths.get(Process.getGameDirectory() + "Nuts/Binaries/Win64/dxgi.dll"), StandardCopyOption.REPLACE_EXISTING);
    } catch(Exception ex) {Logs.logError("dxgi.dll not found: " + ex.getMessage(), "RESHADE_MANAGER"); new MessageBox("ReShade Failure", "ERROR: dxgi.dll not found"); return;}
    //ReShade.ini
    try(InputStream s = Main.getResourceAsStream("reshade/ReShade.ini")) {
      if(s == null) {
        throw new Exception("null stream");
      }
      Files.copy(s, Paths.get(Process.getGameDirectory() + "Nuts/Binaries/Win64/ReShade.ini"));
    } catch(FileAlreadyExistsException ex) {
      //unimplemented
    } catch(Exception ex) {Logs.logError("ReShade.ini not found: " + ex.getMessage(), "RESHADE_MANAGER"); new MessageBox("ReShade Failure", "ERROR: ReShade.ini not found"); return;}
    //ReShadePreset.ini
    try(InputStream s = Main.getResourceAsStream("reshade/ReShadePreset.ini")) {
      if(s == null) {
        throw new Exception("null stream");
      }
      Files.copy(s, Paths.get(Process.getGameDirectory() + "Nuts/Binaries/Win64/ReShadePreset.ini"), StandardCopyOption.REPLACE_EXISTING);
    } catch(Exception ex) {Logs.logError("ReShadePreset.ini not found: " + ex.getMessage(), "RESHADE_MANAGER"); new MessageBox("ReShade Failure", "ERROR: ReShadePreset.ini not found"); return;}
    //ShaderToggler.addon64
    try(InputStream s = Main.getResourceAsStream("reshade/ShaderToggler.addon64")) {
      if(s == null) {
        throw new Exception("null stream");
      }
      Files.copy(s, Paths.get(Process.getGameDirectory() + "Nuts/Binaries/Win64/ShaderToggler.addon64"), StandardCopyOption.REPLACE_EXISTING);
    } catch(Exception ex) {Logs.logError("ShaderToggler.addon64 not found: " + ex.getMessage(), "RESHADE_MANAGER"); new MessageBox("ReShade Failure", "ERROR: ShaderToggler.addon64 not found"); return;}

    //reshade-shaders
    if(extractShaders()) {
      new MessageBox(null, "ReShade Succesfully Installed!");
    } else {
      new MessageBox("Reshade Failure", "ERROR: Failed to install reshade-shaders");
    }
  }

  private static boolean extractShaders() {
    Path shaderFolder = Paths.get(Process.getGameDirectory() + "Nuts/Binaries/Win64/reshade-shaders");

    InputStream in = Main.getResourceAsStream("reshade/reshade-shaders.zip");

    if(in == null) {
      Logs.logError("Could not find ZIP file", "RESHADE_MANAGER");
      return false;
    }

    try{
      ZipManager.extractFromStream(in, shaderFolder.toString());
    } catch(IOException e) {
      Logs.logError("Failed to extract shaders: " + e.getMessage(), "RESHADE_MANAGER");
      return false;
    }

    return true;
  }
}
