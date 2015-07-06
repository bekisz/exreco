package org.exreco.experiment.dim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class DimensionSetPoint extends ArrayList<DimensionValue> implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4543614294054608792L;
	private final DimensionSet dimensionSet;



	public DimensionSetPoint(DimensionSet dimensionSet) {
		super(dimensionSet.getDimensionMap().size());
		// TODO Clone
		this.dimensionSet = dimensionSet;

		this.reset();
	}



	public DimensionSetPoint(DimensionSetPoint dimensionSetPoint) {
		super(dimensionSetPoint);
		this.dimensionSet = dimensionSetPoint.getDimensionSet();
	}

	protected void reset() {
		this.clear();
		for (Dimension dim : dimensionSet.getDimensionMap().values()) {
			this.add(dim.createDimensionMinValue());
		}

	}

	public DimensionSet getDimensionSet() {
		return dimensionSet;
	}

	public DimensionValue[] getValueArray() {
		DimensionValue[] dimensions = this.toArray(new DimensionValue[this
				.size()]);
		return dimensions;
	}

	/**
	 * 
	 * @return true if increase was successful
	 */
	public boolean increase() {
		for (int i = this.size() - 1; i >= 0; i--) {
			if (this.get(i).increase()) {
				return true;
			} else {
				this.get(i).setToMin();
			}
		}
		return false;
	}

	/**
	 * 
	 * @return true if increase was successful
	 */
	public boolean decrease() {
		for (int i = this.size() - 1; i >= 0; i--) {
			if (this.get(i).decrease()) {
				return true;
			} else {
				this.get(i).setToMax();
			}
		}
		return false;
	}

	public boolean isMax() {
		for (int i = this.size() - 1; i >= 0; i--) {
			if (!this.get(i).isMax()) {
				return false;
			}
		}
		return true;
	}

	public boolean isMin() {
		for (int i = this.size() - 1; i >= 0; i--) {
			if (!this.get(i).isMin()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public DimensionSetPoint clone() {
		DimensionSetPoint point = new DimensionSetPoint(this.getDimensionSet());
		point.clear();
		for (DimensionValue dimVal : this) {
			point.add(dimVal.clone());
		}
		return point;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer(60);
		strBuffer.append('[');

		for (DimensionValue value : this) {
			strBuffer.append(value.getDimensionType().getName());
			strBuffer.append('=');
			strBuffer.append(value.getValueAsString());
			strBuffer.append(" ");
		}
		strBuffer.append(']');
		return strBuffer.toString();

	}

	@Override
	public int hashCode() {
		int hashC = 1;
		for (DimensionValue value : this) {
			hashC ^= value.hashCode();
		}
		return hashC;
	}

	@Override
	public boolean equals(Object obj) {
		DimensionSetPoint otherPoint = (DimensionSetPoint) obj;
		Iterator<DimensionValue> it = this.iterator();
		Iterator<DimensionValue> ito = otherPoint.iterator();

		while (it.hasNext()) {
			if (ito.hasNext()) {
				DimensionValue dimVal = it.next();
				DimensionValue dimValOther = ito.next();

				if (!dimVal.equals(dimValOther)) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
