package org.exreco.experiment.util.events;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TwoWayEventListenerProxy<EventType> extends
		LiffEventListenerProxy<EventType> implements Serializable {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager
			.getLogger(TwoWayEventListenerProxy.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 565087394477561027L;
	final private LiffEventListener<EventType> alternateProxied;

	public TwoWayEventListenerProxy(LiffEventListener<EventType> defaultWay,
			LiffEventListener<EventType> alternateProxied) {
		super(defaultWay);
		this.alternateProxied = alternateProxied;

	}

	protected abstract boolean isSwitched(EventType event);

	@Override
	public void eventOccurred(EventType event) throws Exception {
		if (this.isSwitched(event)) {
			alternateProxied.eventOccurred(event);
		} else {
			super.eventOccurred(event);

		}
	}

	/**
	 * @return the alternateProxied
	 */
	public LiffEventListener<EventType> getAlternateProxied() {
		return alternateProxied;
	}
}
