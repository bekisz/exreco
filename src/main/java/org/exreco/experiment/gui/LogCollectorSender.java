package org.exreco.experiment.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogCollectorSender {
	private static Logger logger = LogManager
			.getLogger(LogCollectorSender.class.getName());

	public static void main(String[] args) {
		try {
			/*
			 * // create a runner instance. JobRunner runner = new JobRunner();
			 * 
			 * // Create a job JPPFJob job = runner.createJob();
			 * 
			 * // execute a blocking job runner.executeBlockingJob(job);
			 * 
			 * // execute a non-blocking job //
			 * runner.executeNonBlockingJob(job);
			 */

			// java.util.logging.config.file
			/*
			 * System.out.println("Prop : " +
			 * System.getProperty("java.util.logging.config.file"));
			 * System.out.println("Env : " +
			 * System.getenv("java.util.logging.config.file"));
			 */
			for (int i = 0; i < 10000; i++) {
				logger.debug("Running experiment");

				logger.trace("Expriment initialised.");

				logger.debug("Expriment run finished.");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.debug("Exception caught : ", e);

		}

	}
}
