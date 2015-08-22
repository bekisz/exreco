package org.exreco.experiment.util.events;

import java.io.Serializable;

public abstract class LiffEventConverter<EventType> extends
		LiffEventListenerProxy implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1394533093066419265L;

	public LiffEventConverter(LiffEventListener proxied) {
		super(proxied);

	}

	protected LiffEventConverter() {
		super();

	}

	abstract protected Serializable convert(Serializable event);

	@Override
	public void eventOccurred(Serializable event) throws Exception {
		Serializable convertedEvent = this.convert(event);
		super.eventOccurred(convertedEvent);
	}

}
