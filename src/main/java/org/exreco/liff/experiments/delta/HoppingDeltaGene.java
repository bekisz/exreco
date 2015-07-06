package org.exreco.liff.experiments.delta;



public class HoppingDeltaGene extends DeltaGene {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1838622387823463124L;

	public HoppingDeltaGene() {
		super();
	
	}
	public HoppingDeltaGene(HoppingDeltaGene hoppingDeltaGene) {
		super(hoppingDeltaGene);
	}
	@Override
	public HoppingDeltaGene createGene() {

		return new HoppingDeltaGene(this);
	}
	@Override
	public HoppingReplicator createReplicator() {
		return new HoppingReplicator(this);
	}
	
}
