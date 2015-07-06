package org.exreco.experiment.util.events;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Source of events. Fires the EventType to all listeners.
 * 
 * @param <EventType>
 */
public class EventSource<EventType extends Serializable> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4741010987757902893L;
	private final List<LiffEventListener<EventType>> listeners;
	private static Logger logger = LogManager.getLogger(EventSource.class
			.getName());

	public EventSource() {
		this.listeners = Collections
				.synchronizedList(new LinkedList<LiffEventListener<EventType>>());
		// this.listeners = new LinkedList<LiffEventListener<EventType>>();
	}

	public EventSource(List<LiffEventListener<EventType>> listeners) {
		this.listeners = Collections.synchronizedList(listeners);
	}

	public void fireEvent(EventType event) {
		synchronized (listeners) {
			for (LiffEventListener<EventType> listener : this.listeners) {
				try {
					listener.eventOccurred(event);
				} catch (Exception e) {
					logger.warn("Exception occured on event propagation. ", e);
				}
			}

		}
	}

	/**
	 * @return the listeners
	 */
	public List<LiffEventListener<EventType>> getListeners() {
		return this.listeners;
	}
}
