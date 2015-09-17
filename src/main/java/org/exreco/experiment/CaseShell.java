package org.exreco.experiment;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.jmx.LoggerContextAdmin;
import org.apache.logging.log4j.core.selector.BasicContextSelector;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.exreco.experiment.Case.LifeCycleState;
import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.dim.DimensionValue;
import org.exreco.experiment.dim.ListedSpringReferenceDimension;
import org.exreco.experiment.jms.JmsEventTopicHome;
import org.exreco.experiment.log.DisributedLoggingUtils;
import org.exreco.experiment.util.LiffUtils;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.EventTopicHome;
import org.exreco.experiment.util.events.EventTopicHomeBase;
import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.log.DistributedAppender;
import org.exreco.log.ExrecoContextSelector;
import org.exreco.log.ExrecoLog4jContextFactory;
import org.jppf.classloader.JPPFClassLoader;
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
	private String worldBeansXml;

	private String threadName;
	private int caseId;
	private CaseIf proxiedCase = null;
	private ApplicationContext context;
	private final EventSource eventSource = new EventSource();
	private EventTopicHome evenTopicHome;
	// private ExperimentTracker experimentTracker;
	private long experimentId;

	private String log4j2ConfigLocation = null;
	// private static Boolean isDistributedLoggingInited = new Boolean(false);

	private static Logger logger = LogManager.getLogger(CaseShell.class.getName());

	public CaseShell(String worldBeansXml, String caseTemplateBeanName) {
		super();
		this.caseTemplateBeanName = caseTemplateBeanName;
		this.worldBeansXml = worldBeansXml;
	}

	protected void insertDimensionSetPoint() {
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(this.getProxiedCase());
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


	private void initProxiedCase() throws URISyntaxException {

		DistributedAppender.inject2LoggerContext(this.getLog4j2ConfigLocation(), this.getEvenTopicHome());

		context = new ClassPathXmlApplicationContext(this.getWorldBeansXml());
		CaseIf caseIf = (CaseIf) context.getBean(this.getCaseTemplateBeanName());

		this.setProxiedCase(caseIf);
		caseIf.setDimensionSetPoint(this.getDimensionSetPoint());
		caseIf.setCaseId(this.getCaseId());
		caseIf.setLifeCycleState(this.getLifeCycleState());
		caseIf.setExperimentId(this.getExperimentId());
		String threadName = Thread.currentThread().getName();
		caseIf.setThreadName(threadName);
		this.setThreadName(threadName);
		caseIf.getEventSource().wireTo(this.getEventSource().getListeners());
		// TODO Maybe it is needed :
		// this.getEventSource().getListeners().clear();
		this.insertDimensionSetPoint();
		caseIf.init();
		logger.debug("Proxied Case initialized from file : {} ", this.getWorldBeansXml());

	}

	public void run() {

		try {
			ThreadContext.put("pid", LiffUtils.getProcessId());

			ThreadContext.put("case-id", String.valueOf(this.getCaseId()));

			logger.debug("Running case shell with dim {}", this.getDimensionSetPoint().toString());
			if (this.proxiedCase == null) {
				this.initProxiedCase();
				logger.debug("Case initialized");
			}

			/*
			 * Case.LifeCycleState currentStatus;
			 * 
			 * currentStatus = this.getExperimentTracker().getCaseStatus(
			 * this.getDimensionSetPoint());
			 * 
			 * if (currentStatus == Case.LifeCycleState.ENDED) { logger.debug(
			 * "Skipping world run to avoid duplicate results "); return; }
			 */
			this.getProxiedCase().run();
			logger.debug("Case run finished");
		} catch (Throwable e) {

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

	public String getWorldBeansXml() {
		return worldBeansXml;
	}

	public CaseIf getProxiedCase() {
		return proxiedCase;
	}

	public void setProxiedCase(CaseIf proxiedCase) {
		this.proxiedCase = proxiedCase;
	}

	public EventSource getEventSource() {
		return eventSource;
	}
	/*
	 * @Override public ExperimentTracker getExperimentTracker() {
	 * 
	 * return this.experimentTracker; }
	 * 
	 * @Override public void setExperimentTracker(ExperimentTracker
	 * experimentTracker) { this.experimentTracker = experimentTracker;
	 * 
	 * }
	 */

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

	@Override
	public void setLog4j2ConfigLocation(String log4j2ConfigLocation) {
		this.log4j2ConfigLocation = log4j2ConfigLocation;
	}

	public String getLog4j2ConfigLocation() {
		return log4j2ConfigLocation;
	}

	public EventTopicHome getEvenTopicHome() {
		return evenTopicHome;
	}

	public void setEvenTopicHome(EventTopicHome evenTopicHome) {
		this.evenTopicHome = evenTopicHome;
	}

}
