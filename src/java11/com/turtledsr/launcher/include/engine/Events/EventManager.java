/*
Handles events for the whole system
*/

package com.turtledsr.launcher.include.engine.Events;

import java.util.ArrayList;

public final class EventManager {
  private static ArrayList<ListenerInfo> listeners = new ArrayList<ListenerInfo>();

  public static void addListener(EventListener listener, String event) {
    listeners.add(new ListenerInfo(listener, event));
  }

  public static void addListener(EventListener listener, String event, boolean removeOnTrigger) {
    listeners.add(new ListenerInfo(listener, event, removeOnTrigger));
  }

  public static void removeListener(EventListener listener) {
    for (int i = 0; i < listeners.size(); i++) {
      if(listeners.get(i).listener == listener) listeners.remove(i);
    }
  }

  public static void triggerEvent(String event) {
    for (int i = 0; i < listeners.size(); i++) {
      if(listeners.get(i).event.equals(event)) {
        listeners.get(i).listener.eventTriggered();

        if(listeners.get(i).removeOnTrigger) {
          listeners.remove(i);
        }
      }
    }
  }
}

class ListenerInfo {
  EventListener listener;
  String event;
  boolean removeOnTrigger = false;

  public ListenerInfo(EventListener listener, String event) {
    this.event = event;
    this.listener = listener;
  }

  public ListenerInfo(EventListener listener, String event, boolean removeOnTrigger) {
    this.event = event;
    this.listener = listener;
    this.removeOnTrigger = removeOnTrigger;
  }
} 
