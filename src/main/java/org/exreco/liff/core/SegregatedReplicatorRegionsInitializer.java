package org.exreco.liff.core;

import java.io.Serializable;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.Case;
import org.exreco.liff.experiments.delta.DeltaGene;


public class SegregatedReplicatorRegionsInitializer   extends World.BaseInitializer  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7144602378120242781L;
	private static Logger logger = LogManager.getLogger(SegregatedReplicatorRegionsInitializer.class
			.getName());
	public void init(Case theCase) {
		super.init(theCase);
		World world = (World) theCase;
		world.setExitChecker( world.new FiftyFiftyExitChecker());
		//logger.debug("World init'ed");

		
	}
	public void initReplicators() {

		DirectReplicationFitnessGene gene1_1 = this.getWorld().lookupGene("1.1");

		for (int i = 0; i < this.getWorld().getSpace().getMaxPopulation() / 2; i++) {
			RegionalReplicator replicator = (RegionalReplicator) gene1_1.createReplicator();
			replicator.init();

			Random rand = this.getWorld().getRandom();

			double x = rand.nextDouble() / 2;
			double y = rand.nextDouble();

			replicator.moveTo(x, y);
			// rand.nextInt(Replicator.this.getGene().get)

			Region region = replicator.getRegion();

			region.getReplicators().add(replicator);
		}
		DirectReplicationFitnessGene gene1_2 = this.getWorld().lookupGene("1.2");

		for (int i = 0; i < this.getWorld().getSpace().getMaxPopulation() / 2; i++) {
			RegionalReplicator replicator = (RegionalReplicator) gene1_2.createReplicator();
			replicator.init();

			Random rand = this.getWorld().getRandom();
			DeltaGene deltaGene1_2 = (DeltaGene) gene1_2;
			int maxAge = deltaGene1_2.getMaxLifeTime();
			replicator.setAge(rand.nextInt(maxAge));
			
			double x = rand.nextDouble() / 2 + 0.5;
			double y = rand.nextDouble();
			replicator.moveTo(x, y);
			

			Region region = replicator.getRegion();

			region.getReplicators().add(replicator);
		}

	}

}
