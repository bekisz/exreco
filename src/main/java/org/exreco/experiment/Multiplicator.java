package org.exreco.experiment;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.dim.DimensionSet;
import org.exreco.experiment.dim.DimensionSetPoint;


public class Multiplicator implements Iterator<CaseShellIf> {
	private DimensionSet dimensionSet;

	private DimensionSetPoint actualDimensionSetPoint;
	private int actualCaseId = 0;
	private static Logger logger = LogManager.getLogger(Multiplicator.class.getName());
	public void init() {
		this.setActualDimensionSetPoint(dimensionSet.createMinDimensionSetPoint());
	}
	public DimensionSet getDimensionSet() {
		return dimensionSet;
	}

	public void setDimensionSet(DimensionSet dimensionSet) {
		this.dimensionSet = dimensionSet;
	}


	

	@Override
	public boolean hasNext() {
		
		return !this.getActualDimensionSetPoint().isMax();
	}

	
	@Override
	synchronized public CaseShellIf next() {
		
		boolean couldIncrease = this.getActualDimensionSetPoint().increase();
		
		if (!couldIncrease ) {
			logger.warn("No next case available");
			return null;
		}
		CaseShellIf caseIf = new CaseShell("world");
		DimensionSetPoint clonedPoint = this.getActualDimensionSetPoint().clone();
		caseIf.setDimensionSetPoint(clonedPoint);
		caseIf.setCaseId(this.getActualCaseId());
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
}
