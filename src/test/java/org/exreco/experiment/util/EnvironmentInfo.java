package org.exreco.experiment.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvironmentInfo {
	private static Logger logger = LogManager.getLogger(EnvironmentInfo.class);

	public static void logSystemProperties() {

		logger.info(" - java.class.path = {}", System.getProperty("java.class.path"));
		logger.info(" - java.home = {}", System.getProperty("java.home"));
		logger.info(" - java.vendor = {}", System.getProperty("java.vendor"));

		logger.info(" - java.version = {}", System.getProperty("java.version"));

		logger.info(" - os.name = {}", System.getProperty("os.name"));
		logger.info(" - os.arch = {}", System.getProperty("os.arch"));
		logger.info(" - os.version = {}", System.getProperty("os.version"));

	}

	public static void logEnvironmentVariables() {

		Map<String, String> env = System.getenv();
		logger.info("Environment Variables : ");
		for (String envName : env.keySet()) {
			String value = System.getenv(envName);
			if (value != null) {
				logger.info("{} = {}", envName, value);
			} else {
				logger.info("{} is" + " not assigned.", envName);
			}
		}

	}
	public static boolean isOnWindows() {
		String osName = System.getProperty("os.name");
		if ( osName != null && osName.startsWith("Windows")) {
			return true;
		}
		return false;
		
	}
	public static void main(String[] args) {
		logger.info("started");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			sb.append(' ');
		}
		logger.info("{} " + " runs with {} ", EnvironmentInfo.class.getCanonicalName(), sb.toString());

		EnvironmentInfo.logSystemProperties();
		EnvironmentInfo.logEnvironmentVariables();
	}

}
