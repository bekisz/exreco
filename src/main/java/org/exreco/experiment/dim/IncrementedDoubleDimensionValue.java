package org.exreco.experiment.dim;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class IncrementedDoubleDimensionValue extends DimensionValue implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1989266812733841369L;

	private final IncrementedDoubleDimension dimensionType;
	// private double value;

	private int step = 0;

	public IncrementedDoubleDimensionValue(IncrementedDoubleDimension dimensionType) {
		super();
		this.dimensionType = dimensionType;
		// this.step = 0;
		// this.value = value;
	}

	@Override
	public IncrementedDoubleDimension getDimensionType() {
		return this.dimensionType;
	}

	@Override
	public void setToMin() {
		this.step = 0;

	}

	@Override
	public void setToMax() {
		this.step = this.getDimensionType().totalIncrements() - 1;

	}

	@Override
	public boolean increase() {
		if (this.isMax()) {
			return false;
		}
		this.step++;
		return true;
	}

	@Override
	public boolean decrease() {
		if (this.isMin()) {
			return false;
		}
		this.step--;
		return true;
	}

	/**
	 * Returns true if the current number is lowest possible value in the range
	 * with the increment
	 */
	@Override
	public boolean isMin() {
		return (this.step == 0);
	}

	@Override
	/**
	 * Returns true if the current number is biggest possible value in the range with the increment
	 */
	public boolean isMax() {
		return (this.getValue() + this.getDimensionType().getIncrement() > this
				.getDimensionType().getMax().doubleValue());

	}

	@Override
	public boolean isNaN() {
		return (false);
	}

	@Override
	public Double getValue() {
		return this.getDimensionType().getMin()
				+ this.getDimensionType().getIncrement() * this.step;
	}

	@Override
	public String getValueAsString() {
		Locale loc = Locale.US;
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###.########");

		double value = this.getValue();

		String result = df.format(value);
		return result;
	}

	@Override
	public IncrementedDoubleDimensionValue clone() {
		IncrementedDoubleDimensionValue dimVal = new IncrementedDoubleDimensionValue(
				this.getDimensionType());
		dimVal.step = this.step;
		return dimVal;
	}

	@Override
	public int hashCode() {

		return this.getValue().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IncrementedDoubleDimensionValue) {
			IncrementedDoubleDimensionValue otherDoubleValue = (IncrementedDoubleDimensionValue) obj;

			if (this.getValueAsString().equalsIgnoreCase(
					otherDoubleValue.getValueAsString())) {
				return true;
			}

		}
		return false;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @param step
	 *            the step to set
	 */
	protected void setStep(int step) {
		this.step = step;
	}
}
