/*
stores a mod name and if its toggled on or not
*/
package com.turtledsr.launcher.include.struct;

public final class Mod {
  public String name;
  public boolean toggled;
  public Format format;

  public Mod(String name, boolean toggled, Format format) {
    this.name = name;
    this.toggled = toggled;
    this.format = format;
  }

  public Mod(String name, boolean toggled) {
    this(name, toggled, Format.asmod);
  }

  public Mod(String name) {
    this(name, false, Format.asmod);
  }

  public Mod() {
    //unimplemented
  }

  @Override
  public boolean equals(Object o) {
    if(o == null || o.getClass() != this.getClass()) return false; 
    return (name != null) ? name.equals(((Mod) o).name) : ((Mod) o).name == null;
  }

  @Override
  public String toString() {
    return name;
  }

  public enum Format {
    asmod,
    zip
  }
}
