package org.exreco.experiment.util.events;

import java.util.HashMap;
import java.util.Map;

public class LocalEventTopicHome implements EventTopicHome {

	private final Map<String, EventHub<LiffEvent>> topics = new HashMap<String, EventHub<LiffEvent>>();

	@Override
	public LiffEventListener<LiffEvent> getEventListener(String topicName)
			throws Exception {
		EventHub<LiffEvent> eventListener = this.getTopics().get(topicName);
		if (eventListener == null) {
			eventListener = new EventHub<LiffEvent>();
			this.getTopics().put(topicName, eventListener);

		}
		return eventListener;
	}

	@Override
	public EventSource<LiffEvent> getEventSource(String topicName)
			throws Exception {

		EventHub<LiffEvent> eventSource = this.getTopics().get(topicName);
		if (eventSource == null) {
			eventSource = new EventHub<LiffEvent>();
			this.getTopics().put(topicName, eventSource);

		}
		return eventSource;
	}

	/**
	 * @return the topics
	 */
	public Map<String, EventHub<LiffEvent>> getTopics() {
		return topics;
	}

}
