package org.exreco.log.server;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.exreco.experiment.util.LiffUtils;

public class JmsLoggingService {
	private final Logger logger = LogManager.getLogger(JmsLoggingService.class
			.getName());
	private final LogEventSink logEventSink = new LogEventSink();

	private static final String DEFAULT_CONFIG_FILE_NAME = "config/log4j2-jmsLoggingService.xml";
	private static final String DEFAULT_DESTINATION_NAME = "Log4j2Events";

	public JmsLoggingService() {

	}

	protected void accept() {
		String destinationName = null;
		Context jndiContext = null;
		ConnectionFactory connectionFactory = null;
		Connection connection = null;
		Session session = null;
		Destination destination = null;
		MessageConsumer messageConsumer = null;

		/*
		 * Read queue name from command line and display it.
		 */

		destinationName = DEFAULT_DESTINATION_NAME;
		// System.out.println("Queue name is " + destinationName);

		/*
		 * Create a JNDI API InitialContext object if none exists yet.
		 */
		ThreadContext.put("pid", LiffUtils.getProcessId());
		logger.debug("Starting {}...", JmsLoggingService.class.getName());
		try {
			jndiContext = new InitialContext();
		} catch (NamingException e) {
			logger.error("Could not create JNDI API context: " + e.toString());
			System.exit(1);
		}

		/*
		 * Look up connection factory and queue. If either does not exist, exit.
		 */
		try {
			connectionFactory = (QueueConnectionFactory) jndiContext
					.lookup("QueueConnectionFactory");
			destination = (Destination) jndiContext.lookup(destinationName);
		} catch (NamingException e) {
			logger.error("JNDI API lookup failed: " + e.toString());
			System.exit(1);
		}

		/*
		 * Create connection. Create session from connection; false means
		 * session is not transacted. Create receiver, then start message
		 * delivery. Receive all text messages from queue until a non-text
		 * message is received indicating end of message stream. Close
		 * connection.
		 */
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			messageConsumer = session.createConsumer(destination);
			connection.start();
			while (true) {
				Message m = messageConsumer.receive();
				if (m != null) {

					if (m instanceof TextMessage) {
						TextMessage textMessage = (TextMessage) m;
						logger.debug("Reading message: "
								+ textMessage.getText());
					} else if (m instanceof ObjectMessage) {
						ObjectMessage objectMessage = (ObjectMessage) m;
						LogEvent logEvent = (LogEvent) objectMessage
								.getObject();
						logEventSink.log(logEvent);
					} else {
						break;
					}
				}
			}
		} catch (JMSException e) {
			logger.error("Exception occurred: " + e.toString());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
				}
			}
		}
		logger.debug("Exiting {}", JmsLoggingService.class.getName());
	}

	public static void main(String[] args) throws Exception {
		String configFileName = DEFAULT_CONFIG_FILE_NAME;
		if (args.length > 0) {
			configFileName = args[0];
		}
		ConfigurationFactory
				.setConfigurationFactory(new ServerConfigurationFactory(
						configFileName));

		JmsLoggingService service = new JmsLoggingService();
		service.accept();

		// ((LoggerContext) LogManager.getContext()).reconfigure();

	}

}