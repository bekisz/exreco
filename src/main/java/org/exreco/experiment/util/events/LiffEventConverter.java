package org.exreco.experiment.util.events;

import java.io.Serializable;

public abstract class LiffEventConverter<EventType> extends
		LiffEventListenerProxy<EventType> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1394533093066419265L;

	public LiffEventConverter(LiffEventListener<EventType> proxied) {
		super(proxied);

	}

	protected LiffEventConverter() {
		super();

	}

	abstract protected EventType convert(EventType event);

	@Override
	public void eventOccurred(EventType event) throws Exception {
		EventType convertedEvent = this.convert(event);
		super.eventOccurred(convertedEvent);
	}

}
