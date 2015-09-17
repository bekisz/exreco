package org.exreco.experiment;

import java.net.URI;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.log.DistributedAppender;

public class Deployment {

	private static Logger logger = LogManager.getLogger(Deployment.class.getName());
	private ExecutorService executor;
	private EventTopicHome eventTopicHome;
	private String log4j2ConfigLocation;

	public void init() throws Exception {
		logger.debug("Deployment init entered.");
		DistributedAppender.inject2LoggerContext(this.getLog4j2ConfigLocation(), this.getEventTopicHome());
		//URI log4j2ConfigLocationURI = new URI(this.log4j2ConfigLocation);
		//LoggerContext loggerContext = (LoggerContext) LogManager.getContext(true);
		// loggerContext.setConfigLocation(log4j2ConfigLocationURI);
		//logger = loggerContext.getLogger(Deployment.class.getName());
		logger.debug("//////////////Deployment init finished.");
	}

	/**
	 * @return the executor
	 */
	public ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	/**
	 * @return the eventTopicHome
	 */
	public EventTopicHome getEventTopicHome() {
		return eventTopicHome;
	}

	/**
	 * @param eventTopicHome
	 *            the eventTopicHome to set
	 */
	public void setEventTopicHome(EventTopicHome eventTopicHome) {
		this.eventTopicHome = eventTopicHome;
	}

	public String getLog4j2ConfigLocation() {
		return log4j2ConfigLocation;
	}

	public void setLog4j2ConfigLocation(String log4j2ConfigLocation) {
		this.log4j2ConfigLocation = log4j2ConfigLocation;
	}

}
