package org.exreco.experiment.util.events;

import java.util.EventListener;

public interface LiffEventListener<EventType> extends EventListener {

	void eventOccurred(EventType event) throws Exception;
}
