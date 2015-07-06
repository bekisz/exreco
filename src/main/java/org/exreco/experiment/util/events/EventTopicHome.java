package org.exreco.experiment.util.events;

public interface EventTopicHome {
	public LiffEventListener<LiffEvent> getEventListener(String topicName)
			throws Exception;

	public EventSource<LiffEvent> getEventSource(String topicName)
			throws Exception;

}
