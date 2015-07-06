package org.exreco.experiment.dim;

import java.io.Serializable;


public abstract class Dimension implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9017371733847139241L;
	private String name;


	public void setName(String name) {
		this.name = name;
	}

	public abstract Object getMin();

	public abstract Object getMax();

	public  Number getIncrement() {
		return 1;
	}

	public abstract DimensionValue createDimensionMinValue();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(50);
		sb.append("\nDimension\n name : ").append(this.getName());
		sb.append("\n min : ").append(this.getMin());
		sb.append("\n max : ").append(this.getMax());
		sb.append("\n increment : ").append(this.getIncrement());
		// sb.append("\n actual value : ").append(this.getActualValueAsString());
		sb.append("\n");
		return sb.toString();

	}

	abstract public int totalIncrements();

}
