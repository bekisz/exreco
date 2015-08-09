package org.exreco;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.exreco.logging.MemoryAppender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

	@Test
	public void singleNodeSmokeTest() {

		ExrecoLauncher exrecoLauncher = new ExrecoLauncher();
		exrecoLauncher.setExrecoBeansXmlFile("exreco-beans-single-node-smoke-test.xml");
		exrecoLauncher.run();
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Appender appender = config.getAppender("unitTestAppender");
        MemoryAppender memoryAppender = (MemoryAppender) appender;
		logger.error("Blala");
		int  errorLogSize = memoryAppender.getLoggedEvents().size();
		System.out.println("Assertion test");
		assertTrue("No errors were logged", errorLogSize == 0 );
		//fail("Not yet implemented");
	}

}
