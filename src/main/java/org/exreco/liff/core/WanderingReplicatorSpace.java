package org.exreco.liff.core;

public class WanderingReplicatorSpace extends Space {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7322742183624073695L;
	private double wandering;
	
	/**
	 * @return the wandering
	 */
	public double getWandering() {
		return wandering;
	}

	/**
	 * @param wandering
	 *            the wandering to set
	 */
	public void setWandering(double wandering) {
		this.wandering = wandering;
	}

	@Override
	public void init() {
		this.setWandering(this.getWandering() * this.getMaxX());
		
	}
}
