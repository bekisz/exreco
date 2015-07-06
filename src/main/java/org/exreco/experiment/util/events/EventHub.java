package org.exreco.experiment.util.events;

import java.io.Serializable;

public class EventHub<EventType extends Serializable> extends
		EventSource<EventType> implements LiffEventListener<EventType>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3515107528859740130L;

	@Override
	public void eventOccurred(EventType event) {
		this.fireEvent(event);

	}

}
