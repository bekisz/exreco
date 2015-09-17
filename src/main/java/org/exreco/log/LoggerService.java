package org.exreco.log;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.exreco.experiment.Exreco;
import org.exreco.experiment.util.LiffUtils;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.EventTopicHomeBase;
import org.exreco.experiment.util.events.LiffEventListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LoggerService extends Thread implements LiffEventListener {
	private final static Logger loggerServiceLogger = LogManager.getLogger(LoggerService.class);
	private LoggerContext loggerContext;
	// private Logger logger;
	private String configLocation = "log4j2-central.xml";

	private final static String defaultName = LoggerService.class.getName();
	private EventTopicHome eventTopicHome;
	public static final String DEFAULT_DESTINATION_NAME = "Log4j2Events";
	private String topicName = DEFAULT_DESTINATION_NAME;

	public LoggerService() {
		super(defaultName);

	}

	public LoggerService(String name) {
		super(name);

	}

	public LoggerService(String name, EventTopicHome eventTopicHome) {

		super(name);
		this.eventTopicHome = eventTopicHome;
	}

	public void init() throws URISyntaxException {
		this.loggerContext = new LoggerContext(this.getName());
		loggerContext.setConfigLocation(new URI(configLocation));
		try {
			if (this.getEventTopicHome() != null) {
				EventSource logEventSource = this.getEventTopicHome().getEventSource(DEFAULT_DESTINATION_NAME);
				logEventSource.wireTo(this);
			} else {
				loggerServiceLogger.error("Could not wire log4JEvents to loggerService. EventTopicHome is not set.");

			}

		} catch (Exception e) {
			loggerServiceLogger.error("Could not wire log4JEvents to loggerService.");
			// e.printStackTrace();
		}

	}

	@Override
	public void eventOccurred(Serializable event) throws Exception {

		if (event instanceof LogEvent) {
			LogEvent logEvent = (LogEvent) event;

			Map<String, Appender> appenderMap = loggerContext.getConfiguration().getAppenders();
			for (Appender appender : appenderMap.values()) {
				appender.append(logEvent);
			}

		} else {
			loggerServiceLogger.warn("Other than LogEvent was recieved.");
		}

	}

	public void run() {
		loggerServiceLogger.debug("Distrubited logger service server started");
		try {
			while (true) {
				try {
					synchronized (this) {
						wait();
					}

				} catch (InterruptedException e) {

				}
			}
		} catch (Exception e) {
			loggerServiceLogger.error("Exiting " + this.getName() + " thread. ");
			System.out.println("Exception...");

		}
		System.out.println("Run ended...");

	}

	public String getConfigLocation() {
		return configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	public LoggerContext getLoggerContext() {
		return loggerContext;
	}

	public EventTopicHome getEventTopicHome() {
		return eventTopicHome;
	}

	public void setEventTopicHome(EventTopicHome eventTopicHome) {
		this.eventTopicHome = eventTopicHome;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public static void main(String[] args) throws Exception {

		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("logger-service-beans.xml");
		LoggerService service = (LoggerService) context.getBean("loggerService");
		service.init();
		service.run();

	}

}