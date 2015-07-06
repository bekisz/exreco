package org.exreco.experiment.jms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JmsMessageReceiver implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8743439170479242824L;
	private static Logger logger = LogManager
			.getLogger(JmsMessageReceiver.class.getName());
	private final String destinationName;
	transient private Destination destination;

	transient private Session session = null;
	private final MessageListener messageListener;
	private int acknowledgeMode = Session.CLIENT_ACKNOWLEDGE;

	public JmsMessageReceiver(MessageListener listener, String destinationName) {
		this(listener, destinationName, Session.CLIENT_ACKNOWLEDGE);
	}

	public JmsMessageReceiver(MessageListener listener, String destinationName,
			int acknowledgeMode) {

		this.messageListener = listener;
		this.destinationName = destinationName;
		this.acknowledgeMode = acknowledgeMode;

		this.init();
	}

	synchronized public void init() {
		logger.debug("Initialising JMS Case tracker");

		try {
			this.destination = JmsDestination.getDestination(destinationName);
			this.session = JmsConnection.getConnection().createSession(false,
					this.getAcknowledgeMode());

			MessageConsumer messageConsumer = session
					.createConsumer(destination);

			messageConsumer.setMessageListener(this.getMessageListener());

			JmsConnection.getConnection().start();
			logger.debug("JMS message listener set for destination {}",
					destinationName);

		} catch (JMSException e) {
			logger.error("Exception occurred at receiveing JMS message  ", e);
		}
	}

	/**
	 * @return the messageListener
	 */
	public MessageListener getMessageListener() {
		return messageListener;
	}

	/*
	 * protected Session acquireSession() throws JMSException { Session session
	 * = JmsConnection.getConnection().createSession(false,
	 * Session.AUTO_ACKNOWLEDGE); return session; }
	 * 
	 * protected void releaseSession(Session session) throws JMSException {
	 * session.close(); }
	 */
	protected Destination getDestination() throws JMSException {

		return destination;
	}

	synchronized private void writeObject(ObjectOutputStream out)
			throws IOException {
		out.defaultWriteObject();
	}

	synchronized private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();

		this.init();

	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @return the acknowledgeMode
	 */
	public int getAcknowledgeMode() {
		return acknowledgeMode;
	}

}
