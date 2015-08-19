package org.exreco;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.exreco.logging.MemoryAppender;

public class ExrecoAssert {
	
	static public void assertNoWarningOrMoreOccured() {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		Appender appender = config.getAppender("unitTestAppender");
		MemoryAppender memoryAppender = (MemoryAppender) appender;

		int errorLogSize = memoryAppender.getLoggedEvents().size();

		assertTrue("MemoryAppender collected " + errorLogSize + " warning+ log messages.", errorLogSize == 0);
	}

}
