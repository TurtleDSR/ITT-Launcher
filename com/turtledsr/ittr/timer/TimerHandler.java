package com.turtledsr.ittr.timer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TimerHandler {
  public static boolean awaitingReconnection = false; 

  public static final int reconnectionInterval = 500;
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
      System.err.println(e.getMessage());
      scheduleReconnect(reconnectionInterval);
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
          System.err.println(e.getMessage());
        }
      }
    }, time, TimeUnit.MILLISECONDS);
    System.out.println("Scheduling reconnection attempt in " + time + "ms");
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
      System.err.println(e.getMessage());
    }

    return split;
  }

  public static void split() {
    if(out == null) return;

    System.out.println("SPLITTING");

    out.print("split\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void unsplit() {
    if(out == null) return;

    System.out.println("UNSPLITTING");

    out.print("unsplit\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void start() {
    if(out == null) return;

    System.out.println("STARTING");

    out.print("starttimer\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void pause() {
    if(out == null) return;

    System.out.println("PAUSING");

    out.print("pause\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void pauseGameTime() {
    if(out == null) return;

    System.out.println("PAUSING GAMETIME");

    out.print("pausegametime\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void unpauseGameTime() {
    if(out == null) return;

    System.out.println("UNPAUSING GAMETIME");

    out.print("unpausegametime\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }

  public static void reset() {
    if(out == null) return;

    System.out.println("RESETTING");

    out.print("reset\r\n");
    out.flush();

    try{Thread.sleep(10);} catch(InterruptedException e) {}
  }
}
