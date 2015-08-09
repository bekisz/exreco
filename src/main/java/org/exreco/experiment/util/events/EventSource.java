package org.exreco.experiment.util.events;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Source of events. Fires the EventType to all listeners.
 * 
 * @param <EventType>
 */
public class EventSource<EventType extends Serializable> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4741010987757902893L;
	private final List<LiffEventListener<EventType>> listeners;
	private static Logger logger = LogManager.getLogger(EventSource.class.getName());
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public EventSource() {
		this.listeners = new LinkedList<LiffEventListener<EventType>>();
	}

	public EventSource(List<LiffEventListener<EventType>> listeners) {
		this.listeners = listeners;
	}

	public LiffEventListener<EventType> wireTo(LiffEventListener<EventType> to) {
		this.getLock().writeLock().lock();
		try {

			this.getListeners().add(to);
		} finally {
			this.getLock().writeLock().unlock();
		}

		return to;
	}

	public boolean unwire(LiffEventListener<EventType> to) {

		boolean contains = false;

		this.getLock().writeLock().lock();
		try {
			contains = this.getListeners().remove(to);
		} finally {
			this.getLock().writeLock().unlock();
		}
		return contains;

	}

	public void unwireAll() {
		this.getLock().writeLock().lock();
		try {
			this.listeners.clear();
		} finally {
			this.getLock().writeLock().unlock();
		}
	}

	public void wireTo(List<LiffEventListener<EventType>> listTo) {

		this.getLock().writeLock().lock();
		try {

			for (LiffEventListener<EventType> to : listTo) {

				this.wireTo(to);
			}
		} finally {
			this.getLock().writeLock().unlock();
		}
	}

	public void fireEvent(EventType event) {
		this.getLock().readLock().lock();
		try {

			for (LiffEventListener<EventType> listener : this.listeners) {

				listener.eventOccurred(event);
			}

		} catch (Exception e) {
			logger.error("Exception occured on event propagation. ", e);
			throw new RuntimeException(e);
		} finally {
			this.getLock().readLock().unlock();
		}

	}

	/**
	 * @return the listeners
	 */
	public List<LiffEventListener<EventType>> getListeners() {
		//return new LinkedList<LiffEventListener<EventType>>(this.listeners);
		return this.listeners;
	}

	public ReentrantReadWriteLock getLock() {
		return lock;
	}

}
