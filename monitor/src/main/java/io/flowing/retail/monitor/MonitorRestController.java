package io.flowing.retail.monitor;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorRestController {
  
  @RequestMapping(path = "/event", method = GET)
  public Map<String, List<PastEvent>> getAllEvents() {
    return LogRepository.instance.getAllPastEvents();    
  }

}