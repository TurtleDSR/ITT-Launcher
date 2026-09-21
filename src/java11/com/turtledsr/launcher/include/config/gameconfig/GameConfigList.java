/*
List that holds the config structs
*/

package com.turtledsr.launcher.include.config.gameconfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class GameConfigList extends LinkedHashMap<String, LinkedHashMap<String, ArrayList<GameConfig>>> {
  @Override
  public String toString() {
    String ret = "";
    Set<Map.Entry<String,LinkedHashMap<String,ArrayList<GameConfig>>>> set = this.entrySet();
    for (Map.Entry<String,LinkedHashMap<String,ArrayList<GameConfig>>> entry : set) {
      ret += entry.getKey() + "\n";

      Set<Map.Entry<String,ArrayList<GameConfig>>> set2 = entry.getValue().entrySet();
      for (Map.Entry<String,ArrayList<GameConfig>> entry2 : set2) {
        ret += ": " + entry2.getKey() + "\n";

        ArrayList<GameConfig> list = entry2.getValue();
        for (GameConfig config : list) {
          ret += "  : " + config.identifier + " = " + config.value + "\n";
        }
      }
    }

    return ret;
  }
}