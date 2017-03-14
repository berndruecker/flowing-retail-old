package io.flowing.retail.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogRepository {
  
  public static LogRepository instance = new LogRepository();
  
  private Map<String, List<PastEvent>> events = new HashMap<String, List<PastEvent>>();

  public Map<String, List<PastEvent>> getAllPastEvents() {
    return events;    
  }

  public void addEvent(PastEvent pastEvent) {
    if (!events.containsKey(pastEvent.getTransactionId())) {
      events.put(pastEvent.getTransactionId(), new ArrayList<PastEvent>());
    }
    events.get(pastEvent.getTransactionId()).add(pastEvent);
  }

}
