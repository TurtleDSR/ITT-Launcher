/*
Struct for holding a config value from the .ini config files the game uses
*/

package com.turtledsr.launcher.include.config.gameconfig;

public final class GameConfig {
  public String category;
  public String ownerObject;
  public String identifier;
  public String value;
  public ConfigType type;

  public enum ConfigType {
    _int,
    _float,
    _string,
    _boolean,
  }

  public GameConfig(String category, String ownerObject, String identifier, String value) {
    this.category = category;
    this.ownerObject = ownerObject;
    this.identifier = identifier;
    this.value = value;

    this.type = ConfigType._string;

    try{Float.parseFloat(value);
      this.type = ConfigType._float;
    } catch(NumberFormatException e) {}
    
    try{Integer.parseInt(value);
      this.type = ConfigType._int;
    } catch(NumberFormatException e) {}

    if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
      this.type = ConfigType._boolean;
    }
  }
}
