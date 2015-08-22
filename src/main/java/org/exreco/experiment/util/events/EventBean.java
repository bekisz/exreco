package org.exreco.experiment.util.events;

import java.io.Serializable;


public interface EventBean extends LiffEventListener,  Serializable {
	
	EventSource getEventSource();

	
}

