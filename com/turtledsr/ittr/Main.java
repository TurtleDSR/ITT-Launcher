package com.turtledsr.ittr;

import com.turtledsr.ittr.timer.Autosplitter;
import com.turtledsr.ittr.timer.TimerHandler;

public final class Main {
  private static boolean timerHandlerConnected = false;
  private static boolean processConnected = false;
  public static void main(String[] args) throws Exception {
    TimerHandler.connect();

    while(true) {
      if(!TimerHandler.socket.isClosed() && !processConnected) {
        processConnected = true;
        try{Autosplitter.start();} catch(Exception e) {
          System.err.println(e.getMessage());
          processConnected = false;
        }
      }

      if(TimerHandler.socket.isClosed() == timerHandlerConnected) {
        timerHandlerConnected = !TimerHandler.socket.isClosed();

        if(TimerHandler.socket.isClosed()) {
          Autosplitter.stop();
          processConnected = false;
        }
      }

      if(processConnected) Autosplitter.tick();
    }
  }
}
