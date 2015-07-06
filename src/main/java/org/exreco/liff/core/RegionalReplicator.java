package org.exreco.liff.core;

import org.exreco.liff.experiments.delta.DeltaGene;

public abstract class RegionalReplicator extends Replicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1258063834612694559L;
	public RegionalReplicator(DeltaGene gene) {
		super(gene);
	}

	public RegionalReplicator(RegionalReplicator ancestor) {
		super(ancestor);
	}

	/**
	 * @return the region
	 */
	abstract public Region getRegion();
	/**
	 * @return the x
	 */
	abstract public double getX();


	abstract public void moveTo(double x, double y);

	/**
	 * @return the y
	 */
	abstract public double getY() ;

}
