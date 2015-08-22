package org.exreco.experiment;

import java.io.Serializable;

import javax.persistence.EmbeddedId;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventConverter;
import org.exreco.experiment.util.events.LiffEventListener;

public abstract class Case implements Serializable, AgeTracked, CaseIf {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6181180460065112512L;
	private final EventSource eventSource = new EventSource();
	private DimensionSetPoint dimensionSetpoint;
	private String threadName;
	private int isoCaseId = 0;
	private int caseId;
	private LifeCycleState lifeCycleState = LifeCycleState.CREATED;
	private long experimentId;
	private CaseInitializerIf caseInitializer;

	@SuppressWarnings("unused")
	static Logger logger = LogManager.getLogger(Case.class.getName());

	// private TableLoggers tableLoggers;

	// private Experiment experiment;
	public enum LifeCycleState implements Serializable {
		PLANNED, SUBMITTED, CREATED, INITED, STARTED, RUNNING, ENDED, CANCELLED, NULL

	}

	public class Event extends LiffEvent implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7990487949363255424L;

		/**
		 * @return the world
		 */
		public Case getCase() {
			return Case.this;
		}

	}

	public class RunStartedEvent extends Event implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2274477114130509027L;

	}

	public class RunEndedEvent extends Event implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1823442099248156405L;

	}

	public class TickStartedEvent extends Event implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2475937210034403403L;

	}

	public class TickEndedEvent extends Event implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5089656720513615071L;

	}

	public static class ToStatusLiffEventConverter extends
			LiffEventConverter implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1807671701294776564L;

		public ToStatusLiffEventConverter(LiffEventListener proxied) {
			super(proxied);

		}

		protected ToStatusLiffEventConverter() {
			super();

		}

		@Override
		protected Serializable convert(Serializable event) {
			Case.Event caseEvent = (Case.Event) event;
			CaseStatusEvent caseStatusEvent = caseEvent.getCase()
					.createStatus();

			return caseStatusEvent;
		}
	}

	public void init() {
		this.getCaseInitializer().init(this);
		this.lifeCycleState = LifeCycleState.INITED;
	}

	public void run() {
		// logger.debug("Runing case {}", this.getCaseId());
		this.setLifeCycleState(LifeCycleState.STARTED);

	}

	public void finish() {
		this.setLifeCycleState(LifeCycleState.ENDED);
	}

	/**
	 * @return the experiment
	 */
	/*
	 * public Experiment getExperiment() { return experiment; }
	 */

	/**
	 * @return the dimensionSetpoint
	 */
	public DimensionSetPoint getDimensionSetPoint() {
		return dimensionSetpoint;
	}

	abstract public int getMaxLifeTime();

	/**
	 * @param dimensionSetpoint
	 *            the dimensionSetpoint to set
	 */
	public void setDimensionSetPoint(DimensionSetPoint dimensionSetpoint) {
		this.dimensionSetpoint = dimensionSetpoint;
	}

	/**
	 * @return the threadName
	 */
	synchronized public String getThreadName() {
		return threadName;
	}

	/**
	 * @param threadName
	 *            the threadName to set
	 */
	synchronized public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * @return the endOfTickEventSource
	 */
	public EventSource getEventSource() {
		return eventSource;
	}

	/**
	 * @return the caseId
	 */
	public int getCaseId() {
		return caseId;
	}

	/**
	 * @param caseId
	 *            the caseId to set
	 */
	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}

	/**
	 * @return the isoCaseId
	 */
	public int getIsoCaseId() {
		return isoCaseId;
	}

	/**
	 * @param isoCaseId
	 *            the isoCaseId to set
	 */
	public void setIsoCaseId(int isoCaseId) {
		this.isoCaseId = isoCaseId;
	}

	public CaseStatusEvent createStatus() {
		return new CaseStatusEvent(this);
	}

	/**
	 * @return the lifeCycleState
	 */
	public LifeCycleState getLifeCycleState() {
		return lifeCycleState;
	}

	/**
	 * @param lifeCycleState
	 *            the lifeCycleState to set
	 */
	public void setLifeCycleState(LifeCycleState lifeCycleState) {
		this.lifeCycleState = lifeCycleState;
	}

	/**
	 * @return the experimentId
	 */
	public long getExperimentId() {
		return experimentId;
	}

	/**
	 * @param experimentId
	 *            the experimentId to set
	 */
	public void setExperimentId(long experimentId) {
		this.experimentId = experimentId;
	}

	public CaseInitializerIf getCaseInitializer() {
		return caseInitializer;
	}

	public void setCaseInitializer(CaseInitializerIf caseInitializer) {
		this.caseInitializer = caseInitializer;
	}

}
