package timer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerHandler {
  public static int reconnectionInterval = 500;

  private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private Socket socket;

  private PrintWriter out;
  private BufferedReader in;

  public TimerHandler() throws Exception {
    connect();
  }

  public void connect() {
    try{
      socket = new Socket("localhost", 16834);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch(Exception e) {
      System.err.println(e.getMessage());
      scheduleReconnect(reconnectionInterval);
    }
  }

  public void scheduleReconnect(int time) {
    executor.schedule(new Runnable() {
      @Override
      public void run() {
        try{
          if(socket != null) {
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

  public String GetCurrentSplitName() {
    out.print("getcurrentsplitname\r\n");
    out.flush();

    String split = "ERR";

    try{
      split = in.readLine();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    return split;
  }
}
