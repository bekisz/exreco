package org.exreco.experiment.util.events;



public interface EventTopicHome {
	public LiffEventListener getEventListener(String topicName)
			throws Exception;

	public EventSource getEventSource(String topicName)
			throws Exception;

}
