package org.exreco.experiment.util.events;

import java.io.Serializable;


public interface EventBean<EventType extends Serializable> extends LiffEventListener<EventType>,  Serializable {
	
	EventSource<EventType> getEventSource();

	
}

