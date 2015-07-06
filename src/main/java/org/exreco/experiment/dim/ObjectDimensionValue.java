package org.exreco.experiment.dim;

import java.io.Serializable;

public class ObjectDimensionValue extends DimensionValue implements
		Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 7116473503285009013L;

	private final ObjectDimension dimensionType;

	private int valueId;

	public ObjectDimensionValue(ObjectDimension dimensionType) {
		super();
		this.dimensionType = dimensionType;
		this.valueId = 0;
	}

	@Override
	public ObjectDimension getDimensionType() {
		return this.dimensionType;
	}

	@Override
	public Object getValue() {
		return this.getDimensionType().getPossibleValues().get(this.valueId);
	}

	@Override
	public String getValueAsString() {
		return this.getValue().toString();
	}

	@Override
	public void setToMin() {
		this.valueId = 0;

	}

	@Override
	public void setToMax() {
		this.valueId = this.getDimensionType().getPossibleValues().size() -1;

	}

	@Override
	public boolean increase() {
		if (this.isMax()) {
			return false;
		}
		this.valueId++;
		return true;
	}

	@Override
	public boolean decrease() {
		if (this.isMin()) {
			return false;
		}
		this.valueId--;
		return true;
	}

	/**
	 * Returns true if the current number is lowest possible value in the range
	 * with the increment
	 */
	@Override
	public boolean isMin() {
		return this.valueId == 0;
	}

	@Override
	/**
	 * Returns true if the current number is biggest possible value in the range with the increment
	 */
	public boolean isMax() {
		return this.valueId == this.getDimensionType().getPossibleValues().size() -1;

	}

	@Override
	public boolean isNaN() {
		return true;
	}

	@Override
	public ObjectDimensionValue clone() {
		ObjectDimensionValue dimVal = new ObjectDimensionValue(
				this.getDimensionType());
		dimVal.setValueId(this.valueId);
		return dimVal;
	}

	@Override
	public int hashCode() {

		return this.getValue().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObjectDimensionValue) {
			ObjectDimensionValue otherIntegerDimensionValue = (ObjectDimensionValue) obj;

			if (this.getValueId() == otherIntegerDimensionValue
					.getValueId()) {
				return true;
			}

		}
		return false;
	}

	public int getValueId() {
		return valueId;
	}

	public void setValueId(int valueId) {
		this.valueId = valueId;
	}

}
