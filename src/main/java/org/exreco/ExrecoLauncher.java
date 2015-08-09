package org.exreco;

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LoggerContext;
import org.exreco.experiment.Exreco;

import org.exreco.experiment.util.LiffUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ExrecoLauncher {

	private static Logger logger = LogManager.getLogger(ExrecoLauncher.class.getName());
	private String exrecoBeansXmlFile = "exreco-beans.xml";
	
	public static void main(String[] args) {
		ExrecoLauncher launcher = new ExrecoLauncher();
		launcher.run();
		launcher.finish();

	}
	public void run() {
		try {

			ThreadContext.put("pid", LiffUtils.getProcessId());
			logger.debug("Running experiment");
			/*
			logger.debug("main-Deployment init entered.");
			URI log4j2ConfigLocationURI = new URI("log4j2-multi-node.xml");
			LoggerContext loggerContext = (LoggerContext) LogManager.getContext(true);
			loggerContext.setConfigLocation(log4j2ConfigLocationURI);
			//logger = loggerContext.getLogger(Deployment.class.getName());
			logger.debug("main- //////////////Deployment init finished.");
			
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
	        } */
			@SuppressWarnings("resource")
			ApplicationContext context = new ClassPathXmlApplicationContext(this.getExrecoBeansXmlFile());
			Exreco replicatorCollider = (Exreco) context.getBean("exreco");
			

			logger.debug("Exreco initialised.");


			replicatorCollider.run();
		
			logger.debug("Expriment run finished.");
		
		} catch (Exception e) {
			logger.debug("Exception caught : " + e.getMessage() , e);

		} 
		
	}
	public void finish() {
		((LifeCycle) LogManager.getContext()).stop();
	}

	public String getExrecoBeansXmlFile() {
		return exrecoBeansXmlFile;
	}
	public void setExrecoBeansXmlFile(String exrecoBeansXmlFile) {
		this.exrecoBeansXmlFile = exrecoBeansXmlFile;
	}
}