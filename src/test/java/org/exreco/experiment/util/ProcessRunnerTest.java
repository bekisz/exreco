package org.exreco.experiment.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProcessRunnerTest {
	private static Logger logger = LogManager.getLogger(ProcessRunner.class);
	private ProcessRunner processRunner;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	protected void setUpDirListRunner() {
		logger.debug("Running {}...", this.getClass().getName());
		this.processRunner = new ProcessRunner("TestDirProcessRunner");

		if (EnvironmentInfo.isOnWindows()) {
			processRunner.setCommandAsString("CMD /C DIR");
		} else {
			processRunner.setCommandAsString("ls -la");

		}

	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
		logger.debug("{} finished running.", this.getClass().getName());
		this.processRunner = null;
	}

	@Test
	public void testDirCommandInSameThread() {
		this.setUpDirListRunner();
		processRunner.run();
		int exitedWith = processRunner.getExitedWith();
		assertTrue("Process exit with non-zero exit code", processRunner.isExited() && exitedWith == 0);

	}

	@Test
	public void testDirCommandInNewThread() {
		this.setUpDirListRunner();
		processRunner.patientStart();
		int exitedWith = processRunner.getExitedWith();
		assertTrue("Process exit with non-zero exit code", processRunner.isExited() && exitedWith == 0);

	}

	@Test
	public void testJppfDriverThread() {
		logger.debug("Running {}...", this.getClass().getName());
		this.processRunner = new ProcessRunner("JppfDriverProcessRunner");
		String jppfDriverDir = "C:\\Dev\\jppf\\jppf-5.0.1\\JPPF-5.0.1-driver";
		File jppfDriverDirectoryFile = new File(jppfDriverDir);
		this.processRunner.setDirectory(jppfDriverDirectoryFile);
		String jppfDriver = jppfDriverDir + File.separatorChar + "startDriver";
		if (EnvironmentInfo.isOnWindows()) {
			processRunner.setCommandAsString(jppfDriver + ".bat");
		} else {
			processRunner.setCommandAsString(jppfDriver + ".sh");

		}
		String waitTillLineMatches = "JPPF Driver initialization complete";
		processRunner.patientStart(waitTillLineMatches);
		int exitedWith = processRunner.getExitedWith();
		assertTrue("Process exit with non-zero exit code",  processRunner.isServiceReady() || processRunner.isExited() && exitedWith == 0);

	}

}
