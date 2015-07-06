package org.exreco.experiment.util.events;

import java.io.Serializable;

public class LiffEventListenerProxy<EventType> implements
		LiffEventListener<EventType>,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4432810300093943384L;
	final private LiffEventListener<EventType> proxied;

	public LiffEventListenerProxy(LiffEventListener<EventType> proxied) {
		super();
		this.proxied = proxied;
	}

	protected LiffEventListenerProxy() {
		super();
		this.proxied = null;
	}

	//@Override
	public void eventOccurred(EventType event) throws Exception {

		this.getProxiedEventListner().eventOccurred(event);
	}

	/**
	 * @return the proxied
	 */
	public LiffEventListener<EventType> getProxiedEventListner() {
		return proxied;
	}

}
