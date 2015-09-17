package org.exreco.experiment;

import org.exreco.experiment.util.events.EventTopicHome;

//@TODO Delete it 
public interface CaseShellIf extends CaseIf {

	
	// public ExperimentTracker getExperimentTracker();
	// public void setExperimentTracker(ExperimentTracker experimentTracker);
	public void setLog4j2ConfigLocation(String log4j2ConfigLocation);
	public EventTopicHome getEvenTopicHome();
	public void setEvenTopicHome(EventTopicHome evenTopicHome);

}
