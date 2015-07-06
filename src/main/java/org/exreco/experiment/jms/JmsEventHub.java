package org.exreco.experiment.jms;

import java.io.Serializable;

import javax.jms.MessageListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.util.events.EventHub;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;


public class JmsEventHub extends EventHub<LiffEvent> implements Serializable {
	final private String destinationName;
	final private JmsMessageReceiver jmsMessageReceiver;
	final private LiffEventListener<LiffEvent> jmsAdapter;
	private static Logger logger = LogManager.getLogger(JmsEventHub.class
			.getName());

	protected class PingPongFilter implements LiffEventListener<LiffEvent> {

		@Override
		public void eventOccurred(LiffEvent event) throws Exception {
			JmsEventHub.this.fireEvent(event);

		}

	}

	public JmsEventHub(String destinationName) {
		super();
		this.destinationName = destinationName;
		MessageListener messageListener = new JmsEvent2LiffEventAdapter(
				new PingPongFilter());
		this.jmsMessageReceiver = new JmsMessageReceiver(messageListener,
				destinationName);
		this.jmsAdapter = new LiffEvent2JmsEventAdapter(destinationName);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5188941654679006825L;

	@Override
	public void eventOccurred(LiffEvent event) {
		try {
			this.getJmsAdapter().eventOccurred(event);
		} catch (Exception e) {
			logger.warn("Exception occured on event propagation. ", e);
		}
		super.fireEvent(event);
	}

	/**
	 * @return the destinationName
	 */
	public String getDestinationName() {
		return destinationName;
	}

	/**
	 * @return the jmsMessageReceiver
	 */
	public JmsMessageReceiver getJmsMessageReceiver() {
		return jmsMessageReceiver;
	}

	/**
	 * @return the jmsAdapter
	 */
	public LiffEventListener<LiffEvent> getJmsAdapter() {
		return jmsAdapter;
	}
}
