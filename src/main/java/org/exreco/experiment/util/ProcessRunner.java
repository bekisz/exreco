package org.exreco.experiment.util;

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
import org.springframework.core.env.SystemEnvironmentPropertySource;

public class ProcessRunner extends Thread {
	private static Logger logger = LogManager.getLogger(ProcessRunner.class.getName());

	private List<String> command = new ArrayList<String>(4);
	private Process process;
	private File directory;
	private String homeDirectory;
	final private Lock lock = new ReentrantLock();
	private final Condition ready = lock.newCondition();
	private boolean exited;
	private boolean started;
	private boolean isServiceReady;
	private int exitedWith = 0;
	private String waitTillLineMatches = null;
	private Pattern pattern;

	public boolean isExited() {
		return exited;
	}

	public void setExited(boolean exited) {
		this.exited = exited;
	}

	public int getExitedWith() {
		return exitedWith;
	}

	public void setExitedWith(int exitedWith) {
		this.exitedWith = exitedWith;
	}

	public ProcessRunner() {
		this("ProcessRunner");
	}

	public ProcessRunner(String name) {
		super(name);
		this.createShutdownHook();
	}

	public void patientStart(String waitTillLineMatches) {

		this.setWaitTillLineMatches(waitTillLineMatches);

		this.patientStart();
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

	/**
	 * Get the output of the driver process.
	 * 
	 * @param process
	 *            the process to get the standard or error output from.
	 * @param streamType
	 *            determines whether to obtain the standard or error output.
	 *            "std" or "sterr"
	 * @return the output as a string.
	 */
	public String getOutput(final String streamType) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = "std".equals(streamType) ? process.getInputStream() : process.getErrorStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			try {
				String s = "";
				while (s != null) {
					s = reader.readLine();
					if (s != null)
						sb.append(s).append('\n');
				}
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return sb.toString();
	}

	@Override
	public void run() {

		this.setStarted(true);
		this.setExited(false);
		this.setExitedWith(0);
		try {
			this.startProcess();
			this.processStandardOutput();
			try {
				this.lock.lock();
				final int exitStatus = process.waitFor();
				this.setExited(true);
				this.setExitedWith(exitStatus);
				logger.debug("Process exited with status: {}", exitStatus);
				this.exitedWith(exitStatus);
				if (process != null) {
					process.destroy();
				}
				this.ready.signalAll();
			} finally {
				lock.unlock();
			}

		} catch (Exception e) {
			logger.error("Process runner error. ", e);
		}

	}

	/**
	 * Start the sub-process.
	 * 
	 * @throws Exception
	 *             if any error occurs.
	 */
	public void startProcess() throws Exception {
		// stoppedOnBusyState.set(false);
		this.setProcess(this.buildProcess());

	}

	protected void exitedWith(int exitStatus) {
		if (exitStatus != 0) {
			String output = this.getOutput("err");
			System.err.println(output);
			output = this.getOutput("std");
			System.out.println(output);
		}
	}

	public Process buildProcess() throws Exception {
		logger.debug("Process command:\n" + command);
		ProcessBuilder builder = new ProcessBuilder(command);
		if (this.getDirectory() != null) {
			builder.directory(this.getDirectory());
		}
		Process process = builder.start();
		return process;
	}

	public void setCommandAsString(final String commandString) {
		String[] commandArray = commandString.split("\\s");
		List<String> commandList = Arrays.asList(commandArray);
		this.setCommand(commandList);
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	/**
	 * Create a shutdown hook that is run when this JVM terminates.<br>
	 * This is normally used to ensure the subprocess is terminated as well.
	 */
	protected void createShutdownHook() {
		Runnable hook = new Runnable() {
			@Override
			public void run() {
				if (process != null)
					process.destroy();
			}
		};
		Runtime.getRuntime().addShutdownHook(new Thread(hook));
	}

	/**
	 * Processes one line received from stdout.
	 */
	protected void stdout(String line) {

		// logger.debug(line);
		System.out.println(line);

	}

	protected void processStandardOutput() throws IOException {

		InputStream is = this.getProcess().getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			this.stdout(line);
			if (this.pattern != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					logger.debug("Matching line found to wake up parent thread.");
					lock.lock();
					try {
						this.setServiceReady(true);
						this.ready.signalAll();

					} finally {
						lock.unlock();
					}
				}
			}
		}
	}

	public List<String> getCommand() {
		return command;
	}

	public void setCommand(List<String> command) {
		this.command = command;
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isServiceReady() {
		return isServiceReady;
	}

	public void setServiceReady(boolean isServiceReady) {
		this.isServiceReady = isServiceReady;
	}

	public String getWaitTillLineMatches() {
		return waitTillLineMatches;
	}

	public void setWaitTillLineMatches(String waitTillLineMatches) {
		this.waitTillLineMatches = waitTillLineMatches;
		this.pattern = Pattern.compile(this.waitTillLineMatches);
	}

	public String getHomeDirectory() {
		return homeDirectory;
	}

	public void setHomeDirectory(String homeDirectory) {
		this.homeDirectory = homeDirectory;
		this.setDirectory(new File(homeDirectory));
	}

}
