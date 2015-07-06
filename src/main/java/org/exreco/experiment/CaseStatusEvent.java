package org.exreco.experiment;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.exreco.experiment.Case.LifeCycleState;
import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.log.DynamicDimensionSetPointClassHibernateHelper;
import org.exreco.experiment.persistence.DynamicClass;
import org.exreco.experiment.util.LiffUtils;
import org.exreco.experiment.util.events.LiffEvent;

@Entity(name="casestatus")
public class CaseStatusEvent extends LiffEvent implements
		Serializable {
	private static final long serialVersionUID = -8879575849443253516L;

	/**
	 * 
	 */
	@Transient
	final private DimensionSetPoint dimensionSetpoint;
	//@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	//private DynamicClass dynamicDimensionSetpoint;
	final private String threadName;
	final private int isoCaseId;
	@Id
	//@GeneratedValue(strategy =GenerationType.AUTO)
	@Column(nullable = false, unique = true)
	final private int caseId;
	final private String hostName;
	final private long experimentId;

	private final LifeCycleState lifeCycleState;

	public CaseStatusEvent(Case sourceCase) {
		this.dimensionSetpoint = sourceCase.getDimensionSetPoint();
	
		this.threadName = sourceCase.getThreadName();
		this.isoCaseId = sourceCase.getIsoCaseId();
		this.caseId = sourceCase.getCaseId();
		this.hostName = LiffUtils.getHostName();
		this.lifeCycleState = sourceCase.getLifeCycleState();
		this.experimentId = sourceCase.getExperimentId();
	}

	/**
	 * @return the dimensionSetpoint
	 */
	public DimensionSetPoint getDimensionSetPoint() {
		return dimensionSetpoint;
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * @return the isoCaseId
	 */
	public int getIsoCaseId() {
		return isoCaseId;
	}

	/**
	 * @return the caseId
	 */
	public int getCaseId() {
		return caseId;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @return the lifeCycleState
	 */
	public LifeCycleState getLifeCycleState() {
		return lifeCycleState;
	}

	/**
	 * @return the experimentId
	 */
	public long getExperimentId() {
		return experimentId;
	}

}