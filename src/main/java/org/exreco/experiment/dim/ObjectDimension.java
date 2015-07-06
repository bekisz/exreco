package org.exreco.experiment.dim;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class ObjectDimension extends Dimension implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3562449458015815332L;
	private List<Object> possibleValues = new LinkedList<Object>();
	




	
	@Override
	public Object getMin() {
		return this.getPossibleValues().get(0);
	}

	@Override
	public Object getMax() {
		return this.getPossibleValues().get(this.getPossibleValues().size()-1);
	}


	@Override
	public int totalIncrements() {
		int result = this.getPossibleValues().size();
		return result;
	}

	
	@Override
	public DimensionValue createDimensionMinValue() {
		return new ObjectDimensionValue(this);
	}

	public List<Object> getPossibleValues() {
		return possibleValues;
	}
	public void setPossibleValues(List<Object> possibleValues) {
		this.possibleValues = possibleValues;
	}


}
