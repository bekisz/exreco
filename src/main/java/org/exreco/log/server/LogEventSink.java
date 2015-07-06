package org.exreco.log.server;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LogEventListener;

public class LogEventSink extends LogEventListener {
	@Override
	public void log(LogEvent event) {
		super.log(event);
	};

}