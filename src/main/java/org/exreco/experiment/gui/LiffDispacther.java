package org.exreco.experiment.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.core.LifeCycle;
import org.exreco.experiment.Experiment;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class LiffDispacther {

	private static Logger logger = LogManager.getLogger(LiffDispacther.class
			.getName());

	private static ApplicationContext context;
	public static void main(String[] args) {
		try {
		
			logger.debug("Running experiment");

			context = new ClassPathXmlApplicationContext("experiment-beans.xml");
			Experiment experiment= (Experiment) context.getBean("experiment");

			logger.debug("Experiment initialised.");
			experiment.run();
			logger.debug("Expriment run finished.");
		} catch (Exception e) {
		
			logger.debug("Exception caught : ", e);

		} finally {
			((LifeCycle) LogManager.getContext()).stop();
		}

	}
}