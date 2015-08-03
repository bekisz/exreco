package org.exreco.experiment;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import org.exreco.experiment.Exreco.ExperimentTwoWayEventListenerProxy;
import org.exreco.experiment.dim.DimensionSet;
import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.event.PatientExitCommand;
import org.exreco.experiment.event.PatientPauseCommand;
import org.exreco.experiment.event.StartCommand;
import org.exreco.experiment.log.DynamicDimensionSetPointClassHibernateHelper;
import org.exreco.experiment.log.TableLoggers;
import org.exreco.experiment.persistence.HibernateUtil;
import org.exreco.experiment.util.AtomicDouble;
import org.exreco.experiment.util.StopWatch;
import org.exreco.experiment.util.Time;
import org.exreco.experiment.util.events.BlockingEventFloodFilter;
import org.exreco.experiment.util.events.EventHub;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;
import org.exreco.experiment.util.events.RemoteLiffEventListener;
import org.exreco.liff.core.WorldStatusEvent;

public class Experiment
		implements Iterator<CaseShellIf>, RemoteExperimentTracker, Remote, LiffEventListener<LiffEvent> {

	public final static String caseStatusTopicName = "LiffCaseStatus";
	public final static String experimentStatusTopicName = "LiffExperimentStatus";
	public final static String userCommandTopicName = "LiffUserCommand";
	// public final static String worldLogTopicName = "LiffWorldLog";

	private final EventHub<LiffEvent> experimentEventHub = new EventHub<LiffEvent>();
	// just to keep the reference during the lifcycle of these services :
	// private Collection<RemoteInsertableAdapter> remoteInsertableAdapters =
	// new ArrayList<RemoteInsertableAdapter>(5);
	private final AtomicBoolean paused = new AtomicBoolean(false);
	private boolean toExit = false;
	transient private final ReentrantLock pauseLock = new ReentrantLock();
	transient private final Condition pauseCondition = pauseLock.newCondition();

	private Deployment deployment;
	private ExperimentTracker experimentTracker;


	

	private DimensionSetPoint actualDimensionSetPoint;
	private int actualCaseId = 0;
	private static Logger logger = LogManager.getLogger(Experiment.class.getName());

	transient private TableLoggers tableLoggers;
	transient private String worldBeansXml;

	private DynamicDimensionSetPointClassHibernateHelper dynamicSetPointHelper = new DynamicDimensionSetPointClassHibernateHelper();
	
	final private EventSource<LiffEvent> eventSource = new EventSource<LiffEvent>();
	private final Map<Integer, CaseStatusEvent> caseMap = Collections
			.synchronizedMap(new LinkedHashMap<Integer, CaseStatusEvent>());
	private final Map<Integer, CaseStatusEvent> runningCaseMap = Collections
			.synchronizedMap(new LinkedHashMap<Integer, CaseStatusEvent>());
	private final Map<DimensionSetPoint, Case.LifeCycleState> point2CaseLifeCycleState = Collections
			.synchronizedMap(new LinkedHashMap<DimensionSetPoint, Case.LifeCycleState>());
	private final AtomicInteger casesStarted = new AtomicInteger(0);
	private final AtomicInteger casesEnded = new AtomicInteger(0);
	private final AtomicLong ticksEnded = new AtomicLong(0);
	private final AtomicLong ticksEndedInThisInterval = new AtomicLong(0);
	private final AtomicDouble avgExpectedTicksPerCase = new AtomicDouble(0.0);
	private final StopWatch watch = new StopWatch();
	private final AtomicInteger totalCases = new AtomicInteger(1);
	private long experimentId;

	private final Date startDate = new Date();
	private long broadcastIntervalsInMs;

	private long speedometerUpdateIntervalsInMs;
	private Date finishDate;
	private LifeCycleState lifeCycleState;
	private DimensionSet dimensionSet;
	private final SortedMap<String, CaseStatusEvent> threadPoolMap = Collections
			.synchronizedSortedMap(new TreeMap<String, CaseStatusEvent>());
	private StatusBroadcasterThread statusBroadcasterThread;
	private Speedometer SpeedometerThread;
	private double currentSpeedInTicks = 0;
	private double maxSpeedInTicks = 0;
	private double avgSpeedInTicks = 0;
	private double avgSpeedInCases = 0;
	private String description;
	private String name;
	private String version;




	private static DynamicDimensionSetPointClassHibernateHelper dynDimPointHelper = null;

	static {
		dynDimPointHelper = new DynamicDimensionSetPointClassHibernateHelper();
		dynDimPointHelper.setEmbeddable(false);
		dynDimPointHelper.setIdFieldNeeded(true);
	}

	public enum LifeCycleState implements Serializable {
		CREATED, INITED, STARTED, RUNNING, ENDED, CANCELLED

	}

	public class StatusBroadcasterThread extends Thread {
		final private long broadcastIntervalsInMs;

		public StatusBroadcasterThread(long broadcastIntervalsInMs) {
			this.broadcastIntervalsInMs = broadcastIntervalsInMs;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(this.broadcastIntervalsInMs);
					ExperimentStatusEvent experimentStatusEvent = new ExperimentStatusEvent(Experiment.this);
					Experiment.this.getEventSource().fireEvent(experimentStatusEvent);

					// HibernateUtil.saveOrUpdate(experimentStatusEvent);

				} catch (InterruptedException e) {
					break;
				} catch (Exception e) {
					Experiment.logger.error("Could not broadcast experiment status." + e.getMessage(), e);
				}
			}
		}
	}

	public class Speedometer extends Thread {
		final private long measurementInterval;

		/**
		 * 
		 * @param measurementInterval
		 *            Measured in every x milliseconds
		 */
		public Speedometer(long measurementInterval) {
			this.measurementInterval = measurementInterval;
		}

		@Override
		public void run() {

			while (Experiment.this.getLifeCycleState() != LifeCycleState.ENDED
					|| Experiment.this
							.getLifeCycleState() != LifeCycleState.CANCELLED) {
				try {
					Thread.sleep(this.measurementInterval);

					// Average Speed
					double averageSpeed = (double) 1000 * (double) Experiment.this.getTicksEnded()
							/ Experiment.this.getElapsedTime().getMilliseconds();
					Experiment.this.setAvgSpeedInTicks(averageSpeed);
					// Current Speed
					double currentSpeed = (double) 1000
							* (double) Experiment.this.ticksEndedInThisInterval.get()
							/ this.measurementInterval;
					Experiment.this.setCurrentSpeedInTicks(currentSpeed);
					Experiment.this.ticksEndedInThisInterval.set(0);
					// Max Speed
					if (currentSpeed > Experiment.this.getMaxSpeedInTicks()) {

						Experiment.this.setMaxSpeedInTicks(currentSpeed);
					}
				} catch (InterruptedException e) {
					break;
				} catch (Exception e) {
					Experiment.logger.warn("Speedometer thread interrupted", e);
				}
			}
		}

	}

	@Entity(name = "experiment")
	public static class ExperimentStatusEvent extends LiffEvent implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6198282136883920545L;

		// private final Map<Integer, CaseStatusEvent> caseMap = Collections
		// .synchronizedMap(new LinkedHashMap<Integer, CaseStatusEvent>());
		@Transient
		private final Map<Integer, CaseStatusEvent> runningCaseMap = Collections
				.synchronizedMap(new LinkedHashMap<Integer, CaseStatusEvent>());
		@Transient
		private final SortedMap<String, CaseStatusEvent> threadPoolMap = Collections
				.synchronizedSortedMap(new TreeMap<String, CaseStatusEvent>());

		private final int casesStarted;

		private final int casesEnded;
		private final int totalCases;

		private final long ticksEnded;
		private final double avgExpectedTicksPerCase;
		@Embedded
		@AttributeOverride(name = "timeInMs", column = @Column(name = "expectedRemainingTimeInMs") )
		private final Time expectedRemainingTime;
		private final double expectedTotalTicks;
		@Id
		private long experimentId;

		private final Date startDate;
		private final Date sentDate;
		private final Date finishDate;
		@Embedded
		@AttributeOverride(name = "timeInMs", column = @Column(name = "elapsedTimeInMs") )
		private final Time elapsedTime;
		private final LifeCycleState lifeCycleState;
		private final long broadcastIntervalsInMs;
		@Transient
		private final DimensionSet dimensionSet;
		private final double currentSpeedInTicks;
		private final double avgSpeedInCases;
		private final double maxSpeedInTicks;
		private final double avgSpeedInTicks;

		protected ExperimentStatusEvent(Experiment source) throws Exception {
			synchronized (source) {

				this.casesStarted = source.getCasesStarted();
				this.casesEnded = source.getCasesEnded();
				this.totalCases = source.getTotalCases();
				this.ticksEnded = source.getTicksEnded();
				this.avgExpectedTicksPerCase = source.getAvgExpectedTicksPerCase();
				this.expectedRemainingTime = source.getRemainingTime();
				this.elapsedTime = source.getElapsedTime();
				this.expectedTotalTicks = source.getExpectedTotalTicks();
				this.experimentId = source.getExperimentId();
				this.startDate = source.getStartDate();
				this.finishDate = source.getFinishDate();
				this.sentDate = new Date();

				this.getId2RunningCaseStatusMap().putAll(source.getId2RunningCaseStatusMap());
				// this.getCaseMap().putAll(source.getId2CaseStatusMap());
				this.getThreadPool2CaseStatusMap().putAll(source.getThreadPool2CaseStatusMap());
				this.lifeCycleState = source.getLifeCycleState();
				this.broadcastIntervalsInMs = source.getBroadcastIntervalsInMs();
				// TODO : Check in DimensionSet is to be Cloneable
				this.dimensionSet = (DimensionSet) source.getDimensionSet();
				this.currentSpeedInTicks = source.getCurrentSpeedInTicks();
				this.maxSpeedInTicks = source.getMaxSpeedInTicks();
				this.avgSpeedInTicks = source.getAvgSpeedInTicks();
				this.avgSpeedInCases = source.getAvgSpeedInCases();

			}
		}

		/**
		 * @return the casesStarted
		 */
		public int getCasesStarted() {
			return casesStarted;
		}

		/**
		 * @return the casesEnded
		 */
		public int getCasesEnded() {
			return casesEnded;
		}

		/**
		 * @return the totalCases
		 */
		public int getTotalCases() {
			return totalCases;
		}

		/**
		 * @return the ticksEnded
		 */
		public long getTicksEnded() {
			return ticksEnded;
		}

		/**
		 * @return the avgExpectedTicksPerCase
		 */
		public double getAvgExpectedTicksPerCase() {
			return avgExpectedTicksPerCase;
		}

		/**
		 * @return the runningCaseMap
		 */

		public Map<Integer, CaseStatusEvent> getId2RunningCaseStatusMap() {
			return runningCaseMap;
		}

		/**
		 * @return the threadPoolMap
		 */

		public SortedMap<String, CaseStatusEvent> getThreadPool2CaseStatusMap() {
			return threadPoolMap;
		}

		/**
		 * @return the expectedRemainingTime
		 */

		public Time getExpectedRemainingTime() {
			return expectedRemainingTime;
		}

		/**
		 * @return the expectedTotalTicks
		 */

		public double getExpectedTotalTicks() {
			return expectedTotalTicks;
		}

		/**
		 * @return the experimentId
		 */

		public long getExperimentId() {
			return experimentId;
		}

		/**
		 * @return the startDate
		 */
		public Date getStartDate() {
			return startDate;
		}

		/**
		 * @return the finishDate
		 */
		public Date getFinishDate() {
			return finishDate;
		}

		/**
		 * @return the sentDate
		 */
		public Date getSentDate() {
			return sentDate;
		}

		/**
		 * @return the lifeCycleState
		 */
		public LifeCycleState getLifeCycleState() {
			return lifeCycleState;
		}

		/**
		 * @return the dimensionSet
		 */

		public DimensionSet getDimensionSet() {
			return dimensionSet;
		}

		/**
		 * @return the measurementInterval
		 */
		public long getBroadcastIntervalsInMs() {
			return broadcastIntervalsInMs;
		}

		/**
		 * @return the currentSpeedInTicks
		 */

		public double getCurrentSpeedInTicks() {
			return currentSpeedInTicks;
		}

		/**
		 * @return the maxSpeedInTicks
		 */
		public double getMaxSpeedInTicks() {
			return maxSpeedInTicks;
		}

		/**
		 * @return the avgSpeedinTicks
		 */
		public double getAvgSpeedInTicks() {
			return avgSpeedInTicks;
		}

		public double getAvgSpeedInCases() {
			return avgSpeedInCases;
		}

		/**
		 * @return the elapsedTime
		 */

		public Time getElapsedTime() {
			return elapsedTime;
		}

	}

	public class Event extends LiffEvent implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8571290651423121140L;

		public Experiment getExperiment() {
			return Experiment.this;
		}

	}

	public class ExperimentStarted extends Event implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4824600494896597186L;

	}

	public class ExperimentEnded extends Event implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -31868288589771150L;

	}

	public class UserCommandListener implements LiffEventListener<LiffEvent> {

		@Override
		public void eventOccurred(org.exreco.experiment.util.events.LiffEvent event) throws Exception {
			// synchronized (Experiment.this.pauseLock) {
			Experiment.this.pauseLock.lock();
			if (event instanceof StartCommand) {
				Experiment.this.paused.set(false);
				Experiment.this.pauseCondition.signal();

			} else if (event instanceof PatientPauseCommand) {
				Experiment.this.paused.set(true);
				// Experiment.this.pauseCondition.await();
			} else if (event instanceof PatientExitCommand) {
				Experiment.this.toExit = true;
			}
			Experiment.this.pauseLock.unlock();
		}
	}

	public Experiment() {
		super();

	}

	public void init() throws Exception {
	
	
		this.setLifeCycleState(LifeCycleState.STARTED);
		
		this.getStopWatch().start();
		
		if (this.getExperimentId() == -1) {
			this.experimentId = Math.abs(new Random().nextInt());
		}
		if (this.getBroadcastIntervalsInMs() == 0) {
			this.setBroadcastIntervalsInMs(100);
		}
	
		this.statusBroadcasterThread = new StatusBroadcasterThread(this.getBroadcastIntervalsInMs());
		this.statusBroadcasterThread.start();
		
		if (this.getSpeedometerUpdateIntervalsInMs() == 0) {
			this.setBroadcastIntervalsInMs(3000);
		}

		this.SpeedometerThread = new Speedometer(this.getSpeedometerUpdateIntervalsInMs());
		this.SpeedometerThread.start();
		
		this.setActualDimensionSetPoint(dimensionSet.createMinDimensionSetPoint());
		
	
		this.experimentTracker = (ExperimentTracker) UnicastRemoteObject.exportObject(this, 0);

	}



	@Override
	public boolean hasNext() {

		return !this.getActualDimensionSetPoint().isMax();
	}

	@Override
	synchronized public CaseShellIf next() {

		boolean couldIncrease = this.getActualDimensionSetPoint().increase();

		if (!couldIncrease) {
			logger.warn("No next case available. Returning null.");
			return null;
		}
		CaseShellIf caseIf = new CaseShell(this.getWorldBeansXml(), "world");
		DimensionSetPoint clonedPoint = this.getActualDimensionSetPoint().clone();
		caseIf.setDimensionSetPoint(clonedPoint);
		caseIf.setCaseId(this.getActualCaseId());
		caseIf.setLog4j2ConfigLocation(this.getDeployment().getLog4j2ConfigLocation());
		this.actualCaseId++;
		return caseIf;
	}

	@Override
	public void remove() {
		new UnsupportedOperationException();

	}

	public DimensionSetPoint getActualDimensionSetPoint() {
		return actualDimensionSetPoint;
	}

	public void setActualDimensionSetPoint(DimensionSetPoint actualDimensionSetPoint) {
		this.actualDimensionSetPoint = actualDimensionSetPoint;
	}

	public int getActualCaseId() {
		return actualCaseId;
	}

	protected void setActualCaseId(int actualCaseId) {
		this.actualCaseId = actualCaseId;
	}

	/**
	 * @return the tables
	 */
	public TableLoggers getTableLoggers() {
		return tableLoggers;
	}

	/**
	 * @param tables
	 *            the tables to set
	 */
	public void setTableLoggers(TableLoggers tables) {
		this.tableLoggers = tables;
	}

	public String getWorldBeansXml() {
		return worldBeansXml;
	}

	public void setWorldBeansXml(String worldBeansXml) {
		this.worldBeansXml = worldBeansXml;
	}

	public void run() throws Exception {

		// this.getExperimentEventHub().fireEvent(new ExperimentStarted());
		// Case.getEventSource().getListeners().add(this.getExperimentEventHub());
		// LiffEventListener<LiffEvent> caseTrackerListener = new
		// DelayingEventFloodFilter(
		// new RmiExperimentTrackerAdapter(this.getCaseTracker()));
		// LiffEvent2JmsEventAdapter liffEvent2JmsEventAdapter = new
		// LiffEvent2JmsEventAdapter(
		// "LiffCaseStatus");

		// CaseShellIf Events-> Two-way -> BlockingEventFloodFilter
		// -> To.StatusEvent Converter -> caseStatusEvent Topic

		LiffEventListener<LiffEvent> liffEventListener = this.getDeployment().getEventTopicHome()
				.getEventListener(caseStatusTopicName);

		LiffEventListener<LiffEvent> toStatusEventConverter = new Case.ToStatusLiffEventConverter(liffEventListener);
		BlockingEventFloodFilter<LiffEvent> delayEventFloodFilter = new BlockingEventFloodFilter<LiffEvent>(
				toStatusEventConverter, 250);
		LiffEventListener<LiffEvent> twoWayEventListenerProxy = new ExperimentTwoWayEventListenerProxy(
				toStatusEventConverter, delayEventFloodFilter);

		while (this.hasNext() && !this.toExit) {

			while (this.paused.get()) {
				this.pauseLock.lock();
				this.pauseCondition.await();
				this.pauseLock.unlock();
			}

			CaseShellIf runableCase = this.next();
			// DimensionSetPoint point = runableCase.getDimensionSetPoint();
			// runableCase.setExperimentTracker(this.getExperimentTracker());
			runableCase.setExperimentId(this.getExperimentTracker().getExperimentId());
			// if (this.getExperimentTracker().getCaseStatus(point) ==
			// Case.LifeCycleState.ENDED) {
			// continue;
			// }

			ThreadContext.put("case-id", String.valueOf(runableCase.getCaseId()));
			// CaseShellIf Events -> Tableloggers
			runableCase.getEventSource().getListeners().add(this.getTableLoggers());

			// CaseShellIf Events -> CaseStatus Topic

			runableCase.getEventSource().getListeners().add(twoWayEventListenerProxy);

			// logger.debug("Case started with dimension : {}", runableCase
			// .getDimensionSetpoint().toString());


			this.getDeployment().getExecutor().execute(runableCase);
			logger.debug("Case {} submitted for execution.", runableCase.getCaseId());

		}
		ThreadContext.put("case-id", "");
	}

	public Deployment getDeployment() {
		return deployment;
	}

	public void setDeployment(Deployment deployment) {
		this.deployment = deployment;
	}

	/**
	 * @return the experimentTracker
	 */
	public ExperimentTracker getExperimentTracker() {
		return experimentTracker;
	}

	/**
	 * @param experimentTracker
	 *            the experimentTracker to set
	 */
	public void setExperimentTracker(ExperimentTracker experimentTracker) {
		this.experimentTracker = experimentTracker;
	}

	/**
	 * Called at the end of the experiment
	 */
	protected void finish() {
		this.setLifeCycleState(LifeCycleState.ENDED);
		this.setFinishDate(new Date());
	}

	@Override
	synchronized public void onCaseUpdated(CaseStatusEvent status) throws Exception {
		// logger.debug("Experiment Tracker : case status received");
		if (status.getExperimentId() != this.getExperimentId()) {
			return;
		}
		this.setLifeCycleState(LifeCycleState.RUNNING);
		int caseId = status.getCaseId();
		int permutations = status.getDimensionSetPoint().getDimensionSet().getPermutations();
		String threadName = status.getHostName() + ":" + status.getThreadName();
		threadName = threadName.replaceAll("node processing-", "");

		if (status.getLifeCycleState() == Case.LifeCycleState.STARTED
				|| status.getLifeCycleState() == Case.LifeCycleState.INITED) {

			if (this.getCasesStarted() == 0) {
				this.totalCases.set(permutations);

				if (status instanceof WorldStatusEvent) {
					WorldStatusEvent worldstatus = (WorldStatusEvent) status;
					this.avgExpectedTicksPerCase.set(worldstatus.getMaxLifeTime());
				}
			}
			if (!getId2RunningCaseStatusMap().containsKey(caseId)) {
				this.getId2RunningCaseStatusMap().put(caseId, status);
				this.casesStarted.getAndIncrement();
				this.getThreadPool2CaseStatusMap().put(threadName, status);
			}
			Object dynamicDimensionSetpoint;
			Map<String, Object> caseIdMap = new HashMap<String, Object>();
			caseIdMap.put("caseId", status.getCaseId());
			caseIdMap.put("experimentId", status.getExperimentId());
			try {
				dynamicDimensionSetpoint = dynDimPointHelper.createDynamicObject(status.getDimensionSetPoint(),
						caseIdMap);
				HibernateUtil.saveOrUpdate(dynamicDimensionSetpoint);
			} catch (Exception e) {
				Case.logger.error("Could not create dynamic dimension set point. ", e);

			}

		} else if (status.getLifeCycleState() == Case.LifeCycleState.ENDED) {

			if (this.getId2RunningCaseStatusMap().remove(caseId) != null) {
				this.getThreadPool2CaseStatusMap().put(threadName, null);
				this.casesEnded.getAndIncrement();
				if (this.getCasesEnded() == this.getTotalCases()) {
					this.finish(); // End of experiment
				}
				double averageSpeedInCases = (double) 60 * 1000 * (double) Experiment.this.getCasesEnded()
						/ Experiment.this.getElapsedTime().getMilliseconds();
				this.setAvgSpeedInCases(averageSpeedInCases);
			}

			int casesEnded = this.getCasesEnded();
			if (status instanceof WorldStatusEvent) {
				WorldStatusEvent worldstatus = (WorldStatusEvent) status;
				this.avgExpectedTicksPerCase.set(worldstatus.getMaxLifeTime());

				boolean isAvgLifeTimeSuccessfullySet = false;
				while (!isAvgLifeTimeSuccessfullySet) {

					Double originalAvgLifeTime = this.getAvgExpectedTicksPerCase();
					Double newAvgLifeTime = (double) worldstatus.getAge() / casesEnded
							+ this.getAvgExpectedTicksPerCase() * (casesEnded - 1) / casesEnded;

					if (this.avgExpectedTicksPerCase.compareAndSet(originalAvgLifeTime, newAvgLifeTime))
						isAvgLifeTimeSuccessfullySet = true;
				}
				WorldStatusEvent oldWorldstatus = (WorldStatusEvent) this.getId2RunningCaseStatusMap()
						.get(status.getCaseId());
				long addedTicks = 0;
				if (oldWorldstatus != null) {
					addedTicks = worldstatus.getAge() - oldWorldstatus.getAge();
				}

				this.ticksEnded.addAndGet(addedTicks);
				this.ticksEndedInThisInterval.addAndGet(addedTicks);
				if (status.getDimensionSetPoint().isMax()) {
					this.getStopWatch().stop();
				}
			}
		} else if (status.getLifeCycleState() == Case.LifeCycleState.RUNNING) {

			if (status instanceof WorldStatusEvent) {
				WorldStatusEvent worldstatus = (WorldStatusEvent) status;
				WorldStatusEvent oldWorldstatus = (WorldStatusEvent) this.getId2RunningCaseStatusMap()
						.get(status.getCaseId());
				long addedTicks;
				if (oldWorldstatus != null) {
					addedTicks = worldstatus.getAge() - oldWorldstatus.getAge();

				} else {
					addedTicks = worldstatus.getAge();
				}
				this.ticksEnded.addAndGet(addedTicks);
				this.ticksEndedInThisInterval.addAndGet(addedTicks);

			}
			this.getId2RunningCaseStatusMap().put(caseId, status);
			// --

			this.getThreadPool2CaseStatusMap().put(threadName, status);
		}

		// this.getCaseMap().put(caseId, status);
		this.getPoint2CaseLifeCycleState().put(status.getDimensionSetPoint(), status.getLifeCycleState());

		HibernateUtil.saveOrUpdate(status);

	}

	/**
	 * @return the caseMap
	 */
	@Override
	public Map<Integer, CaseStatusEvent> getId2CaseStatusMap() {
		return caseMap;
	}

	/**
	 * @return the runningCaseMap
	 */
	@Override
	public Map<Integer, CaseStatusEvent> getId2RunningCaseStatusMap() {
		return runningCaseMap;
	}

	@Override
	public StopWatch getStopWatch() {
		return watch;
	}

	@Override
	public int getTotalCases() throws Exception {

		return this.totalCases.get();
	}

	@Override
	public double getExpectedTotalTicks() throws Exception {
		return this.getTotalCases() * this.getAvgExpectedTicksPerCase();
	}

	@Override
	public Time getRemainingTime() throws Exception {
		this.getStopWatch().touch();

		long ms = this.getStopWatch().getMilliseconds();
		long remainingMs = (long) ((((this.getExpectedTotalTicks()) / (this.getTicksEnded())) - 1) * ms);

		return new Time(remainingMs);
	}

	@Transient
	public Time getElapsedTime() throws Exception {
		this.getStopWatch().touch();

		return this.getStopWatch();
	}

	/**
	 * @return the casesStarted
	 */
	@Override
	public int getCasesStarted() throws Exception {
		return this.casesStarted.get();
	}

	/**
	 * @return the casesEnded
	 */
	@Override
	public int getCasesEnded() throws Exception {
		return this.casesEnded.get();

	}

	/**
	 * @return the ticksEnded
	 */
	@Override
	public long getTicksEnded() throws Exception {
		return this.ticksEnded.get();
	}

	/**
	 * @return the watch
	 */
	@Override
	public StopWatch getWatch() throws Exception {
		return watch;
	}

	/**
	 * @return the avgExpectedTicksPerCase
	 */
	@Override
	public Double getAvgExpectedTicksPerCase() throws Exception {
		return avgExpectedTicksPerCase.get();
	}

	/**
	 * @return the experimentId
	 */
	@Id
	public long getExperimentId() throws Exception {
		return experimentId;
	}

	/**
	 * @return the threadPoolMap
	 */
	@Override
	public SortedMap<String, CaseStatusEvent> getThreadPool2CaseStatusMap() throws Exception {
		return threadPoolMap;
	}

	/**
	 * @return the eventSource
	 */
	@Override
	public EventSource<LiffEvent> getEventSource() throws Exception {
		return eventSource;
	}

	@Override
	public void addListener(RemoteLiffEventListener listener) throws Exception {
		this.getEventSource().getListeners().add(listener);
	}

	@Override
	public void removeListener(RemoteLiffEventListener listener) throws Exception {
		this.getEventSource().getListeners().remove(listener);
	}

	@Override
	public void eventOccurred(LiffEvent event) throws Exception {

		if (event instanceof CaseStatusEvent) {
			CaseStatusEvent caseStatusEvent = (CaseStatusEvent) event;

			this.onCaseUpdated(caseStatusEvent);
		}

	}

	/**
	 * @return the startDate
	 */

	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return the finishDate
	 */
	public Date getFinishDate() {
		return finishDate;
	}

	/**
	 * @param finishDate
	 *            the finishDate to set
	 */
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
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
	 * @return the measurementInterval
	 */
	@Override
	public long getBroadcastIntervalsInMs() {
		return broadcastIntervalsInMs;
	}

	/**
	 * @param measurementInterval
	 *            the measurementInterval to set
	 */
	public void setBroadcastIntervalsInMs(long broadcastIntervalsInMs) {
		this.broadcastIntervalsInMs = broadcastIntervalsInMs;
	}

	/**
	 * @return the dimensionSet
	 */
	@Override
	@Transient
	public DimensionSet getDimensionSet() {
		return dimensionSet;
	}

	/**
	 * @param dimensionSet
	 *            the dimensionSet to set
	 */
	public void setDimensionSet(DimensionSet dimensionSet) {
		this.dimensionSet = dimensionSet;
	}

	/**
	 * @return the statusBroadcasterThread
	 */
	public StatusBroadcasterThread getStatusBroadcasterThread() {
		return statusBroadcasterThread;
	}

	/**
	 * @return the currentSpeedInTicks
	 */
	public double getCurrentSpeedInTicks() {
		return currentSpeedInTicks;
	}

	/**
	 * @param currentSpeedInTicks
	 *            the currentSpeedInTicks to set
	 */
	public void setCurrentSpeedInTicks(double currentSpeed) {
		this.currentSpeedInTicks = currentSpeed;
	}

	/**
	 * @return the avgSpeedinTicks
	 */
	public double getAvgSpeedInTicks() {
		return avgSpeedInTicks;
	}

	/**
	 * @param avgSpeedinTicks
	 *            the avgSpeedinTicks to set
	 */
	public void setAvgSpeedInTicks(double avgSpeed) {
		this.avgSpeedInTicks = avgSpeed;
	}

	public double getAvgSpeedInCases() {
		return avgSpeedInCases;
	}

	public void setAvgSpeedInCases(double avgSpeedInCases) {
		this.avgSpeedInCases = avgSpeedInCases;
	}

	/**
	 * @return the maxSpeedInTicks
	 */
	public double getMaxSpeedInTicks() {
		return maxSpeedInTicks;
	}

	/**
	 * @param maxSpeedInTicks
	 *            the maxSpeedInTicks to set
	 */
	public void setMaxSpeedInTicks(double maxSpeed) {
		this.maxSpeedInTicks = maxSpeed;
	}

	@Override
	public org.exreco.experiment.Case.LifeCycleState getCaseStatus(DimensionSetPoint point) throws Exception {
		Case.LifeCycleState state = this.getPoint2CaseLifeCycleState().get(point);
		if (state == null) {
			state = Case.LifeCycleState.NULL;

		}
		if (state == Case.LifeCycleState.RUNNING || state == Case.LifeCycleState.STARTED
				|| state == Case.LifeCycleState.ENDED) {
			logger.debug("Active state");
		}
		return state;
	}

	@Override
	public org.exreco.experiment.Case.LifeCycleState getCaseStatus(int caseId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the point2CaseLifeCycleState
	 */
	public Map<DimensionSetPoint, Case.LifeCycleState> getPoint2CaseLifeCycleState() {
		return point2CaseLifeCycleState;
	}

	public EventHub<LiffEvent> getExperimentEventHub() {
		return experimentEventHub;
	}
	/**
	 * @return the pauseLock
	 */
	public ReentrantLock getPauseLock() {
		return pauseLock;
	}

	/**
	 * @return the pauseCondition
	 */
	public Condition getUnpaused() {
		return pauseCondition;
	}

	public long getSpeedometerUpdateIntervalsInMs() {
		return speedometerUpdateIntervalsInMs;
	}

	public void setSpeedometerUpdateIntervalsInMs(long speedometerUpdateIntervalsInMs) {
		this.speedometerUpdateIntervalsInMs = speedometerUpdateIntervalsInMs;
	}

	public void setExperimentId(long experimentId) {
		this.experimentId = experimentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
