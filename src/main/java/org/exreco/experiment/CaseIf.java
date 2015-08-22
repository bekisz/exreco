package org.exreco.experiment;

import org.exreco.experiment.Case.LifeCycleState;
import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.LiffEvent;

public interface CaseIf extends Runnable {
	public void init();
	public DimensionSetPoint getDimensionSetPoint();
	public void setDimensionSetPoint(DimensionSetPoint dimensionSetpoint);
	
	public int getCaseId();
	public void setCaseId(int caseId);
	
	
	public LifeCycleState getLifeCycleState();
	public void setLifeCycleState(LifeCycleState lifeCycleState);
	
	public long getExperimentId();
	public void setExperimentId(long experimentId);


	public EventSource getEventSource();
	public void setThreadName(String name);
	public String getThreadName();

	void run();


}
