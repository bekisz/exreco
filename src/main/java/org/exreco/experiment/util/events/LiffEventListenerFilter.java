package org.exreco.experiment.util.events;

import java.io.Serializable;

public abstract class LiffEventListenerFilter<EventType> extends
		LiffEventListenerProxy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 565087394477561027L;

	public LiffEventListenerFilter(LiffEventListener proxied) {
		super(proxied);

	}

	protected abstract boolean isFilteredOut(Serializable event);

	@Override
	public void eventOccurred(Serializable event) throws Exception {
		if (!this.isFilteredOut(event)) {
			super.eventOccurred(event);
		}
	}
}
