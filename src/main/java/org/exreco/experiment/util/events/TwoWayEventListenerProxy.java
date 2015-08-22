package org.exreco.experiment.util.events;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TwoWayEventListenerProxy extends
		LiffEventListenerProxy implements Serializable {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager
			.getLogger(TwoWayEventListenerProxy.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 565087394477561027L;
	final private LiffEventListener alternateProxied;

	public TwoWayEventListenerProxy(LiffEventListener defaultWay,
			LiffEventListener alternateProxied) {
		super(defaultWay);
		this.alternateProxied = alternateProxied;

	}

	protected abstract boolean isSwitched(Serializable event);

	@Override
	public void eventOccurred(Serializable event) throws Exception {
		if (this.isSwitched(event)) {
			alternateProxied.eventOccurred(event);
		} else {
			super.eventOccurred(event);

		}
	}

	/**
	 * @return the alternateProxied
	 */
	public LiffEventListener getAlternateProxied() {
		return alternateProxied;
	}
}
