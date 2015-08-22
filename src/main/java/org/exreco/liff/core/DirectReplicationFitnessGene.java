package org.exreco.liff.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.AgeTracked;
import org.exreco.experiment.util.events.LiffEventListener;
import org.exreco.liff.experiments.delta.DeltaGene;

abstract public class DirectReplicationFitnessGene  implements Gene, AgeTracked, Active, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4700311447619026334L;

	protected class ReplicatorListener implements
			LiffEventListener {
		@Override
		public void eventOccurred(Serializable serEvent) {
			Replicator.Event event = (Replicator.Event) serEvent;
			
			if (event instanceof Replicator.DestructedEvent) {
				DirectReplicationFitnessGene.this.unRegisterReplicatorInstance(event.getReplicator());
			} else if (event instanceof Replicator.CreatedEvent) {

				DirectReplicationFitnessGene.this.registerReplicatorInstance(event.getReplicator());

			}
		}
	}

	private int variant;
	private int nextMutantVariant = 1;

	/** The ancestor gene */
	private DirectReplicationFitnessGene superGene;
	private Collection<Replicator> replicators = new LinkedHashSet<Replicator>();

	private Collection<DirectReplicationFitnessGene> subGenes = new LinkedHashSet<DirectReplicationFitnessGene>();

	// Total number of replicators with this gene ( successor genes not counted)
	private long allTimePopulation = 0;

	private long birthTick;
	private World world;
	private String name = "unknown";
	/** Chance that the replicator survives a tick */
	private double survivalChance;

	/** Chance weight that a replicator replicates in a given tick */
	private double replicationFitness;
	
	private static Logger logger = LogManager.getLogger(DirectReplicationFitnessGene.class.getName());

	public void init() {
		logger.debug("DirectReplicationFitnessGene {} init'd.", this.getName());
	}

	

	transient private final ReplicatorListener replicatorListener = new ReplicatorListener();

	final public World getWorld() {
		return this.world;
	}

	public DirectReplicationFitnessGene() {
		this.superGene = null;
		this.variant = 1;
		// this.world = world;

		this.birthTick = 0;
		// this.world.setEveGene(this);
	}

	public DirectReplicationFitnessGene(DirectReplicationFitnessGene ancestor) {
		super();
		this.init(ancestor);

	}

	public void init(DirectReplicationFitnessGene superGene) {
		this.superGene = superGene;
		this.variant = superGene.getNextMutantVariant();
		superGene.setNextMutantVariant(this.variant + 1);
		superGene.registerSubgene(this);

		this.world = superGene.getWorld();
		if (this.world != null) {
			this.birthTick = this.world.getAge();
		}
		this.survivalChance = superGene.getSurvivalChance();
		this.replicationFitness = superGene.getReplicationFitness();	

	}

	/**
	 * Used to init the eveGene
	 * 
	 * @param world
	 */
	public DirectReplicationFitnessGene(World world) {
		this.variant = 1;
		this.world = world;
		this.superGene = null;
		this.birthTick = this.world.getAge();
		this.world.setEveGene(this);

	}

	abstract public DirectReplicationFitnessGene createGene();

	protected void registerSubgene(DirectReplicationFitnessGene gene) {
		this.getSubGenes().add(gene);
	}

	protected void unegisterSubgene(DirectReplicationFitnessGene gene) {
		this.getSubGenes().remove(gene);
		if (this.isExtinct()) {
			this.subGenes = Collections.emptyList();
			this.replicators = Collections.emptyList();
			int minAllTimePopulationForEternalFame = this.getWorld()
					.getMinAllTimePopulationForEternalFame();
			if (this.getAllTimePopulation() < minAllTimePopulationForEternalFame) {
				this.getSuperGene().unegisterSubgene(this);
			}
		}
	}

	@Override
	public String toString() {
		if (this.superGene == null) {
			return Integer.toString(this.getVariant());

		} else {
			return this.superGene.toString() + "."
					+ Integer.toString(this.getVariant());
		}

	}

	public int getVariant() {
		return variant;
	}

	public DirectReplicationFitnessGene getSuperGene() {
		return superGene;
	}

	public int calculateGeneticDistanceFromEve() {
		int i = 0;
		for (DirectReplicationFitnessGene ancestor = this.getSuperGene(); ancestor != null; ancestor = ancestor
				.getSuperGene()) {
			i++;

		}
		return i;
	}

	public int calculateGeneticDistanceFrom(DirectReplicationFitnessGene other) {
		DirectReplicationFitnessGene commonAncestor = this.searchCommonAncestor(other);
		int i = 1;
		for (DirectReplicationFitnessGene ancestor = this; ancestor != commonAncestor; ancestor = ancestor
				.getSuperGene()) {
			i++;
		}

		for (DirectReplicationFitnessGene ancestor = other; ancestor != commonAncestor; ancestor = ancestor
				.getSuperGene()) {
			i++;
		}

		return i;
	}

	public DirectReplicationFitnessGene searchCommonAncestor(DirectReplicationFitnessGene other) {

		if (this == other) {
			return this;
		}
		for (DirectReplicationFitnessGene ancestor = this.getSuperGene(); ancestor != null; ancestor = ancestor
				.getSuperGene()) {
			if (other.isDescendantOf(ancestor)) {
				return ancestor;
			}
		}
		return null;
	}

	public boolean isDescendantOf(DirectReplicationFitnessGene ancestor) {
		DirectReplicationFitnessGene gene = this;
		while (gene != null) {
			if (gene == ancestor) {
				return true;
			}

			gene = gene.getSuperGene();
		}
		return false;

	}

	public DirectReplicationFitnessGene getSubGene(int variantNumber) {
		Iterator<DirectReplicationFitnessGene> it = this.getSubGenes().iterator();
		while (it.hasNext()) {
			DirectReplicationFitnessGene gene = it.next();
			if (gene.getVariant() == variantNumber) {
				return gene;
			}
		}
		return null;
	}

	/**
	 * Total number of replicators with this gene ( successor genes not counted)
	 * ever existed
	 * 
	 * @return number of replicator instances
	 */
	public long getAllTimePopulation() {
		return this.allTimePopulation;
	}

	public long fetchAllTimePopulationInLineage() {

		Iterator<DirectReplicationFitnessGene> it = this.getSubGenes().iterator();
		long result = 0;

		// genes.addAll(this.getOffsprings());

		while (it.hasNext()) {
			DirectReplicationFitnessGene gene = it.next();
			result += gene.fetchAllTimePopulationInLineage();
		}
		result += this.getAllTimePopulation();
		return result;
	}

	public boolean isExtinct() {
		return (this.subGenes.isEmpty() && this.replicators.isEmpty());
	}

	/*
	 * Buggy
	 */
	public boolean isExtinctInLineage() {
		if (!this.isExtinct()) {
			return false;
		} else {
			Iterator<DirectReplicationFitnessGene> it = this.getSubGenes().iterator();
			while (it.hasNext()) {
				DirectReplicationFitnessGene gene = it.next();
				if (!gene.isExtinctInLineage()) {
					return false;
				}
			}

		}
		return true;
	}

	public LiffEventListener getReplicatorEventListener() {
		return this.replicatorListener;
	}

	public void registerReplicatorInstance(Replicator newReplicatator) {
		this.allTimePopulation++;
		this.replicators.add(newReplicatator);
	}

	public void unRegisterReplicatorInstance(Replicator oldReplicatator) {

		while (this.replicators.remove(oldReplicatator))
			;
		// oldReplicatator.getEventSource().getListeners().remove(this);
		// If number of replicators became zero it means that it will never have
		// replictors anymore as it became extinct.
		// To free up the List we link to an empty null list

		if (this.replicators.isEmpty()) {
			this.replicators = Collections.emptyList();
			if (this.subGenes.isEmpty()) {
				this.subGenes = Collections.emptyList();
				if (this.getAllTimePopulation() < this.getWorld()
						.getMinAllTimePopulationForEternalFame()) {
					this.getSuperGene().getSubGenes().remove(this);
				}
			}

		}

	}

	public Collection<Replicator> getReplicators() {
		return this.replicators;
	}

	public class Filter {
		public boolean isIncluded(DirectReplicationFitnessGene gene) {
			return true;
		}
	};

	/**
	 * Fetches this genes all other genes in it its lineage.
	 * 
	 * @param genes
	 *            this genes and all offspring genes
	 */
	public void fetchGenesInLineage(List<DirectReplicationFitnessGene> genes, Filter filter) {
		if (filter.isIncluded(this)) {
			genes.add(this);
		}
		Iterator<DirectReplicationFitnessGene> it = this.getSubGenes().iterator();
		while (it.hasNext()) {
			DirectReplicationFitnessGene gene = it.next();
			gene.fetchGenesInLineage(genes, filter);
		}

	}

	public List<DirectReplicationFitnessGene> fetchGenesInLineage(Filter filter) {
		List<DirectReplicationFitnessGene> result = new LinkedList<DirectReplicationFitnessGene>();
		this.fetchGenesInLineage(result, filter);
		return result;
	}

	public List<DirectReplicationFitnessGene> fetchGenesInLineage() {
		return this.fetchGenesInLineage(new DirectReplicationFitnessGene.Filter());
	}

	public double calculateAvergeGeneticDistanceFromDescendantReplicators() {
		ReplicatorCollection<Replicator> replicators = this
				.fetchReplicatorsInLineage();
		return replicators.calculateAverageGeneticDistanceFrom(this);
	}

	/**
	 * Calculates what percentage of successful mutations are built into the
	 * genetic history of the lineage. (At least 10 replicator instance needed)
	 * 
	 * @return
	 */
	public double calculateEvolutionaryEfficiency() {
		double result = this
				.calculateAvergeGeneticDistanceFromDescendantReplicators()
				/ this.countSuccessfulGenesInLineage(10);
		return result;
	}

	/**
	 * Calculates the ratio of regions where this gene is present
	 * 
	 * @return
	 */
	public double calculateRegionalSpread() {
		double result = 0;
		Space space = this.getWorld().getSpace();
		int regions = space.getRegionList().size();
		int nrOfRegionsPopulated = 0;
		for (Region region : space.getRegionList()) {
			for (Replicator replicator : region.getReplicators()) {
				if (replicator.getGene().isDescendantOf(this)) {
					nrOfRegionsPopulated++;
					break;
				}

			}
		}
		result = (double) nrOfRegionsPopulated / (double)regions;
		return result;
	}

	/**
	 * Calculates the ratio of regions where this gene is present
	 * 
	 * @return
	 */

	/**
	 * Count the number of successful mutations happened in the last 'ticks'
	 * ticks
	 * 
	 * @param ticks
	 * @return
	 */

	public int countNumberOfMutationsIn(final int ticks) {

		Filter youngFilter = new Filter() {
			@Override
			public boolean isIncluded(DirectReplicationFitnessGene gene) {
				return (gene.getAge() <= ticks);
			}
		};

		return this.fetchGenesInLineage(youngFilter).size();
	}

	/**
	 * Count the number of successful subgenes having at least minReplicators in
	 * their total life-time
	 * 
	 * 
	 * @param ticks
	 * @return
	 */

	public int countSuccessfulGenesInLineage(final int minReplicators) {

		Filter successfulGeneFilter = new Filter() {
			@Override
			public boolean isIncluded(DirectReplicationFitnessGene gene) {
				return (gene.getAllTimePopulation() >= minReplicators);
			}
		};
		List<DirectReplicationFitnessGene> successfulGeneList = this
				.fetchGenesInLineage(successfulGeneFilter);

		return successfulGeneList.size();
	}

	public void fetchReplicatorsInLineage(
			ReplicatorCollection<Replicator> offspringReplicators) {
		Iterator<DirectReplicationFitnessGene> it = this.getSubGenes().iterator();
		offspringReplicators.addAll(this.getReplicators());
		while (it.hasNext()) {
			DirectReplicationFitnessGene gene = it.next();
			gene.fetchReplicatorsInLineage(offspringReplicators);
		}

	}

	public ReplicatorCollection<Replicator> fetchReplicatorsInLineage() {
		ReplicatorCollection<Replicator> result = new ReplicatorCollection<Replicator>(
				new LinkedList<Replicator>());
		this.fetchReplicatorsInLineage(result);
		return result;
	}

	abstract public DirectReplicationFitnessGene mutate();

	abstract public Replicator createReplicator();

	public Collection<DirectReplicationFitnessGene> getSubGenes() {
		return subGenes;
	}

	public void deleteSubGenes() {
		this.getSubGenes().clear();
	}

	/**
	 * Deletes all the dead end genes that has no sublineage or replicator
	 * instances, and the total number replicator instances are small than
	 * specified in minAllTimePopulationForEternalFame.
	 */

	public void clean() {
		int minAllTimePopulationForEternalFame = this.getWorld()
				.getMinAllTimePopulationForEternalFame();
		Iterator<DirectReplicationFitnessGene> it = this.getSubGenes().iterator();

		while (it.hasNext()) {
			DirectReplicationFitnessGene gene = it.next();
			gene.clean();
			if (gene.isExtinct()
					&& gene.getAllTimePopulation() < minAllTimePopulationForEternalFame) {
				it.remove();
			}
		}
	}

	public double calculateAverageReplicatorAge() {
		Iterator<Replicator> it = this.getReplicators().iterator();
		double sum = 0;
		int count = this.getReplicators().size();
		if (count == 0) {
			return 0.0;
		}
		while (it.hasNext()) {
			Replicator replicator = it.next();
			sum += replicator.getAge();
		}
		return sum / this.getReplicators().size();

	}

	public double calculateAverageReplicatorAgeInLineage() {

		Iterator<DirectReplicationFitnessGene> it = this.getSubGenes().iterator();
		double sum = 0;
		int counter = 0;
		while (it.hasNext()) {
			DirectReplicationFitnessGene gene = it.next();
			sum += gene.calculateAverageReplicatorAgeInLineage();
		}
		// sum += this.();
		return sum / counter;
	}

	@Override
	public void tick() {

	}

	@Override
	public long getAge() {
		return this.world.getAge() - this.getBirthTick();
	}

	@Override
	public long getBirthTick() {

		return this.birthTick;
	}

	protected int getNextMutantVariant() {
		return nextMutantVariant;
	}

	protected void setNextMutantVariant(int nextMutantVariant) {
		this.nextMutantVariant = nextMutantVariant;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the supergene id out of a gene id.
	 * 
	 * @param id
	 * @return supergene ID or "" is returned when there is no ancestor
	 *         (supergene)
	 */
	public static String getSuperGeneId(String id) {
		int lastDotPos = id.lastIndexOf('.');
		if (lastDotPos < 1) {
			return "";
		}
		return id.substring(0, lastDotPos);
	}

	/**
	 * Gets the variant id out of a gene id.
	 * 
	 * @param id
	 * @return supergene ID or "" is returned when there is no ancestor
	 *         (supergene)
	 */
	public static String getVariantId(String id) {
		int lastDotPos = id.lastIndexOf('.');
		if (lastDotPos < 1) {
			return id;
		}
		return id.substring(lastDotPos + 1);
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	public void setSuperGene(DirectReplicationFitnessGene superGene) {
		this.superGene = superGene;
	}

	public void setSubGenes(Collection<DirectReplicationFitnessGene> subGenes) {
		this.subGenes = subGenes;
	}

	public void setWorld(World world) {
		this.world = world;
	}
	public double getSurvivalChance() {
		return survivalChance;
	}

	public void setSurvivalChance(double survivalChance) {
		if ( this.getWorld() != null && this.getAge() != 0 ) {
			logger.error("DirectReplicationFitnessGene proporty set after later than its first tick");
		}
		this.survivalChance = survivalChance;
		// Recursively sets all subgenes to same value !!
		// This part of the code  should run only at initialization of world
		
		if ( this.getSubGenes() != null ) {
			for( DirectReplicationFitnessGene gene : this.getSubGenes()) {
				DeltaGene deltaGene = (DeltaGene) gene;
				deltaGene.setSurvivalChance(survivalChance);
			}
		}
	}

	public double getReplicationFitness() {
		return replicationFitness;
	}

	public void setReplicationFitness(double replicationFitness) {
		if ( this.getWorld() != null && this.getAge() != 0 ) {
			logger.error("DirectReplicationFitnessGene proporty set after later than its first tick");
		}
		this.replicationFitness = replicationFitness;
		// Recursively sets all subgenes to same value !!
		// This part of the code  should run only at initialization of world
		
		if ( this.getSubGenes() != null ) {
			for( DirectReplicationFitnessGene gene : this.getSubGenes()) {
				DeltaGene deltaGene = (DeltaGene) gene;
				deltaGene.setReplicationFitness(replicationFitness);
			}
		}
	}
}
