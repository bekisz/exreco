package org.exreco.experiment.jms;

import java.io.Serializable;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;


public class JmsEvent2LiffEventAdapter implements MessageListener, Serializable {

	private static final long serialVersionUID = -7970940195392959199L;

	private static Logger logger = LogManager
			.getLogger(JmsEvent2LiffEventAdapter.class.getName());
	private final LiffEventListener liffEventListener;

	public JmsEvent2LiffEventAdapter(
			LiffEventListener liffEventListener) {
		super();
		this.liffEventListener = liffEventListener;
	}
  
	protected Serializable convert(Message message) throws Exception {
		Serializable liffEvent = null;
		if (message instanceof ObjectMessage) {
			
			ObjectMessage objectMessage = (ObjectMessage) message;

			liffEvent = objectMessage.getObject();
		} else {
			logger.error("Unexpected JMS message type: {}", message.getClass()
					.getName());
		}
		return liffEvent;
	}

	@Override
	public void onMessage(Message message) {

		try {
			Serializable event = this.convert(message);
			this.getLiffEventListener().eventOccurred(event);
			message.acknowledge();
		} catch (Exception e) {
			logger.error("Exception occurred at dispatching liff event", e);
		}

	}

	/**
	 * @return the liffEventListener
	 */
	public LiffEventListener getLiffEventListener() {
		return liffEventListener;
	}
}
