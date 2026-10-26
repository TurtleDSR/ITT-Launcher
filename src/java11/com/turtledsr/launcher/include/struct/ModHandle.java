/*
stores a mod and if its toggled or not
*/
package com.turtledsr.launcher.include.struct;

import com.turtledsr.hazemodlib.Mod;
import com.turtledsr.hazemodlib.ModProperties;

public final class ModHandle {
  public boolean toggled;
  public Mod mod;
  public ModProperties properties;

  public ModHandle(String modpath, boolean toggled) {
    this.mod = new Mod(modpath);
    this.properties = mod.getProperties();
    this.toggled = toggled;
  }

  @Override
  public boolean equals(Object o) {
    if(o == null || o.getClass() != this.getClass()) return false; 
    return (properties.getPath() != null) ? properties.getPath().equals(((ModHandle) o).properties.getPath()) : ((ModHandle) o).properties.getPath() == null;
  }

  @Override
  public String toString() {
    return mod.getProperties().getName();
  }
}
