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
import org.exreco.experiment.util.events.EventSource;
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

	private Deployment deployment;
	private Experiment experiment;




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


	public Exreco() {
	}

	public void init() throws Exception {

		this.wireEvents();
	}

	static public void wire(EventSource<LiffEvent> from, LiffEventListener<LiffEvent> to) {
		from.wireTo(to);
	}
	
	protected void wireEvents() throws Exception {

		// this.getExperimentEventHub().getListeners()
		// .add(new RmiExperimentTrackerAdapter(this.getCaseTracker()));

			// CaseStatus Events Topic ->  ExperimentTracker
		Exreco.wire(this.getDeployment().getEventTopicHome().getEventSource(Experiment.caseStatusTopicName), this.getExperiment());

		//  ExperimentEventHub ->  CaseStatus Events Topic
	
		Exreco.wire(this.getExperiment().getExperimentEventHub(), this.getDeployment().getEventTopicHome().getEventListener(
				Experiment.caseStatusTopicName));
		

		//  ExperimentEventHub ->  ExperimentStatus Events Topic
		Exreco.wire(this.getExperiment()
				.getEventSource(),this.getDeployment().getEventTopicHome().getEventListener(
						Experiment.experimentStatusTopicName));
		
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
		this.getDeployment().getEventTopicHome().getEventSource(Experiment.userCommandTopicName)
				.wireTo(this.getExperiment().new UserCommandListener());
	}


	

	public void run() throws Exception {

		//ExperimentXmlNode.CaseIterator it = this.getCaseIterator();
		
	
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
		/*
		LiffEventListener<LiffEvent> liffEventListener = this.getDeployment().getEventTopicHome()
				.getEventListener(Experiment.caseStatusTopicName);

		LiffEventListener<LiffEvent> toStatusEventConverter = new Case.ToStatusLiffEventConverter(
				liffEventListener);
		BlockingEventFloodFilter<LiffEvent> delayEventFloodFilter = new BlockingEventFloodFilter<LiffEvent>(
				toStatusEventConverter, 50);
		LiffEventListener<LiffEvent> twoWayEventListenerProxy = new ExperimentTwoWayEventListenerProxy(
				toStatusEventConverter, delayEventFloodFilter);
		*/
		this.getExperiment().run();
		
		ThreadContext.put("case-id", "");
		this.getDeployment().getExecutor().shutdown();
		logger.debug("Awaiting termination...");
		this.getDeployment().getExecutor().awaitTermination(Long.MAX_VALUE,
				TimeUnit.MILLISECONDS);

		logger.debug("Finishing exreco...");

		// Case.getEventSource().getListeners()
		// .remove(this.getExperimentEventHub());
		this.finish();
		logger.debug("Experiment finished");
	}

	protected void finish() throws Exception {

		// this.getExperimentEventHub().fireEvent(new ExperimentEnded());
		this.getExperiment().getExperimentEventHub().unwireAll();

		HibernateUtil.getSessionFactory().close();

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









	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment multiplicator) {
		this.experiment = multiplicator;
	}

	public Deployment getDeployment() {
		return deployment;
	}

	public void setDeployment(Deployment deployment) {
		this.deployment = deployment;
	}
}
