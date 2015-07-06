package org.exreco.experiment.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JmsConnection {

	private static Connection connection = null;
	private static Logger logger = LogManager.getLogger(JmsConnection.class
			.getName());

	public static synchronized Connection getConnection() {
		if (connection == null) {

			try {
				Context jndiContext = new InitialContext();

				ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
						.lookup("ConnectionFactory");
				connection = connectionFactory.createConnection();
			} catch (NamingException e) {
				logger.error("JNDI API lookup failed: " + e.toString(), e);

			} catch (JMSException e) {
				logger.error(
						"Failed to create JMS connection: " + e.toString(), e);
			}
			logger.debug("JMS connection initialised");
		}
		return connection;

	}

	public synchronized void close() {
		if (connection != null) {
			try {
				connection.close();
				logger.debug("JMS Connection closed");
			} catch (JMSException e) {
				logger.error("Could not close JMS connection  ", e);
			}
		}
	}
}
