package org.exreco.experiment.jms;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActiveMqProcessRunner extends Thread {
	private static Logger logger = LogManager.getLogger(ActiveMqProcessRunner.class.getName());

	private boolean serviceReady = false;
	private String amqReadyPattern = "Apache ActiveMQ.*started";
	final Lock lock = new ReentrantLock();
	private final Condition ready = lock.newCondition();
	private final Thread closeChildThread = new Thread() {
		
	    public void run() {
	        ActiveMqProcessRunner.this.shutDown();
	    }
	};

	public ActiveMqProcessRunner() {
		super("ProcessRunner");
	}

	public static boolean isActiveMqHomeDirectorySet() {
		return (getActiveMqHomeDirectory() != null);

	}

	public static String getActiveMqHomeDirectory() {
		return System.getenv("ACTIVEMQ_HOME");

	}

	public static String getActiveMqBinDirectory() {
		return getActiveMqHomeDirectory() + File.separatorChar + "bin";

	}

	public boolean isServiceReady() {
		return true;
	}

	public void patientStart() {
		lock.lock();
		try {
			this.start();
			ready.await();
		} catch (InterruptedException e) {
			logger.error("InterruptedException on Active Message Queue thread.", e);

		} finally {
			lock.unlock();
		}
	}

	public void shutDown() {

		try {
			logger.debug("Stopping Active MQ");
			List<String> list = new ArrayList<String>(4);
			// TODO : Make it work on Unix
			list.add("activemq.bat");
			list.add("stop");
			final ProcessBuilder processBuilder = new ProcessBuilder(list);
			Process process;
			if (!isActiveMqHomeDirectorySet()) {
				logger.error("ACTIVE_MQ environment variable is not set. ActiveMQ can not be stopped.");
				return;
			}
			String activeMqBinDir = getActiveMqBinDirectory();
			processBuilder.directory(new File(activeMqBinDir));

			process = processBuilder.start();

			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			// logger.debug("Output of running %s is:\n {}",
			// Arrays.deepToString(list.toArray()));
			//Pattern pattern = Pattern.compile(this.getAmqReadyPattern());
			while ((line = br.readLine()) != null) {
				logger.debug(line);

			}
			final int exitStatus = process.waitFor();
			logger.debug("ActiveMQ stop finished with status: {}", exitStatus);

		} catch (IOException |

		InterruptedException e)

		{
			logger.error("Exception on Active Message Queue thread.", e);

		}
	}

	public void run() {

		try {
			logger.debug("Running Active MQ process runner");
			List<String> command = new ArrayList<String>(4);
			// TODO : Make it work on Unix
			command.add("activemq.bat");
			command.add("start");
			final ProcessBuilder processBuilder = new ProcessBuilder(command);
			Process process;
			if (!isActiveMqHomeDirectorySet()) {
				logger.error("ACTIVE_MQ environment variable is not set. ActiveMQ can not be started.");
				return;
			}
			logger.debug("Using MQ in directory {}", getActiveMqHomeDirectory());
			String activeMqBinDir = getActiveMqBinDirectory();
			processBuilder.directory(new File(activeMqBinDir));

			process = processBuilder.start();
			// Let the process be shut down cleanly on exit of parent (like after SIGINT)
			Runtime.getRuntime().addShutdownHook(this.closeChildThread); 
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			// logger.debug("Output of running %s is:\n {}",
			// Arrays.deepToString(list.toArray()));
			Pattern pattern = Pattern.compile(this.getAmqReadyPattern());
			while ((line = br.readLine()) != null) {
				logger.debug(line);

				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					// TODO : make it thread-safe
					this.serviceReady = true;
					logger.debug("ActiveMQ is ready to accept connections");

					lock.lock();
					try {
						this.ready.signalAll();
					} finally {
						lock.unlock();
					}
				}
			}
			final int exitStatus = process.waitFor();
			logger.debug("ActiveMQ finished with status: {}", exitStatus);

		} catch (IOException |

		InterruptedException e)

		{
			logger.error("Exception on Active Message Queue thread.", e);

		}

	}

	public String getAmqReadyPattern() {
		return amqReadyPattern;
	}

	public void setAmqStartedPattern(String amqStartedPattern) {
		this.amqReadyPattern = amqStartedPattern;
	}
}
