import java.util.Timer;
import java.util.TimerTask;

import timer.TimerHandler;

public class Main {
  public static Timer timer = new Timer();
  public static void main(String[] args) throws Exception {
    TimerHandler handler = new TimerHandler();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        System.out.println(handler.GetCurrentSplitName());
      }
    }, 0, 20);
  }
}
