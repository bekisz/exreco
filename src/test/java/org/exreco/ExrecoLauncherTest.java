package org.exreco;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.exreco.experiment.log.Log4j2ErrorCounter;
import org.exreco.experiment.util.events.EventSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ExrecoLauncherTest {

	private static Logger logger = LogManager.getLogger(ExrecoLauncherTest.class.getName());

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

	/**
	 * Smoke test having all run without GUI and all within one process. No
	 * JPPF, no activeMQ, still MySQL is needed to be started beforehand
	 * manually.
	 */
	@Test
	@Ignore
	public void singleNodeSmokeTest() {

		ExrecoLauncher exrecoLauncher = new ExrecoLauncher();
		exrecoLauncher.setExrecoBeansXmlFile("exreco-beans-single-node-smoke-test.xml");
		exrecoLauncher.init();
		
		Log4j2ErrorCounter errorCounter = new Log4j2ErrorCounter();

		try {
			 EventSource logEventSource = exrecoLauncher.getReplicatorCollider().getDeployment()
			 		.getEventTopicHome().getEventSource("Log4j2Events");
			logEventSource.wireTo(errorCounter);
		} catch (Exception e) {
			logger.error("Could not wire log4JEvents to ErrorCounter.");
			e.printStackTrace();
		}

		exrecoLauncher.run();
		int errors = errorCounter.getLoggedEvents().size();
	
		assertTrue("" + errors +" error log messages recieved.", errors == 0 );
		

		//ExrecoAssert.assertNoWarningOrMoreOccured();
		// exrecoLauncher.finish();
	}

	/**
	 * Smoke test having all run without GUI. It starts up and ActiveMQ and JPPF
	 * Driver and JPPF Node. MySQL is needed to be started beforehand manually.
	 */
	@Test
	public void multiNodeSmokeTest() {

		ExrecoLauncher exrecoLauncher = new ExrecoLauncher();
		exrecoLauncher.setExrecoBeansXmlFile("exreco-beans-multi-node-embedded-smoke-test.xml");
		exrecoLauncher.init();
		Log4j2ErrorCounter errorCounter = new Log4j2ErrorCounter();

		try {
			 EventSource logEventSource = exrecoLauncher.getReplicatorCollider().getDeployment()
			 		.getEventTopicHome().getEventSource("Log4j2Events");
			logEventSource.wireTo(errorCounter);
		} catch (Exception e) {
			logger.error("Could not wire log4JEvents to ErrorCounter.");
			e.printStackTrace();
		}

		exrecoLauncher.run();
		int errors = errorCounter.getLoggedEvents().size();
	
		assertTrue("" + errors +" error log messages recieved.", errors == 0 );
		//exrecoLauncher.finish();
	}
}
