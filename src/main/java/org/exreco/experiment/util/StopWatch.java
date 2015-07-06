package org.exreco.experiment.util;

import java.io.Serializable;
import java.util.Date;

public class StopWatch extends Time implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5940647179952420228L;
	public Date date = null;

	public synchronized void start() {
		if (date == null) {
			date = new Date();
		}
	}

	public synchronized void stop() {
		if (date != null) {

			Date currentDate = new Date();
			long time = this.getMilliseconds();

			time += (currentDate.getTime() - date.getTime());
			this.setTimeInMs(time);
			date = null;
		}
	}

	public synchronized boolean isRunning() {
		return (this.date != null);
	}

	public synchronized void touch() {
		if (this.date != null) {

			this.stop();
			this.start();
		}
	}

	public void displayMeasuredTime() {
		System.out.println("\nStoppered Time  : " + this.toString());
	}

	synchronized public void reset() {
		this.date = new Date();
		this.setTimeInMs(0);
	}

	synchronized public boolean isOver(long ms) {
		this.touch();
		return (this.getMilliseconds() > ms);
	}

	synchronized public boolean resetifIsOver(long ms) {
		if (this.isOver(ms)) {
			this.reset();
			return true;
		}
		return false;
	}
}
