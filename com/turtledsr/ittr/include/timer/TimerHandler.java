/*
Handles the socket connection between ITTR and Livesplit

Connects to the Livesplit TCP socket to control the timer
*/

package com.turtledsr.ittr.include.timer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.turtledsr.ittr.Main;
import com.turtledsr.ittr.include.engine.Logs;

public final class TimerHandler {
  public static boolean awaitingReconnection = false; 

  public static Socket socket;

  private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private static PrintWriter out;
  private static BufferedReader in;

  public static void connect() {
    try{
      socket = new Socket("localhost", 16834);
      socket.setSoTimeout(1000);

      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      out.print("switchto gametime\r\n"); //force IGT
      out.flush();

      awaitingReconnection = false;
    } catch(Exception e) {
      Logs.logError(e.getMessage(), "TIMERHANDLER");
      scheduleReconnect(Main.RECONNECTION_INTERVAL);
    }
  }

  public static void scheduleReconnect(int time) {
    awaitingReconnection = true;
    executor.schedule(new Runnable() {
      @Override
      public void run() {
        try{
          if(socket != null) {
            in = null;
            out = null;
            socket.close();
          }
          connect();
        } catch(Exception e) {
          Logs.logError(e.getMessage(), "TIMERHANDLER");
        }
      }
    }, time, TimeUnit.MILLISECONDS);
    System.out.println("[TimerHandler]: Scheduling reconnection attempt in " + time + "ms");
    Logs.log("Scheduling reconnection attempt in " + time + "ms", "TIMERHANDLER");
  }

  public static int getCurrentIndex() {
    if(out == null) return -1;

    out.print("getsplitindex\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}

    int split = -1;

    try{
      split = Integer.parseInt(in.readLine());
    } catch (Exception e) {
      Logs.logError(e.getMessage(), "TIMERHANDLER");
    }

    return split;
  }

  public static void split() {
    if(out == null) return;

    Logs.log("SPLITTING", "TIMERHANDLER");

    out.print("split\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void unsplit() {
    if(out == null) return;

    Logs.log("UNSPLITTING", "TIMERHANDLER");

    out.print("unsplit\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void start() {
    if(out == null) return;

    Logs.log("STARTING", "TIMERHANDLER");

    out.print("starttimer\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void pause() {
    if(out == null) return;

    Logs.log("PAUSING", "TIMERHANDLER");

    out.print("pause\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void pauseGameTime() {
    if(out == null) return;

    Logs.log("PAUSING GAMETIME", "TIMERHANDLER");

    out.print("pausegametime\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void unpauseGameTime() {
    if(out == null) return;

    Logs.log("UNPAUSING GAMETIME", "TIMERHANDLER");

    out.print("unpausegametime\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void reset() {
    if(out == null) return;

    Logs.log("RESETTING", "TIMERHANDLER");

    out.print("reset\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }
}
