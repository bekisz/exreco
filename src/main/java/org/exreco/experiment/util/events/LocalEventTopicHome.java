package org.exreco.experiment.util.events;

import java.util.HashMap;
import java.util.Map;

public class LocalEventTopicHome implements EventTopicHome {

	private final Map<String, EventHub> topics = new HashMap<String, EventHub>();

	@Override
	public LiffEventListener getEventListener(String topicName)
			throws Exception {
		EventHub eventListener = this.getTopics().get(topicName);
		if (eventListener == null) {
			eventListener = new EventHub();
			this.getTopics().put(topicName, eventListener);

		}
		return eventListener;
	}

	@Override
	public EventSource getEventSource(String topicName)
			throws Exception {

		EventHub eventSource = this.getTopics().get(topicName);
		if (eventSource == null) {
			eventSource = new EventHub();
			this.getTopics().put(topicName, eventSource);

		}
		return eventSource;
	}

	/**
	 * @return the topics
	 */
	public Map<String, EventHub> getTopics() {
		return topics;
	}

}
