package org.exreco.experiment.gui;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LifeCycle;
import org.exreco.experiment.Exreco;

import org.exreco.experiment.util.LiffUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Liff {

	private static Logger logger = LogManager.getLogger(Liff.class.getName());
	private static ApplicationContext context;

	public static void main(String[] args) {
		try {

			ThreadContext.put("pid", LiffUtils.getProcessId());
			logger.debug("Running experiment");
			
	        Map<String, String> env = System.getenv();
	        if (env.containsKey("ACTIVEMQ_HOME")) {
	        	String activeMqHome = env.get("ACTIVEMQ_HOME");
	        	System.setProperty("activemq.home", activeMqHome);
	        	String activeMqConfig  =activeMqHome + File.separatorChar + "conf";
	        	System.setProperty("activemq.conf", activeMqConfig);
	        	logger.debug("ActiveMQ Config dir is " + activeMqConfig);
	        }
	        for (String envName : env.keySet()) {
	            System.out.format("%s=%s%n",
	                              envName,
	                              env.get(envName));
	        }
			context = new ClassPathXmlApplicationContext("exreco-beans.xml");
			Exreco experiment= (Exreco) context.getBean("exreco");
			

			logger.debug("Experiment initialised.");

			LiffFrame frame = new LiffFrame();
			frame.registerEventTopicHome(experiment.getEventTopicHome());
		

			experiment.run();
		
			logger.debug("Expriment run finished.");
		} catch (Exception e) {
			// e.printStackTrace();
			logger.debug("Exception caught : " + e.getMessage() , e);

		} finally {
			((LifeCycle) LogManager.getContext()).stop();
		}

	}
}