package org.exreco;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.exreco.logging.MemoryAppender;

public class ExrecoAssert {
	static public String unitTestAppenderName ="unitTestAppender"; 
	static public MemoryAppender getUnitTestMemoryAppender() {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		Appender appender = config.getAppender(unitTestAppenderName);
		MemoryAppender memoryAppender = (MemoryAppender) appender;
		return memoryAppender;
	}
	static public int countMemoryAppenderErrors() {
		
		MemoryAppender memoryAppender = getUnitTestMemoryAppender();
		int errorLogSize = memoryAppender.getLoggedEvents().size();
		return errorLogSize;
	}
	/**
	 * 
	 * @return number of errors found before clearing errors
	 */
	static public int countAndClearMemoryAppenderErrors() {
		MemoryAppender memoryAppender = getUnitTestMemoryAppender();
		int errorLogSize = memoryAppender.getLoggedEvents().size();
		memoryAppender.getLoggedEvents().clear();
		return errorLogSize;
		
	}

	static public void assertNoWarningOrMoreOccured() {
		int errorLogSize = countAndClearMemoryAppenderErrors();
		assertTrue("MemoryAppender collected " + errorLogSize + " warning+ log messages.", errorLogSize == 0);
	}

}
