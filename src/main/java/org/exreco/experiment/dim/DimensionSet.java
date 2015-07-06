package org.exreco.experiment.dim;

import java.io.Serializable;

import java.util.Map;


public class DimensionSet  implements
		Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1356129163786836495L;
	

	
	private final Map<String, Dimension> dimensionMap;
	/** all possible states within the dimensions */
	private int permutations;
	
	
	public DimensionSet(Map<String, Dimension> dimensionMap) {
		super();
		this.dimensionMap = dimensionMap;
		this.calculatePermutation();
		this.fillInDimensionNames();
	}


	private void fillInDimensionNames() {
		for (String key : this.getDimensionMap().keySet()) {
				String name = this.getDimensionMap().get(key).getName();
				if ( name == null || "".equals(name)) {
					this.getDimensionMap().get(key).setName(key);
				}
		
		}	
		
	}

	/**
	 * Returns the number of permutations wthin all values of all dimensions
	 * 
	 * @return
	 */
	public void calculatePermutation() {
		int cases = 1;
		for (Dimension dim : this.getDimensionMap().values()) {
			cases *= dim.totalIncrements();
		}
		this.permutations = cases;

	}

	/**
	 * @return the permutations
	 */
	public int getPermutations() {
		return permutations;
	}

	public DimensionSetPoint createMinDimensionSetPoint() {
		return new DimensionSetPoint(this);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(100);
		sb.append("[");
		for (Dimension dim : this.getDimensionMap().values()) {
			sb.append(dim.getName());
			sb.append(":");
			sb.append(dim.getMin());
			sb.append("...");
			sb.append(dim.getMax());
			sb.append("/inc:");
			sb.append(dim.getIncrement());
			sb.append("");
			sb.append("; ");
		}
		sb.append("]");
		return sb.toString();

	}

	public Map<String, Dimension> getDimensionMap() {
		return dimensionMap;
	}

}
