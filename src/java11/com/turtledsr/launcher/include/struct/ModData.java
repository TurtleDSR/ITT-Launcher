package com.turtledsr.launcher.include.struct;

public class ModData {
  public String path = "";
  public boolean toggled = false;

  public ModData(String path, boolean toggled) {
    this.path = path;
    this.toggled = toggled;
  }

  public ModData() {
    //unimplemented
  }
}
