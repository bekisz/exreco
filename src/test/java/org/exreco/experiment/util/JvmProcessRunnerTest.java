package org.exreco.experiment.util;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jppf.server.DriverLauncher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JvmProcessRunnerTest {
	private static JvmProcessRunner jvmProcessRunner;
	private static Logger logger = LogManager.getLogger(JvmProcessRunnerTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		jvmProcessRunner = new JvmProcessRunner();
		logger.debug("Running {}...", this.getClass().getName());
		jvmProcessRunner = new JvmProcessRunner("JvmProcessRunnerTest");

	}

	@After
	public void tearDown() throws Exception {
		jvmProcessRunner = null;
	}

	@Test
	public void testProcessInfoProcess() {
		jvmProcessRunner.setMainClass(EnvironmentInfo.class.getCanonicalName());
		jvmProcessRunner.run();
		int exitedWith = jvmProcessRunner.getExitedWith();
		assertTrue("Process exit with non-zero exit code", jvmProcessRunner.isExited() && exitedWith == 0);
	}

	@Test
	public void testStartJppfDriver() {
		jvmProcessRunner.setMainClass(DriverLauncher.class.getCanonicalName());
		jvmProcessRunner.setJvmOptions(
				"-Xmx16m -Dlog4j.configuration=log4j-driver.properties -Djppf.config=jppf-driver.properties -Djava.util.logging.config.file=config/logging-driver.properties");

		jvmProcessRunner.run();
		int exitedWith = jvmProcessRunner.getExitedWith();
		assertTrue("Process exit with non-zero exit code", jvmProcessRunner.isExited() && exitedWith == 0);
	}

}
