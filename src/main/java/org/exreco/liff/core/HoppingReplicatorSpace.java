package org.exreco.liff.core;

public class HoppingReplicatorSpace extends Space {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7322742183624073695L;
	private double regionHoppingChance;
	


	@Override
	public void init() {
	
		
	}



	public double getRegionHoppingChance() {
		return regionHoppingChance;
	}



	public void setRegionHoppingChance(double regionHoppingChance) {
		this.regionHoppingChance = regionHoppingChance;
	}
}
