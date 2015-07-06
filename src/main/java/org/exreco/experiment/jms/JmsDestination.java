package org.exreco.experiment.jms;

import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JmsDestination {
	private static Logger logger = LogManager.getLogger(JmsConnection.class
			.getName());

	public static Destination getDestination(String name) {
		Destination result = null;
		try {

			InitialContext jndiContext = new InitialContext();
			result = (Destination) jndiContext.lookup(name);
		} catch (NamingException e) {
			logger.error("JNDI API lookup failed for JMS destination '" + name
					+ "': " + e.toString(), e);

		}
		return result;
	}
}
