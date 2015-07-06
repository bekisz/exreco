package org.exreco.experiment.util.events;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Filters the events so that the proxied event handler gets less messages. This
 * filter sleeps till its sleep time over and meanwhile stores the lastest
 * message. Once sleep time is over the last message (if exists) is propagated.
 * In case the there were no messages during the sleep time next the incoming
 * message will be proagated immediately. (not after the sleep time is over)
 * 
 */
public class DelayingEventFloodFilter<EventType extends Serializable> extends
		LiffEventListenerProxy<EventType> implements
		 Serializable {
	protected class BackgroundThread extends Thread {

		@Override
		public void run() {
			DelayingEventFloodFilter.this.setAllowedToPropagate(true);

			while (!DelayingEventFloodFilter.this.isTerminated()) {
				try {
					Thread.sleep(DelayingEventFloodFilter.this.getSleepTime());
				} catch (InterruptedException e) {

				} finally {
				}
				synchronized (DelayingEventFloodFilter.this) {
					DelayingEventFloodFilter.this.setAllowedToPropagate(false);

					try {
						EventType lastEvent;
						lastEvent = DelayingEventFloodFilter.this
								.getLastChachedEvent();
						if (lastEvent != null) {
							DelayingEventFloodFilter.this
									.setLastCachedEvent(null);
							DelayingEventFloodFilter.this
									.getProxiedEventListner().eventOccurred(
											lastEvent);

						} else {
							DelayingEventFloodFilter.this
									.setAllowedToPropagate(true);
						}
					} catch (Exception e) {
						logger.throwing(Level.ERROR, e);
					}
				}
			}
		}
	};

	private static Logger logger = LogManager
			.getLogger(DelayingEventFloodFilter.class.getName());

	transient private Thread backgroundThread;
	transient private EventType lastChachedEvent;

	transient private boolean allowedToPropagate;
	transient private boolean terminated;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6039918716532281796L;

	private final int sleepTime;

	public DelayingEventFloodFilter(LiffEventListener<EventType> proxied) {
		super(proxied);
		this.sleepTime = 30;
		this.init();

	}

	public DelayingEventFloodFilter(LiffEventListener<EventType> proxied,
			int sleepTime) {
		super(proxied);

		this.sleepTime = sleepTime;
		this.init();
	}

	private void init() {
		// this.timer = new Timer(this.getClass().getName(), false);

	}

	/**
	 * @return the sleepTime
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * @return the lastChachedEvent
	 */
	synchronized public EventType getLastChachedEvent() {
		return lastChachedEvent;
	}

	/**
	 * @param lastChachedEvent
	 *            the lastChachedEvent to set
	 */
	synchronized public void setLastCachedEvent(EventType lastChachedEvent) {
		this.lastChachedEvent = lastChachedEvent;
	}

	@Override
	synchronized public void eventOccurred(EventType event) throws Exception {
		this.setLastCachedEvent(event);
		if (this.getBackgroundThread() == null) {
			super.eventOccurred(event);
			this.setLastCachedEvent(null);
			Thread t = new BackgroundThread();
			t.start();
			this.setBackgroundThread(t);

		} else if (this.isAllowedToPropagate()) {
			this.getBackgroundThread().interrupt();
			// this.getBackgroundThread().
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();

		this.init();

	}

	/**
	 * @return the backgroundThread
	 */
	public Thread getBackgroundThread() {
		return backgroundThread;
	}

	/**
	 * @param backgroundThread
	 *            the backgroundThread to set
	 */
	public void setBackgroundThread(Thread backgroundThread) {
		this.backgroundThread = backgroundThread;
	}

	/**
	 * @return the terminated
	 */
	synchronized public boolean isTerminated() {
		return terminated;
	}

	/**
	 * @param terminated
	 *            the terminated to set
	 */
	synchronized public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}

	/**
	 * @return the allowedToPropagate
	 */
	synchronized public boolean isAllowedToPropagate() {
		return allowedToPropagate;
	}

	/**
	 * @param allowedToPropagate
	 *            the allowedToPropagate to set
	 */
	synchronized public void setAllowedToPropagate(boolean allowedToPropagate) {
		this.allowedToPropagate = allowedToPropagate;
	}

	@Override
	synchronized protected void finalize() {
		this.setTerminated(true);
	}
}
