package org.exreco.experiment.util.events;

import java.io.Serializable;
import java.util.HashMap;




public class EventMap extends
		HashMap<Class<? extends Serializable>, LiffEventListener>
		implements 
		LiffEventListener, Serializable {

	

	private static final long serialVersionUID = 8307434900509022792L;

	

	@Override
	public void eventOccurred(Serializable event) throws Exception {

		Class<?> eventClass = event.getClass();

		LiffEventListener eventHandler = this.get(eventClass);

		if (eventHandler != null) {

			eventHandler.eventOccurred(event);

		}

	}
	public HashMap<Class<? extends Serializable>, LiffEventListener> getTheEventMap() {
		return this;
	}

	public void setTheEventMap(
			HashMap<Class<? extends Serializable>, LiffEventListener> theEventMap) {
		this.clear();
		this.putAll(theEventMap);
	}

}
