package org.exreco.liff.experiments.delta;



public class WanderingDeltaGene extends DeltaGene {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1472941106733759474L;
	
	
	public WanderingDeltaGene() {
		super();
	
	}
	/**
	 * 
	 */
	public WanderingDeltaGene(WanderingDeltaGene hoppingDeltaGene) {
		super(hoppingDeltaGene);
	}
	@Override
	public WanderingDeltaGene createGene() {

		return new WanderingDeltaGene(this);
	}

	@Override
	public WanderingReplicator createReplicator() {
		return new WanderingReplicator(this);
	}
}
