package org.exreco.experiment;

import java.util.Map;
import java.util.SortedMap;

import org.exreco.experiment.dim.DimensionSet;
import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.util.StopWatch;
import org.exreco.experiment.util.Time;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.RemoteLiffEventListener;


public interface ExperimentTrackerOutput {
	public Map<Integer, CaseStatusEvent> getId2CaseStatusMap() throws Exception;

	/**
	 * @return the runningCaseMap
	 */
	public Map<Integer, CaseStatusEvent> getId2RunningCaseStatusMap() throws Exception;

	public StopWatch getStopWatch() throws Exception;

	public int getTotalCases() throws Exception;

	public double getExpectedTotalTicks() throws Exception;

	public Time getRemainingTime() throws Exception;

	public long getExperimentId() throws Exception;

	public DimensionSet getDimensionSet() throws Exception;

	public long getBroadcastIntervalsInMs() throws Exception;

	Case.LifeCycleState getCaseStatus(DimensionSetPoint point) throws Exception;

	Case.LifeCycleState getCaseStatus(int caseId) throws Exception;

	/**
	 * @return the casesStarted
	 */
	public int getCasesStarted() throws Exception;

	/**
	 * @return the casesEnded
	 */
	public int getCasesEnded() throws Exception;

	/**
	 * @return the ticksEnded
	 */
	public long getTicksEnded() throws Exception;

	/**
	 * @return the watch
	 * @throws Exception
	 */
	public StopWatch getWatch() throws Exception;

	/**
	 * @return the avgExpectedTicksPerCase
	 */
	public Double getAvgExpectedTicksPerCase() throws Exception;

	/**
	 * @return the threadPoolMap
	 */
	public SortedMap<String, CaseStatusEvent> getThreadPool2CaseStatusMap()
			throws Exception;

	/**
	 * @return the eventSource
	 */
	public EventSource<LiffEvent> getEventSource() throws Exception;

	public void addListener(RemoteLiffEventListener listener) throws Exception;

	public void removeListener(RemoteLiffEventListener listener)
			throws Exception;

}
