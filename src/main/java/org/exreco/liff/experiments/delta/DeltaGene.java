package org.exreco.liff.experiments.delta;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.CaseShell;
import org.exreco.liff.core.DirectReplicationFitnessGene;
import org.exreco.liff.core.Replicator;
import org.exreco.liff.core.World;



public abstract class DeltaGene extends DirectReplicationFitnessGene {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2828029467750149139L;

	private static Logger logger = LogManager.getLogger(DeltaGene.class
			.getName());



	/** In case of mutation : The chance that the replication is successful */
	private double chanceOfSuccessfulMutation;

	/** The factor that sucessfully mutated replicator outperforms the old ones */

	private double replicatorSuccessfulMutationAdvantage;
	/** Chance that mutation occurs at any tick */
	private double mutationRate;
	/**
	 * In case of mutation : The chance that the replication gets completely
	 * sterile after a mutation
	 */
	private double chanceOfSterilisation;
	/**
	 * In case of mutation : The chance that the replication gets completely
	 * dead after a mutation
	 */
	private double chanceOfDeathAfterMutation;

	/**
	 * @return the chanceOfSuccessfulMutation
	 */
	public double getChanceOfSuccessfulMutation() {
		return chanceOfSuccessfulMutation;
	}

	/**
	 * @param chanceOfSuccessfulMutation
	 *            the chanceOfSuccessfulMutation to set
	 */
	public void setChanceOfSuccessfulMutation(double chanceOfSuccessfulMutation) {
		this.chanceOfSuccessfulMutation = chanceOfSuccessfulMutation;
		if ( this.getWorld() != null && this.getAge() != 0 ) {
			logger.error("DirectReplicationFitnessGene proporty set after later than its first tick");
		}
		// Recursively sets all subgenes to same value !!
		// This part of the code  should run only at initialization of world
		
		if ( this.getSubGenes() != null ) {
			for( DirectReplicationFitnessGene gene : this.getSubGenes()) {
				DeltaGene deltaGene = (DeltaGene) gene;
				deltaGene.setChanceOfSuccessfulMutation(chanceOfSuccessfulMutation);
			}
		}
	}

	/**
	 * @return the chanceOfDeathAfterMutation
	 */
	public double getChanceOfDeathAfterMutation() {
		return chanceOfDeathAfterMutation;
	}

	/**
	 * @param chanceOfDeathAfterMutation
	 *            the chanceOfDeathAfterMutation to set
	 */
	public void setChanceOfDeathAfterMutation(double chanceOfDeathAfterMutation) {
		this.chanceOfDeathAfterMutation = chanceOfDeathAfterMutation;
		// Recursively sets all subgenes to same value !!
		// This part of the code  should run only at initialization of world
		if ( this.getWorld() != null && this.getAge() != 0 ) {
			logger.error("DirectReplicationFitnessGene proporty set after later than its first tick");
		}
		if ( this.getSubGenes() != null ) {
			for( DirectReplicationFitnessGene gene : this.getSubGenes()) {
				DeltaGene deltaGene = (DeltaGene) gene;
				deltaGene.setChanceOfDeathAfterMutation(chanceOfDeathAfterMutation);
			}
		}
	}



	/** The maximum lifetime for its replicator */
	private int maxLifeTime;

	public DeltaGene() {

	}

	public DeltaGene(DeltaGene superGene) {
		// super(superGene);
		this.init(superGene);

	}

	public void init(DeltaGene superGene) {
		super.init(superGene);
		this.chanceOfSuccessfulMutation = superGene
				.getChanceOfSuccessfulMutation();


		this.mutationRate = superGene.getMutationRate();
		this.replicatorSuccessfulMutationAdvantage = superGene
				.getReplicatorSuccessfulMutationAdvantage();
		this.maxLifeTime = superGene.getMaxLifeTime();
		this.chanceOfSterilisation = superGene.getChanceOfSterilisation();
		this.chanceOfDeathAfterMutation = superGene
				.getChanceOfDeathAfterMutation();
	}

	public DeltaGene(World world) {
		super(world);
	}



	@Override
	abstract public DeltaGene createGene();

	@Override
	public DeltaGene mutate() {
		DeltaGene mutant = this.createGene();
		double mutantReplicationFitness = 0;
		double rnd = this.getWorld().getRandom().nextDouble();
		double chanceSum = 0;
		if (rnd <= (chanceSum += this.getChanceOfSuccessfulMutation())) {
			mutantReplicationFitness = this.getReplicationFitness()
					* this.getReplicatorSuccessfulMutationAdvantage();
			mutant.setReplicationFitness(mutantReplicationFitness);
			return mutant;
		} else if (rnd <= (chanceSum += this.getChanceOfSterilisation())) {
			mutant.setReplicationFitness(0);
			return mutant;
		} else if (rnd <= (chanceSum += this.getChanceOfDeathAfterMutation())) {
			mutant.setSurvivalChance(0);
			return mutant;
		}

		return mutant;
	}





	public double getReplicatorSuccessfulMutationAdvantage() {
		return replicatorSuccessfulMutationAdvantage;
	}

	public void setReplicatorSuccessfulMutationAdvantage(
			double replicatorSuccessfulMutationAdvantage) {
		if ( this.getWorld() != null && this.getAge() != 0 ) {
			logger.error("DirectReplicationFitnessGene proporty set after later than its first tick");
		}
		this.replicatorSuccessfulMutationAdvantage = replicatorSuccessfulMutationAdvantage;

		// Recursively sets all subgenes to same value !!
		// This part of the code  should run only at initialization of world
		
		if ( this.getSubGenes() != null ) {
			for( DirectReplicationFitnessGene gene : this.getSubGenes()) {
				DeltaGene deltaGene = (DeltaGene) gene;
				deltaGene.setReplicatorSuccessfulMutationAdvantage(replicatorSuccessfulMutationAdvantage);
			}
		}
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		if ( this.getWorld() != null && this.getAge() != 0 ) {
			logger.error("DirectReplicationFitnessGene proporty set after later than its first tick");
		}
		this.mutationRate = mutationRate;
		
		// Recursively sets all subgenes to same value !!
		// This part of the code  should run only at initialization of world
		
		if ( this.getSubGenes() != null ) {
			for( DirectReplicationFitnessGene gene : this.getSubGenes()) {
				DeltaGene deltaGene = (DeltaGene) gene;
				deltaGene.setMutationRate(mutationRate);
			}
		}
	}




	/**
	 * @return the maxLifeTime
	 */
	public int getMaxLifeTime() {
		return maxLifeTime;
	
	}

	/**
	 * @param maxLifeTime
	 *            the maxLifeTime to set
	 */
	public void setMaxLifeTime(int maxLifeTime) {
		if ( this.getWorld() != null && this.getAge() != 0 ) {
			logger.error("DirectReplicationFitnessGene proporty set after later than its first tick");
		}
		this.maxLifeTime = maxLifeTime;
		// Recursively sets all subgenes to same value !!
		// This part of the code  should run only at initialization of world
		
		if ( this.getSubGenes() != null ) {
			for( DirectReplicationFitnessGene gene : this.getSubGenes()) {
				DeltaGene deltaGene = (DeltaGene) gene;
				deltaGene.setMaxLifeTime(maxLifeTime);
			}
		}
	}

	/**
	 * @return the chanceOfSterilisation
	 */
	public double getChanceOfSterilisation() {
		return chanceOfSterilisation;
	}

	/**
	 * @param chanceOfSterilisation
	 *            the chanceOfSterilisation to set
	 */
	public void setChanceOfSterilisation(double chanceOfSterilisation) {
		if ( this.getWorld() != null && this.getAge() != 0 ) {
			logger.error("DirectReplicationFitnessGene proporty set after later than its first tick");
		}
		this.chanceOfSterilisation = chanceOfSterilisation;
		// Recursively sets all subgenes to same value !!
		// This part of the code  should run only at initialization of world
		
		if ( this.getSubGenes() != null ) {
			for( DirectReplicationFitnessGene gene : this.getSubGenes()) {
				DeltaGene deltaGene = (DeltaGene) gene;
				deltaGene.setChanceOfSterilisation(chanceOfSterilisation);
			}
		}
	}

}
