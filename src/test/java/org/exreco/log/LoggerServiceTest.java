package org.exreco.log;


import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.exreco.experiment.jms.JmsEventTopicHome;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.LocalEventTopicHome;
import org.exreco.log.LoggerService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class LoggerServiceTest {
	private LoggerContext loggerContext;
	private final static String configLocation = "log4j2-loggerServiceTest.xml";
	private final static String centralConfigLocation = "log4j2-central.xml";
	
	private final static String defaultName = LoggerService.class.getName();

	public static final String DEFAULT_DESTINATION_NAME = "Log4j2Events";

	public void init() throws URISyntaxException {
		this.loggerContext = new LoggerContext(this.getClass().getName());
		loggerContext.setConfigLocation(new URI(configLocation));

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.init();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public void testOnJms() throws Exception {
		EventTopicHome eventTopicHome = new JmsEventTopicHome();
		// EventListener loggerTopic =
		// eventTopicHome.getEventListener(LoggerService.DEFAULT_DESTINATION_NAME);
		Logger logger = loggerContext.getLogger(this.getClass().getCanonicalName());
		logger.debug("This is a test message");

	}

	@Test
	public void testOnLocal() throws Exception {
		EventTopicHome eventTopicHome = new LocalEventTopicHome();
		// EventListener loggerTopic =
		// eventTopicHome.getEventListener(LoggerService.DEFAULT_DESTINATION_NAME);
		EventSource logEventSource = eventTopicHome.getEventSource(LoggerService.DEFAULT_DESTINATION_NAME);

		LoggerService loggerService = new LoggerService();
		logEventSource.wireTo(loggerService);
		loggerService.setConfigLocation(centralConfigLocation);
		loggerService.init();
		loggerService.start();
		synchronized (this) {

			wait(1000);
		}
		Logger logger = loggerContext.getLogger(this.getClass().getCanonicalName());
		logger.debug("This is a test message");

	}

}
