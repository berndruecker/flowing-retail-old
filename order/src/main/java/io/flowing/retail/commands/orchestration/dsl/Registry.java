package io.flowing.retail.commands.orchestration.dsl;

import java.util.HashMap;
import java.util.Map;

public class Registry {

  private static Map<String, EventInput> mappings = new HashMap<String, EventInput>();

  public static void add(String nodeId, EventInput eventInput) {
    mappings.put(nodeId, eventInput);
  }

  public static EventInput get(String nodeId) {
    return mappings.get(nodeId);    
  }
}
