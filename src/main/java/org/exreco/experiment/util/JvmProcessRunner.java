package org.exreco.experiment.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JvmProcessRunner extends ProcessRunner {

	private static Logger logger = LogManager.getLogger(JvmProcessRunner.class.getName());
	private String jvmOptions;
	private String mainClass;
	public JvmProcessRunner() {
		super();
		
	}

	public JvmProcessRunner(String name) {
		super(name);
	}
	
	public Process buildProcess() throws Exception {

		List<String> jvmOptions = new ArrayList<>();
		List<String> cpElements = new ArrayList<>();

		String s = this.getJvmOptions();
		if (s != null) {
			String[] options = s.split("\\s");
			int count = 0;
			while (count < options.length) {
				String option = options[count++];
				if ("-cp".equalsIgnoreCase(option) || "-classpath".equalsIgnoreCase(option))
					cpElements.add(options[count++]);
				else
					jvmOptions.add(option);
			}
		}
		String classPath = System.getProperty("java.class.path");
		cpElements.add(classPath);
		
		String javaHomeDir = System.getProperty("java.home");

		if (javaHomeDir == null) {
			logger.error("The environment variable JAVA_HOME is not set.");
			throw new Exception("The environment variable JAVA_HOME is not set.");
		}

		this.getCommand().add(javaHomeDir + File.separatorChar + "bin" + File.separatorChar + "java");
		this.getCommand().add("-cp");
		StringBuilder sb = new StringBuilder();
		String sep = File.pathSeparator;

		for (int i = 0; i < cpElements.size(); i++) {
			sb.append(cpElements.get(i));
			if (i > 0) {
				sb.append(sep);
			}
		}
		this.getCommand().add(sb.toString());

		for (String opt : jvmOptions) {
			this.getCommand().add(opt);
		}
		this.getCommand().add(mainClass.trim());

		return super.buildProcess();

	}

	public String getJvmOptions() {
		return jvmOptions;
	}

	public void setJvmOptions(String jvmOptions) {
		this.jvmOptions = jvmOptions;
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
}
