package org.exreco.experiment.util.events;

import java.io.Serializable;
import java.util.HashMap;




public class EventMap<EventType> extends
		HashMap<Class<? extends EventType>, LiffEventListener<EventType>>
		implements 
		LiffEventListener<EventType>, Serializable {

	

	private static final long serialVersionUID = 8307434900509022792L;

	

	@Override
	public void eventOccurred(EventType event) throws Exception {

		Class<?> eventClass = event.getClass();

		LiffEventListener<EventType> eventHandler = this.get(eventClass);

		if (eventHandler != null) {

			eventHandler.eventOccurred(event);

		}

	}
	public HashMap<Class<? extends EventType>, LiffEventListener<EventType>> getTheEventMap() {
		return this;
	}

	public void setTheEventMap(
			HashMap<Class<? extends EventType>, LiffEventListener<EventType>> theEventMap) {
		this.clear();
		this.putAll(theEventMap);
	}

}
