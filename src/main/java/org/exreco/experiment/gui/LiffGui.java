package org.exreco.experiment.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LifeCycle;
import org.exreco.experiment.jms.JmsEventTopicHome;
import org.exreco.experiment.util.LiffUtils;
import org.exreco.experiment.util.events.EventTopicHome;


public class LiffGui {
	private static Logger logger = LogManager
			.getLogger(LiffGui.class.getName());
	//private static JmsMessageReceiver experimentStatusReceiver;

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		try {

			ThreadContext.put("pid", LiffUtils.getProcessId());
			logger.debug("Running LiffGui");

			LiffFrame frame = new LiffFrame();
			EventTopicHome eventTopicHome = new JmsEventTopicHome();
			frame.registerEventTopicHome(eventTopicHome);
			logger.debug("Remote Liff event listener initialised.");

			logger.debug("LiffGui run finished.");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.debug("Exception caught : ", e);

		} finally {
			((LifeCycle) LogManager.getContext()).stop();
		}

	}
}
