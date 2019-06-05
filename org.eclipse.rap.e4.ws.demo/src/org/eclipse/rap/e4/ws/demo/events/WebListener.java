package org.eclipse.rap.e4.ws.demo.events;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(
	    property = {
	        EventConstants.EVENT_TOPIC + "=" + "org/osgi/service/web/DEPLOYING",
	        EventConstants.EVENT_TOPIC + "=" + "org/osgi/service/web/DEPLOYED",
	        EventConstants.EVENT_TOPIC + "=" + "org/osgi/service/web/UNDEPLOYING",
	        EventConstants.EVENT_TOPIC + "=" + "org/osgi/service/web/UNDEPLOYED",
	        EventConstants.EVENT_TOPIC + "=" + "org/osgi/service/web/FAILED"
	     }
)
public class WebListener implements EventHandler{

	@Override
	public void handleEvent(Event event) {
		System.out.println(event.getTopic());
		Arrays.asList(event.getPropertyNames()).forEach(p->System.out.println(String.format("%s=%s",p,event.getProperty(p))));
		
	}

}
