package org.exreco.experiment;


import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.exreco.experiment.event.PatientExitCommand;
import org.exreco.experiment.event.PatientPauseCommand;
import org.exreco.experiment.event.StartCommand;
import org.exreco.experiment.log.Insertable;
import org.exreco.experiment.log.Insertable2LiffEventAdapter;
import org.exreco.experiment.log.LiffEvent2InsertableAdapter;
import org.exreco.experiment.log.RemoteInsertableAdapter;
import org.exreco.experiment.log.Session;
import org.exreco.experiment.log.TableLogger;
import org.exreco.experiment.log.TableLoggers;
import org.exreco.experiment.persistence.HibernateUtil;
import org.exreco.experiment.util.events.BlockingEventFloodFilter;
import org.exreco.experiment.util.events.EventHub;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;
import org.exreco.experiment.util.events.TwoWayEventListenerProxy;
import org.exreco.liff.core.World;


public class Exreco {
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -6033112864323245890L;
	private static Logger logger = LogManager.getLogger(Exreco.class
			.getName());
	public final static String caseStatusTopicName = "LiffCaseStatus";
	public final static String experimentStatusTopicName = "LiffExperimentStatus";
	public final static String userCommandTopicName = "LiffUserCommand";
	// public final static String worldLogTopicName = "LiffWorldLog";


	private final EventHub<LiffEvent> experimentEventHub = new EventHub<LiffEvent>();
	// just to keep the reference during the lifcycle of these services :
	// private Collection<RemoteInsertableAdapter> remoteInsertableAdapters = new ArrayList<RemoteInsertableAdapter>(5);
	private final AtomicBoolean paused = new AtomicBoolean(false);
	private boolean toExit = false;
	transient private final ReentrantLock pauseLock = new ReentrantLock();
	transient private final Condition pauseCondition = pauseLock.newCondition();

	private ExperimentTracker experimentTracker;
	private ExperimentTrackerImpl experimentTrackerImpl;




	transient private TableLoggers tableLoggers;


	
	//private Session session;

	private ExecutorService executor;
	private EventTopicHome eventTopicHome;
	private Experiment experiment;


	public class Event extends LiffEvent implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8571290651423121140L;

		public Exreco getExperiment() {
			return Exreco.this;
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

	protected static class ExperimentTwoWayEventListenerProxy
			extends TwoWayEventListenerProxy<LiffEvent> {
		private static final long serialVersionUID = -2506355405661545709L;

		public ExperimentTwoWayEventListenerProxy(
				LiffEventListener<LiffEvent> defaultWay,
				LiffEventListener<LiffEvent> alternateProxied) {
			super(defaultWay, alternateProxied);

		}

		@Override
		protected boolean isSwitched(LiffEvent event) {
			return (event instanceof World.TickEndedEvent);

		}

	};

	private class UserCommandListener implements LiffEventListener<LiffEvent> {

		@Override
		public void eventOccurred(
				org.exreco.experiment.util.events.LiffEvent event)
				throws Exception {
			// synchronized (Experiment.this.pauseLock) {
			Exreco.this.pauseLock.lock();
			if (event instanceof StartCommand) {
				Exreco.this.paused.set(false);
				Exreco.this.pauseCondition.signal();

			} else if (event instanceof PatientPauseCommand) {
				Exreco.this.paused.set(true);
				// Experiment.this.pauseCondition.await();
			} else if (event instanceof PatientExitCommand) {
				Exreco.this.toExit = true;
			}
			Exreco.this.pauseLock.unlock();
		}
	}



	public Exreco() {
	}

	public void init() throws Exception {

		this.experimentTrackerImpl = new ExperimentTrackerImpl();
		this.experimentTrackerImpl.setDimensionSet(this.getExperiment().getDimensionSet());
		this.experimentTracker = (ExperimentTracker) UnicastRemoteObject
				.exportObject(this.experimentTrackerImpl, 0);
		this.wireEvents();
	}

	protected void wireEvents() throws Exception {

		// this.getExperimentEventHub().getListeners()
		// .add(new RmiExperimentTrackerAdapter(this.getCaseTracker()));

			// CaseStatus Events Topic ->  ExperimentTracker
		this.getEventTopicHome().getEventSource(caseStatusTopicName)
				.getListeners().add(this.experimentTrackerImpl);

			//  ExperimentEventHub ->  CaseStatus Events Topic
		this.getExperimentEventHub()
				.getListeners()
				.add(this.getEventTopicHome().getEventListener(
						caseStatusTopicName));

		//  ExperimentEventHub ->  ExperimentStatus Events Topic
		this.experimentTrackerImpl
				.getEventSource()
				.getListeners()
				.add(this.getEventTopicHome().getEventListener(
						experimentStatusTopicName));

		// this.getExperimentEventHub().fireEvent(new ExperimentStarted());
			
//		for (TableLogger tableLogger : this.getTableLoggers().getTableLoggerMap().values()) {
//			// tableLogger.
//			boolean synchronousTableWrite = true;
//			if (synchronousTableWrite) {
//				RemoteInsertableAdapter remoteInsertableAdapter = new RemoteInsertableAdapter(
//						tableLogger.getTable());
//				Insertable insertable = (Insertable) UnicastRemoteObject
//						.exportObject(remoteInsertableAdapter, 0);
//				tableLogger.setInsertable(insertable);
//				this.remoteInsertableAdapters.add(remoteInsertableAdapter);
//			} else {
//				LiffEventListener<LiffEvent> event = this.getEventTopicHome()
//						.getEventListener(worldLogTopicName);
//				tableLogger
//						.setInsertable(new Insertable2LiffEventAdapter(event));
//				LiffEvent2InsertableAdapter<LiffEvent> liffEvent2InsertableAdapter = new LiffEvent2InsertableAdapter<LiffEvent>(
//						tableLogger.getTable());
//
//				this.getEventTopicHome().getEventSource(worldLogTopicName)
//						.getListeners().add(liffEvent2InsertableAdapter);
//			}
//
//		}
		
		//  User Command Event ->  UserCommandListener
		this.getEventTopicHome().getEventSource(userCommandTopicName)
				.getListeners().add(new UserCommandListener());
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

	public void run() throws Exception {

		//ExperimentXmlNode.CaseIterator it = this.getCaseIterator();
		
		Experiment experiment = this.getExperiment();
		// this.getExperimentEventHub().fireEvent(new ExperimentStarted());
		// Case.getEventSource().getListeners().add(this.getExperimentEventHub());
		// LiffEventListener<LiffEvent> caseTrackerListener = new
		// DelayingEventFloodFilter(
		// new RmiExperimentTrackerAdapter(this.getCaseTracker()));
		// LiffEvent2JmsEventAdapter liffEvent2JmsEventAdapter = new
		// LiffEvent2JmsEventAdapter(
		// "LiffCaseStatus");
		
		// CaseShellIf Events-> Two-way -> BlockingEventFloodFilter 
		//	-> To.StatusEvent Converter -> caseStatusEvent Topic
	
		LiffEventListener<LiffEvent> liffEventListener = this.getEventTopicHome()
				.getEventListener(caseStatusTopicName);

		LiffEventListener<LiffEvent> toStatusEventConverter = new Case.ToStatusLiffEventConverter(
				liffEventListener);
		BlockingEventFloodFilter<LiffEvent> delayEventFloodFilter = new BlockingEventFloodFilter<LiffEvent>(
				toStatusEventConverter, 250);
		LiffEventListener<LiffEvent> twoWayEventListenerProxy = new ExperimentTwoWayEventListenerProxy(
				toStatusEventConverter, delayEventFloodFilter);

		while (experiment.hasNext() && !this.toExit) {

			while (this.paused.get()) {
				Exreco.this.pauseLock.lock();
				Exreco.this.pauseCondition.await();
				Exreco.this.pauseLock.unlock();
			}

			CaseShellIf runableCase = experiment.next();
			//DimensionSetPoint point = runableCase.getDimensionSetPoint();
			//runableCase.setExperimentTracker(this.getExperimentTracker());
			runableCase.setExperimentId(this.getExperimentTracker().getExperimentId());
			// if (this.getExperimentTracker().getCaseStatus(point) ==
			// Case.LifeCycleState.ENDED) {
			// continue;
			// }

			ThreadContext.put("case-id",
					String.valueOf(runableCase.getCaseId()));
			// CaseShellIf Events -> Tableloggers
			runableCase.getEventSource().getListeners()
					.add(this.getTableLoggers());

			// CaseShellIf Events -> CaseStatus Topic

			runableCase.getEventSource().getListeners()
					.add(twoWayEventListenerProxy);

			// logger.debug("Case started with dimension : {}", runableCase
			// .getDimensionSetpoint().toString());
			this.getExecutor().execute(runableCase);
			logger.debug("Case submitted for execution");

		}
		ThreadContext.put("case-id", "");

		this.getExecutor().awaitTermination(Long.MAX_VALUE,
				TimeUnit.MILLISECONDS);
		this.getExecutor().shutdown();
		logger.debug("Awaiting termination...");

		// Case.getEventSource().getListeners()
		// .remove(this.getExperimentEventHub());
		this.finish();
		logger.debug("Experiment finished");
	}

	protected void finish() throws Exception {

		// this.getExperimentEventHub().fireEvent(new ExperimentEnded());
		this.getExperimentEventHub().getListeners().clear();

		HibernateUtil.getSessionFactory().close();

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

	/*
	 * public Case getCurrentCase() { return currentCase; }
	 * 
	 * private void setCurrentCase(Case currentCase) throws Exception {
	 * 
	 * if (this.currentCase != null) {
	 * 
	 * this.currentCase = currentCase; } if (currentCase != null) {
	 * 
	 * this.currentCase = currentCase;
	 * 
	 * } }
	 */

	/**
	 * @return the worldEventSource
	 */
	public EventHub<LiffEvent> getExperimentEventHub() {
		return this.experimentEventHub;
	}






	/**
	 * @return the executor
	 */
	public ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
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
	 * @return the eventTopicHome
	 */
	public EventTopicHome getEventTopicHome() {
		return eventTopicHome;
	}

	/**
	 * @param eventTopicHome
	 *            the eventTopicHome to set
	 */
	public void setEventTopicHome(EventTopicHome eventTopicHome) {
		this.eventTopicHome = eventTopicHome;
	}


	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment multiplicator) {
		this.experiment = multiplicator;
	}
}
