package org.exreco.experiment.log;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;


public class Log4j2ErrorCounter implements LiffEventListener, Serializable {
	private final Logger logger = LogManager.getLogger(Log4j2ErrorCounter.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 7503741239344317649L;
	
	
	List<LogEvent> loggedEvents = Collections.synchronizedList(new LinkedList<LogEvent>());
	@Override
	public void eventOccurred(Serializable event) throws Exception {
		if (event instanceof LogEvent) {
			LogEvent logEvent = (LogEvent) event;
			Level level = logEvent.getLevel();
			
			if (!level.isLessSpecificThan(Level.WARN)) {
				System.err.println("Level : " + level.name());
				this.getLoggedEvents().add(logEvent);
			}
		} else {
			logger.warn("Other than LogEvent was recieved.");
		}

	}
	public List<LogEvent> getLoggedEvents() {
		return loggedEvents;
	}

}
