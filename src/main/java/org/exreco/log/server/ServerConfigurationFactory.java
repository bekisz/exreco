package org.exreco.log.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.xml.sax.InputSource;

public class ServerConfigurationFactory extends XmlConfigurationFactory {

	private final String path;

	public ServerConfigurationFactory(String path) {
		this.path = path;
	}

	@Override
	public Configuration getConfiguration(String name, URI configLocation) {
		if (path != null && path.length() > 0) {
			File file = null;
			InputSource source = null;
			FileInputStream is = null;
			try {
				file = new File(path);
				is = new FileInputStream(file);
				source = new InputSource(is);
				source.setSystemId(path);
			} catch (FileNotFoundException ex) {
				// Ignore this error
			}
			if (source == null) {
				try {
					URL url = new URL(path);
					source = new InputSource(url.openStream());
					source.setSystemId(path);
				} catch (MalformedURLException mue) {
					// Ignore this error
				} catch (IOException ioe) {
					// Ignore this error
				}
			}

			try {
				if (source != null) {
					ConfigurationSource configurationSource = new ConfigurationSource(
							is, file);

					return new XmlConfiguration(configurationSource);
				}
			} catch (Exception ex) {
				// Ignore this error.
			}
			System.err.println("Unable to process configuration at " + path
					+ ", using default.");
		}
		return super.getConfiguration(name, configLocation);
	}

}
