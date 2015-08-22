package org.exreco.experiment.util.events;

import java.io.Serializable;

public class EventHub extends
		EventSource implements LiffEventListener,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3515107528859740130L;

	@Override
	public void eventOccurred(Serializable event) {
		this.fireEvent(event);

	}

}
