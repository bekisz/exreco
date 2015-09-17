package org.exreco.experiment.jms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.util.events.LiffEventListener;


public class LiffEvent2JmsEventAdapter implements LiffEventListener,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4236423724137419785L;
	private static Logger logger = LogManager
			.getLogger(LiffEvent2JmsEventAdapter.class.getName());
	transient private Session session;

	private final Destination destination;
	transient private MessageProducer messageProducer;

	public LiffEvent2JmsEventAdapter(String destinationName) {

		this(JmsDestination.getDestination(destinationName));

	}

	public LiffEvent2JmsEventAdapter(Destination destination) {
		super();
		this.destination = destination;


	}

	synchronized protected void init() throws JMSException {
		this.initSession();
		this.initMessageProducer();
	}

	synchronized protected void initSession() throws JMSException {

		this.session = JmsConnection.getConnection().createSession(false,
				Session.CLIENT_ACKNOWLEDGE);
	}

	synchronized protected void initMessageProducer() throws JMSException {
		this.messageProducer = this.getSession().createProducer(
				this.getDestination());
		// this.messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}

	synchronized protected Session getSession() throws JMSException {
		if (this.session == null) {
			this.initSession();
		}
		return this.session;
	}

	synchronized protected MessageProducer getMessageProducer()
			throws JMSException {
		if (this.messageProducer == null) {
			this.initMessageProducer();
		}
		return this.messageProducer;
	}

	protected Destination getDestination() throws JMSException {

		return destination;
	}

	protected Message convert(Serializable object) throws Exception {
		return this.getSession().createObjectMessage(object);
	}

	@Override
	synchronized public void eventOccurred(Serializable event) throws Exception {

		try {

			Message message = this.convert(event);
			this.getMessageProducer().send(message);

		} catch (Exception e) {
			
			System.err.println("Exception occurred at sending JMS message  ");

			throw e;
		}

	}

	protected void close() throws JMSException {
		this.messageProducer.close();
		this.session.close();
	}

	synchronized private void writeObject(ObjectOutputStream out)
			throws IOException {
		out.defaultWriteObject();
	}

	synchronized private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();

		try {
			this.init();
		} catch (JMSException e) {
			logger.error("Could not initialize {} instance at deserialisation",
					this.getClass().getName());
			// e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@Override
	synchronized protected void finalize() throws Throwable {
		try {
			this.close();
		} finally {
			super.finalize();
		}
	}
}
