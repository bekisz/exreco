package org.exreco.experiment.jms;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.exreco.experiment.util.events.EventHub;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.LiffEventListener;


public class JmsEventTopicHome  implements EventTopicHome, Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 2699924834297943454L;
	transient private final Map<String, LiffEvent2JmsEventAdapter> topicInputMap = new HashMap<String, LiffEvent2JmsEventAdapter>();
	transient private final Map<String, JmsMessageReceiver> jmsReceiverMap = new HashMap<String, JmsMessageReceiver>();
	transient private final Map<String, EventHub> topicOutputMap = new HashMap<String, EventHub>();
	public JmsEventTopicHome() {
		super();
		// Avoid lazy init so that Exreco appender could run
		JmsConnection.getConnection();
	}
	@Override
	synchronized public LiffEventListener getEventListener(
			String topicName) throws Exception {
		LiffEvent2JmsEventAdapter eventListener = this.getTopicInputMap().get(
				topicName);
		if (eventListener == null) {
			eventListener = new LiffEvent2JmsEventAdapter(topicName);
			this.getTopicInputMap().put(topicName, eventListener);

		}
		return eventListener;
	}

	@Override
	synchronized public EventSource getEventSource(String topicName)
			throws Exception {

		EventHub eventSource;

		eventSource = this.getTopicOutputMap().get(topicName);

		if (eventSource == null) {
			eventSource = new EventHub();

			JmsEvent2LiffEventAdapter jms2liffAdapter = new JmsEvent2LiffEventAdapter(
					eventSource);
			JmsMessageReceiver receiver = new JmsMessageReceiver(
					jms2liffAdapter, topicName);
			this.getJmsReceiverMap().put(topicName, receiver);

		}
		return eventSource;
	}

	/**
	 * @return the topicInputMap
	 */
	public Map<String, LiffEvent2JmsEventAdapter> getTopicInputMap() {
		return topicInputMap;
	}

	/**
	 * @return the jmsReceiverMap
	 */
	public Map<String, JmsMessageReceiver> getJmsReceiverMap() {
		return jmsReceiverMap;
	}

	/**
	 * @return the topicOutputMap
	 */
	public Map<String, EventHub> getTopicOutputMap() {
		return topicOutputMap;
	}
	synchronized private void writeObject(ObjectOutputStream out)
			throws IOException {
		out.defaultWriteObject();
	}

	synchronized private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();
		JmsConnection.getConnection();
	

	}

}
