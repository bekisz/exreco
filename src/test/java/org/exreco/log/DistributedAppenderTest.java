package org.exreco.log;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.exreco.experiment.jms.JmsEventTopicHome;
import org.exreco.experiment.log.DisributedLoggingUtils;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.LocalEventTopicHome;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class DistributedAppenderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOnJms() throws Exception {
	//	EventTopicHome eventTopicHome = new JmsEventTopicHome();
		//Configuration conf = DistributedAppender.createNewConfig();
		// Logger logger = conf.getLoggers().get(LogManager.ROOT_LOGGER_NAME);
		Logger logger = LogManager.getLogger();
		logger.debug("Config file loaded");

	}

	@Test
	@Ignore

	public void testOnLocal() throws Exception {
	

	}
	


}
