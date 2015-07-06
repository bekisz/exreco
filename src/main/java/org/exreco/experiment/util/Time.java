package org.exreco.experiment.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.persistence.Embeddable;
@Embeddable
public class Time implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1238738078577555199L;
	private long timeInMs;

	public Time() {
		this.timeInMs = 0;
	}

	public Time(long timeInMs) {
		this.timeInMs = timeInMs;
	}

	public long getMilliseconds() {
		return this.timeInMs;
	}

	public int getDays() {
		return (int) (this.timeInMs / 1000 / 60 / 60 / 24);
	}

	public int getHours() {
		return (int) (this.timeInMs / 1000 / 60 / 60);
	}

	public int getMinutes() {
		return (int) (this.timeInMs / 1000 / 60);
	}

	public long getSeconds() {
		return this.timeInMs / 1000;
	}

	@Override
	public synchronized String toString() {
		Locale loc = Locale.US;
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("00");
		int days = this.getDays();
		String dayString = (days == 0) ? "" : String.valueOf(days) + " days ";
		int hours = this.getHours() % 24;
		String hourString = (days == 0 && hours == 0) ? "" : String
				.valueOf(hours) + " hours ";
		int minutes = this.getMinutes() % 60;
		String minString = (minutes == 0) ? "" : df.format(minutes) + " mins ";
		String secString = df.format(this.getSeconds() % 60) + " s ";

		if (days > 6) {
			hourString = "";

		}

		if (days > 0) {
			minString = "";
			secString = "";
		} else if (days == 0 && hours > 0) {

			secString = "";
		}
		return dayString + hourString + minString + secString;

	}

	protected void setTimeInMs(long timeInMs) {
		this.timeInMs = timeInMs;
	}

}
