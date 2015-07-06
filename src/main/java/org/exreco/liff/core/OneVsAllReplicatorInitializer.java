package org.exreco.liff.core;

import java.io.Serializable;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.Case;
import org.exreco.liff.core.World.BaseInitializer;
import org.exreco.liff.core.World.FiftyFiftyExitChecker;
import org.exreco.liff.core.World.OneDotTwoExistExitChecker;

public class OneVsAllReplicatorInitializer  extends World.BaseInitializer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4909770169142459221L;
	private static Logger logger = LogManager.getLogger(OneVsAllReplicatorInitializer.class
			.getName());
	public void init(Case theCase) {
		super.init(theCase);
		World world = (World) theCase;
		world.setExitChecker( world.new OneDotTwoExistExitChecker());
		//logger.debug("World init'ed");
		
	}
	public void initReplicators() {

		DirectReplicationFitnessGene gene1_1 = this.getWorld().lookupGene("1.1");

		for (int i = 0; i < this.getWorld().getSpace().getMaxPopulation() - 1; i++) {
			RegionalReplicator replicator = (RegionalReplicator) gene1_1.createReplicator();
			replicator.init();

			Random rand = this.getWorld().getRandom();

			double x = rand.nextDouble();
			double y = rand.nextDouble();

			replicator.moveTo(x, y);
			// rand.nextInt(Replicator.this.getGene().get)

			Region region = replicator.getRegion();

			region.getReplicators().add(replicator);
		}
		DirectReplicationFitnessGene gene1_2 = this.getWorld().lookupGene("1.2");

		RegionalReplicator replicator = (RegionalReplicator)gene1_2.createReplicator();
		replicator.init();

		Random rand = this.getWorld().getRandom();

		double x = rand.nextDouble();
		double y = rand.nextDouble();
		replicator.moveTo(x, y);


		Region region = replicator.getRegion();

		region.getReplicators().add(replicator);
		/*
		DirectReplicationFitnessGene gene1_3 = this.getWorld().lookupGene("1.3");

		replicator = (RegionalReplicator) gene1_3.createReplicator();
		replicator.init();
		x = rand.nextDouble();
		y = rand.nextDouble();
		replicator.moveTo(x, y);

		region = replicator.getRegion();

		region.getReplicators().add(replicator); */

	}


}



