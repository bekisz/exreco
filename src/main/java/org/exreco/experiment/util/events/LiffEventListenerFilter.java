package org.exreco.experiment.util.events;

import java.io.Serializable;

public abstract class LiffEventListenerFilter<EventType> extends
		LiffEventListenerProxy<EventType> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 565087394477561027L;

	public LiffEventListenerFilter(LiffEventListener<EventType> proxied) {
		super(proxied);

	}

	protected abstract boolean isFilteredOut(EventType event);

	@Override
	public void eventOccurred(EventType event) throws Exception {
		if (!this.isFilteredOut(event)) {
			super.eventOccurred(event);
		}
	}
}
