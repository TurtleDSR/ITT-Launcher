package com.turtledsr.ittr;

import com.turtledsr.ittr.process.Process;
import com.turtledsr.ittr.timer.Autosplitter;
import com.turtledsr.ittr.timer.TimerHandler;

public final class Main {
  public static void main(String[] args) throws Exception {
    TimerHandler.connect();
    while(true) {
      tick();
    }
  }

  private static void tick() {
    if(Process.getProcessPID("Livesplit.exe").isEmpty()) {
      if(!TimerHandler.awaitingReconnection) {
        TimerHandler.scheduleReconnect(TimerHandler.reconnectionInterval);
      }   
      return;
    }

    if(Autosplitter.process == null) {
      try{Autosplitter.run();} 
      catch(Exception e) {
        System.err.println(e.getMessage());
        return;
      }
    }

    Autosplitter.tick();
  }
}
