package org.exreco.liff.core;

public abstract class MutatingReplicator extends Replicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2628780559519819042L;
	abstract protected Replicator createReplicator();
	public void mutate() {
		DirectReplicationFitnessGene mutatedGene = this.getGene().mutate();
		this.setGene(mutatedGene);
	}

}
