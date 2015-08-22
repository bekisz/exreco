package org.exreco.experiment.util.events;

import java.io.Serializable;

public class LiffEventListenerProxy implements
		LiffEventListener,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4432810300093943384L;
	final private LiffEventListener proxied;

	public LiffEventListenerProxy(LiffEventListener proxied) {
		super();
		this.proxied = proxied;
	}

	protected LiffEventListenerProxy() {
		super();
		this.proxied = null;
	}

	//@Override
	public void eventOccurred(Serializable event) throws Exception {

		this.getProxiedEventListner().eventOccurred(event);
	}

	/**
	 * @return the proxied
	 */
	public LiffEventListener getProxiedEventListner() {
		return proxied;
	}

}
