package org.exreco.experiment.dim;

import java.io.Serializable;

public class IncrementedIntegerDimensionValue extends DimensionValue implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1404985854897939695L;

	private final IncrementedIntegerDimension dimensionType;

	private int value;

	public IncrementedIntegerDimensionValue(IncrementedIntegerDimension dimensionType) {
		super();
		this.dimensionType = dimensionType;
		this.value = this.getDimensionType().getMin();
	}

	@Override
	public IncrementedIntegerDimension getDimensionType() {
		return this.dimensionType;
	}

	@Override
	public Integer getValue() {
		return this.value;
	}

	@Override
	public String getValueAsString() {
		return Integer.toString(this.getValue());
	}

	@Override
	public void setToMin() {
		this.value = this.getDimensionType().getMin();

	}

	@Override
	public void setToMax() {
		this.value = this.getDimensionType().getMax();

	}

	@Override
	public boolean increase() {
		if (this.isMax()) {
			return false;
		}
		this.value += this.getDimensionType().getIncrement();
		return true;
	}

	@Override
	public boolean decrease() {
		if (this.isMin()) {
			return false;
		}
		this.value -= this.getDimensionType().getIncrement();
		return true;
	}

	/**
	 * Returns true if the current number is lowest possible value in the range
	 * with the increment
	 */
	@Override
	public boolean isMin() {
		return (this.value - this.getDimensionType().getIncrement() < this
				.getDimensionType().getMin().intValue());
	}

	@Override
	/**
	 * Returns true if the current number is biggest possible value in the range with the increment
	 */
	public boolean isMax() {
		return (this.value + this.getDimensionType().getIncrement() > this
				.getDimensionType().getMax().intValue());

	}

	@Override
	public boolean isNaN() {
		return (this.value == Integer.MIN_VALUE);
	}

	@Override
	public IncrementedIntegerDimensionValue clone() {
		IncrementedIntegerDimensionValue dimVal = new IncrementedIntegerDimensionValue(
				this.getDimensionType());
		dimVal.value = this.value;
		return dimVal;
	}

	@Override
	public int hashCode() {

		return this.getValue().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IncrementedIntegerDimensionValue) {
			IncrementedIntegerDimensionValue otherIntegerDimensionValue = (IncrementedIntegerDimensionValue) obj;

			if (this.getValue().intValue() == otherIntegerDimensionValue
					.getValue().intValue()) {
				return true;
			}

		}
		return false;
	}
}
