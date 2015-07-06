package org.exreco.experiment.dim;

import java.io.Serializable;

public abstract class DimensionValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4820160515994543931L;

	public abstract Dimension getDimensionType();

	abstract public Object getValue();

	abstract public String getValueAsString();

	// abstract public void setValue(Number value);

	abstract public void setToMin();

	abstract public void setToMax();

	abstract public boolean increase();

	abstract public boolean decrease();

	abstract public boolean isMin();

	abstract public boolean isMax();

	abstract public boolean isNaN();

	@Override
	abstract public DimensionValue clone();

	@Override
	abstract public int hashCode();

	@Override
	abstract public boolean equals(Object obj);

}
