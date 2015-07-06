package org.exreco.experiment;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.exreco.experiment.Case.LifeCycleState;
import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.dim.DimensionValue;
import org.exreco.experiment.dim.ListedSpringReferenceDimension;
import org.exreco.experiment.util.LiffUtils;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.LiffEvent;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CaseShell implements CaseShellIf, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5011900262234620639L;
	private DimensionSetPoint dimensionSetPoint;
	private String caseTemplateBeanName = null;
	private String threadName;
	private int caseId;
	private CaseIf proxiedCase = null;
	private ApplicationContext context;
	private final EventSource<LiffEvent> eventSource = new EventSource<LiffEvent>();
	// private ExperimentTracker experimentTracker;
	private long experimentId;


	private static Logger logger = LogManager.getLogger(CaseShell.class
			.getName());

	public CaseShell(String caseTemplateBeanName) {
		super();
		this.caseTemplateBeanName = caseTemplateBeanName;
	}

	protected void insertDimensionSetPoint() {
		BeanWrapper beanWrapper = PropertyAccessorFactory
				.forBeanPropertyAccess(this.getProxiedCase());
		for (DimensionValue dimVal : this.getDimensionSetPoint()) {
			Object obj = dimVal.getValue();
			String propetyName = dimVal.getDimensionType().getName();
			
			// insert late binding beans reference dimenson values
			if (dimVal.getDimensionType() instanceof ListedSpringReferenceDimension) {
				String refenceName = obj.toString();
				obj = context.getBean(refenceName);
			}
			beanWrapper.setPropertyValue(propetyName, obj);

		}

	}

	private void initProxiedCase() {
		context = new ClassPathXmlApplicationContext("world-beans.xml");
		CaseIf caseIf = (CaseIf) context
				.getBean(this.getCaseTemplateBeanName());
		this.setProxiedCase(caseIf);
		caseIf.setDimensionSetPoint(this.getDimensionSetPoint());
		caseIf.setCaseId(this.getCaseId());
		caseIf.setLifeCycleState(this.getLifeCycleState());
		caseIf.setExperimentId(this.getExperimentId());
		String threadName = Thread.currentThread().getName();
		caseIf.setThreadName(threadName);
		this.setThreadName(threadName);
		caseIf.getEventSource().getListeners()
				.addAll(this.getEventSource().getListeners());
		this.insertDimensionSetPoint();
		caseIf.init();

	}


	public void run() {

		try {
			ThreadContext.put("pid", LiffUtils.getProcessId());

			ThreadContext.put("case-id", String.valueOf(this.getCaseId()));

			logger.debug("Running case shell with dim {}", this
					.getDimensionSetPoint().toString());
			if (this.proxiedCase == null) {
				this.initProxiedCase();
				logger.debug("Case initialized");
			}

			/*	
			Case.LifeCycleState currentStatus;

			currentStatus = this.getExperimentTracker().getCaseStatus(
					this.getDimensionSetPoint());

			if (currentStatus == Case.LifeCycleState.ENDED) {
				logger.debug("Skipping world run to avoid duplicate results ");
				return;
			}*/
			this.getProxiedCase().run();
			logger.debug("Case run finished");
		} catch (Exception e) {
			logger.error("Error occured during case exectution", e);
		} finally {
			ThreadContext.clearAll();
			this.context = null;

		}
	}

	@Override
	public DimensionSetPoint getDimensionSetPoint() {

		return this.dimensionSetPoint;
	}

	@Override
	public void setDimensionSetPoint(DimensionSetPoint dimensionSetpoint) {
		this.dimensionSetPoint = dimensionSetpoint;
	}

	@Override
	public int getCaseId() {

		return this.caseId;
	}

	@Override
	public void setCaseId(int caseId) {
		this.caseId = caseId;

	}

	@Override
	public LifeCycleState getLifeCycleState() {

		return LifeCycleState.CREATED;
	}

	@Override
	public void setLifeCycleState(LifeCycleState lifeCycleState) {

	}

	@Override
	public long getExperimentId() {

		return this.experimentId;
	}

	@Override
	public void setExperimentId(long experimentId) {
		this.experimentId = experimentId;
	}

	public String getCaseTemplateBeanName() {
		return caseTemplateBeanName;
	}

	public void setCaseTemplateBeanName(String caseTemplateBeanName) {
		this.caseTemplateBeanName = caseTemplateBeanName;
	}

	public CaseIf getProxiedCase() {
		return proxiedCase;
	}

	public void setProxiedCase(CaseIf proxiedCase) {
		this.proxiedCase = proxiedCase;
	}

	public EventSource<LiffEvent> getEventSource() {
		return eventSource;
	}
	/*
	@Override
	public ExperimentTracker getExperimentTracker() {

		return this.experimentTracker;
	}

	@Override
	public void setExperimentTracker(ExperimentTracker experimentTracker) {
		this.experimentTracker = experimentTracker;

	}*/

	@Override
	public void setThreadName(String name) {
		this.threadName = name;

	}

	@Override
	public String getThreadName() {

		return this.threadName;
	}

	@Override
	public void init() {
		this.getProxiedCase().init();

	}

}
