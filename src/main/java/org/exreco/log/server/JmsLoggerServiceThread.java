package org.exreco.log.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.exreco.experiment.util.LiffUtils;

public class JmsLoggerServiceThread extends Thread {
	private final Logger logger = LogManager.getLogger(JmsLoggerServiceThread.class
			.getName());
	private JmsLoggingService service = new JmsLoggingService();

	JmsLoggerServiceThread() {
		super("JmsLoggerServiceThread");
	}
	public void run() {
		ThreadContext.put("ROUTINGKEY","jmsLoggerService");
		
		try {
			this.getService().accept();
		} catch (Exception e) {
			logger.error("Exiting " + this.getName() + " thread. " );
		}

		
	}
	public JmsLoggingService getService() {
		return service;
	}
	
}