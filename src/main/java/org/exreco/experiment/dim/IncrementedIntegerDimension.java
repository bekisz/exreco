package org.exreco.experiment.dim;

import java.io.Serializable;


public class IncrementedIntegerDimension extends Dimension implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3561645733462716132L;
	private Integer min = 1;
	public void setMin(Integer min) {
		this.min = min;
	}

	public void setIncrement(Integer increment) {
		this.increment = increment;
	}

	private Integer max;
	private Integer increment = 1;



	@Override
	public Integer getMin() {
		return min;
	}

	@Override
	public Integer getMax() {
		return max;
	}

	
	@Override
	public Integer getIncrement() {
		return increment;
	}

	@Override
	public int totalIncrements() {
		int result = (int) (Math.floor((this.max - this.min) / this.increment) + 1);
		return result;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	@Override
	public DimensionValue createDimensionMinValue() {
		return new IncrementedIntegerDimensionValue(this);
	}
}
