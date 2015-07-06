package org.exreco.experiment.dim;

import java.io.Serializable;


public class IncrementedDoubleDimension extends Dimension implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2491988329958950061L;
	private Double min = 1.0;
	private Double max;
	private Double increment = 1.0;

	// private Double actualValue = Double.NaN;
	/*
	@Override
	public void init(Node dimensionNode) {
		super.init(dimensionNode);
		NamedNodeMap attributes = dimensionNode.getAttributes();

		this.max = Double
				.valueOf(attributes.getNamedItem("max").getNodeValue());

		// min is not mandatory attribute. The default value = 1.0
		Node minNode = attributes.getNamedItem("min");

		if (minNode != null) {
			this.min = Double.valueOf(attributes.getNamedItem("min")
					.getNodeValue());
		}

		// increment is not mandatory attribute. The default value = 1.0
		Node incrementNode = attributes.getNamedItem("increment");

		if (incrementNode != null) {
			this.increment = Double.valueOf(attributes
					.getNamedItem("increment").getNodeValue());
		}

	}*/

	@Override
	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public void setIncrement(Double increment) {
		this.increment = increment;
	}

	@Override
	public Double getMax() {
		return max;
	}

	@Override
	public Double getIncrement() {
		return increment;
	}

	@Override
	public int totalIncrements() {
		int result = (int) (Math.floor((this.max - this.min) / this.increment) + 1);
		return result;
	}

	@Override
	public IncrementedDoubleDimensionValue createDimensionMinValue() {

		return new IncrementedDoubleDimensionValue(this);
	}
}
