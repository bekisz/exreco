package org.exreco.experiment.jms;

import java.util.HashMap;
import java.util.Map;

import org.exreco.experiment.util.events.EventHub;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;


public class JmsEventTopicHome implements EventTopicHome {

	private final Map<String, LiffEvent2JmsEventAdapter> topicInputMap = new HashMap<String, LiffEvent2JmsEventAdapter>();
	private final Map<String, JmsMessageReceiver> jmsReceiverMap = new HashMap<String, JmsMessageReceiver>();
	private final Map<String, EventHub<LiffEvent>> topicOutputMap = new HashMap<String, EventHub<LiffEvent>>();

	@Override
	synchronized public LiffEventListener<LiffEvent> getEventListener(
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
	synchronized public EventSource<LiffEvent> getEventSource(String topicName)
			throws Exception {

		EventHub<LiffEvent> eventSource;

		eventSource = this.getTopicOutputMap().get(topicName);

		if (eventSource == null) {
			eventSource = new EventHub<LiffEvent>();

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
	public Map<String, EventHub<LiffEvent>> getTopicOutputMap() {
		return topicOutputMap;
	}

}
