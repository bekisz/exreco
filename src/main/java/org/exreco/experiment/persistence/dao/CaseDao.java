package org.exreco.experiment.persistence.dao;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.exreco.experiment.Case;
import org.exreco.experiment.Case.LifeCycleState;
import org.exreco.experiment.dim.DimensionSetPoint;
import org.exreco.experiment.log.DynamicDimensionSetPointClassHibernateHelper;
import org.exreco.experiment.util.LiffUtils;

@Entity
public class CaseDao {
	/**
	 * 
	 */
	//@Transient
	//private DimensionSetPoint dimensionSetpoint;
	//@Embedded
	//private Object dynamicDimensionSetpoint;
	private String threadName;
	private int isoCaseId2;
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	@Column(nullable = false, unique = true)
	private int caseId2;



	private String hostName;
	private long experimentId;
	/*
	private static DynamicDimensionSetPointClassHibernateHelper dimHelper = null;
	static {
		dimHelper = new DynamicDimensionSetPointClassHibernateHelper("CaseStatusDynamicDimensionSetPoint");
		dimHelper.setEmbeddable(true);
		dimHelper.setIdFieldNeeded(false);
	} */
	/*
	public DimensionSetPoint getDimensionSetpoint() {
		return dimensionSetpoint;
	}

	public void setDimensionSetpoint(DimensionSetPoint dimensionSetpoint) {
		this.dimensionSetpoint = dimensionSetpoint;
	} */
/*
	public Object getDynamicDimensionSetpoint() {
		return dynamicDimensionSetpoint;
	}

	public void setDynamicDimensionSetpoint(Object dynamicDimensionSetpoint) {
		this.dynamicDimensionSetpoint = dynamicDimensionSetpoint;
	}
*/
	/*
	public static DynamicDimensionSetPointClassHibernateHelper getDimHelper() {
		return dimHelper;
	}

	public static void setDimHelper(
			DynamicDimensionSetPointClassHibernateHelper dimHelper) {
		CaseDao.dimHelper = dimHelper;
	}
	*/
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setExperimentId(long experimentId) {
		this.experimentId = experimentId;
	}

	public void setLifeCycleState(LifeCycleState lifeCycleState) {
		this.lifeCycleState = lifeCycleState;
	}


	private LifeCycleState lifeCycleState;

	public CaseDao() {
		
	}

	/**
	 * @return the dimensionSetpoint
	 */
	/*
	public DimensionSetPoint getDimensionSetPoint() {
		return dimensionSetpoint;
	}
	*/
	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
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

	public int getIsoCaseId2() {
		return isoCaseId2;
	}

	public void setIsoCaseId2(int isoCaseId2) {
		this.isoCaseId2 = isoCaseId2;
	}
	public int getCaseId2() {
		return caseId2;
	}

	public void setCaseId2(int caseId2) {
		this.caseId2 = caseId2;
	}
}
