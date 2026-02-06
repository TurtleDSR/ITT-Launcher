/*
stores a mod name and if its toggled on or not
*/
package com.turtledsr.launcher.include.struct;

public final class Mod {
  public String name;
  public boolean toggled;

  public Mod(String name, boolean toggled) {
    this.name = name;
    this.toggled = toggled;
  }

  public Mod(String name) {
    this.name = name;
    this.toggled = false;
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
}
