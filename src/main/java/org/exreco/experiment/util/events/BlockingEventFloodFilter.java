package org.exreco.experiment.util.events;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.util.StopWatch;


/**
 * Filters the events so that the proxied event filter gets less messages.
 * 
 * The filter sleeps till its sleep time ends and during this period all
 * messages are ignored. After sleep time first single message is forwarded to
 * the proxied event handler, and again it starts sleeping.
 * 
 */
public class BlockingEventFloodFilter<EventType extends Serializable> extends
		LiffEventListenerProxy<EventType> implements
		LiffEventListener<EventType>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5739906743671983891L;

	@SuppressWarnings("unused")
	private static Logger logger = LogManager
			.getLogger(BlockingEventFloodFilter.class.getName());

	private final int sleepTime;
	private final StopWatch stopWatch = new StopWatch();

	/**
	 * @return the stopWatch
	 */
	public StopWatch getStopWatch() {
		return stopWatch;
	}

	public BlockingEventFloodFilter(LiffEventListener<EventType> proxied) {
		super(proxied);
		this.sleepTime = 100;
		this.init();

	}

	public BlockingEventFloodFilter(LiffEventListener<EventType> proxied,
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

	@Override
	synchronized public void eventOccurred(EventType event) throws Exception {
		synchronized (this.stopWatch) {

			if (!this.getStopWatch().isRunning()) { // lazy init
				super.eventOccurred(event);
				this.getStopWatch().start();

			} else {
				if (this.getStopWatch().resetifIsOver(this.getSleepTime())) {
					// logger.debug("Over sleep time");
					super.eventOccurred(event);
				}
			}
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();

		this.init();

	}

}
