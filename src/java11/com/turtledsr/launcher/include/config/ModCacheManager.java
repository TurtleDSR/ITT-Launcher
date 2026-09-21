/*
Mod Persistence class

Handles opening, reading and updating of the modpersistence json file.
*/

package com.turtledsr.launcher.include.config;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.turtledsr.launcher.include.control.Process;
import com.turtledsr.launcher.include.engine.Logs;

public final class ModCacheManager {
  public static ModCache cache;

  private static ObjectMapper mapper = new ObjectMapper();
  private static File cacheFile = new File(Process.getGameDirectory(), "Launcher/modlist.cache");

  private static ShutdownHook hook;

  public static void loadCache() {
    loadCache(true);
  }

  public static void loadCache(boolean shutdownHook) {
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    if(shutdownHook && hook == null) {
      hook = new ShutdownHook();
      Runtime.getRuntime().addShutdownHook(hook);
    }

    try{
      if(!cacheFile.exists()) {
        cache = new ModCache();

        mapper.writeValue(cacheFile, cache);
      } else {
        cache = mapper.readValue(cacheFile, ModCache.class);
        if(cache == null) {
          Logs.logError("Failed to read modcache: cache is null", "MOD_PERSISTENCE_MANAGER");
          cache = new ModCache();
        }
      }
    } catch (Exception e) {
      Logs.logError("Failed to read modcache: " + e.getMessage(), "MOD_PERSISTENCE_MANAGER");
      cache = new ModCache();
    }
  }

  public static void writeCache() {
    try {
      mapper.writeValue(cacheFile, cache);
    } catch (Exception e) {
      Logs.logError("Failed to write modcache to drive: " + e.getMessage(), "MOD_PERSISTENCE_MANAGER");
    }
  }

  static class ShutdownHook extends Thread {
    public void run() {
      writeCache();
    }
  }
}