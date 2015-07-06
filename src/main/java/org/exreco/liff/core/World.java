package org.exreco.liff.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.exreco.experiment.AgeTracked;
import org.exreco.experiment.Case;
import org.exreco.experiment.CaseInitializerIf;
import org.exreco.experiment.util.CompoundCollection;

public class World extends Case implements AgeTracked, Runnable,
		Serializable {


	public abstract static class BaseInitializer implements Serializable, CaseInitializerIf {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6991774249639205885L;
		private World world;

		public void init(Case world) {
			this.setWorld((World) world);
			// Assigning back-references
			this.getWorld().assignWorldReferenceToGenes();
			this.getWorld().getSpace().setWorld(this.getWorld());
			// ----

			this.initSpace();
			this.initReplicators();

			this.getWorld().initReplicatorCollection();
		}

		public void initSpace() {
			Space space = this.getWorld().getSpace();
			// Normlisation of wandering
	

			if (space.getSquareSide() != 0) {
				space.setMaxX(space.getSquareSide());
				space.setMaxY(space.getSquareSide());
			}
			space.init();

			this.initSpaceRegions();
			this.initSpaceRegionList();

		}

		protected void initSpaceRegions() {
			Space space = this.getWorld().getSpace();
			space.setRegions(new Region[space.getMaxX()][space.getMaxY()]);
			int regionPopulation = (int) (space.getMaxPopulation() / ((double) space
					.getMaxX() * (double) space.getMaxY()));
			for (int i = 0; i < space.getMaxX(); i++) {
				for (int j = 0; j < space.getMaxY(); j++) {
					Region region = space.createRegion();

					region.init(space);
					region.setX(i);
					region.setY(j);
					region.setMaxPopulation(regionPopulation);
					space.getRegions()[i][j] = region;

				}
			}
		}

		protected void initSpaceRegionList() {
			Space space = this.getWorld().getSpace();
			space.setRegionList(new ArrayList<Region>(space.getMaxX()
					* space.getMaxY()));
			for (int i = 0; i < space.getMaxX(); i++) {
				for (int j = 0; j < space.getMaxY(); j++) {
					space.getRegionList().add(space.getRegions()[i][j]);
				}
			}
			Collections.shuffle(space.getRegionList(), this.getWorld()
					.getRandom());
		}

		public abstract void initReplicators();

		public World getWorld() {
			return world;
		}

		public void setWorld(World world) {
			this.world = world;
		}
	}




	

	interface ExitChecker {
		boolean isCaseFinished();
	}
	
	public class FiftyFiftyExitChecker implements ExitChecker , Serializable {
		
	
		
		@Override
		public boolean isCaseFinished() {
			if (World.this.earlyExit && World.this.getAge() % 2 == 1) {
				Collection<DirectReplicationFitnessGene> testedGenes = World.this.getEveGene().getSubGenes();
				int nonExtints = 0;
				for (DirectReplicationFitnessGene gene : testedGenes) {
					
					if (gene.fetchReplicatorsInLineage().size() != 0) {
						nonExtints++;
					}

				}
				if (nonExtints < 2) {
					return true;
				}
			}

			return World.this.getMaxLifeTime() < World.this.getAge();

		}
		
	}
	public class OneDotTwoExistExitChecker implements ExitChecker, Serializable  {
		
	
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -569421129145313630L;

		@Override
		public boolean isCaseFinished() {

			DirectReplicationFitnessGene gene_1_2 = World.this.getEveGene().getSubGene(2);
	
			return ( gene_1_2.fetchReplicatorsInLineage().size() == 0 || (World.this.isEarlyExit() && (World.this.getMaxLifeTime() < World.this.getAge())));

		}
		
	}
	private static final long serialVersionUID = -7872790275883695648L;
	private static Logger logger = LogManager.getLogger(World.class.getName());


	private boolean earlyExit = false;
	private DirectReplicationFitnessGene eveGene;
	private ExitChecker exitChecker;
	private long age = 0;

	private int maxLifeTime = 0;

	private final int maxPopulation = 0;
	private Space space;
	private final CompoundCollection<Replicator> replicators = new CompoundCollection<Replicator>();

	private Random random;
	/**
	 * When no living replicator instance is alive for gene and no subgenes
	 * available, then the application will not retain this gene. This is mostly
	 * for memory management reasons. 0 - All genes are preserved for later
	 * statistics even if it is extinct currently
	 */
	private int minAllTimePopulationForEternalFame;

	public World() {
		this.random = // ThreadLocalRandom.current();
		new Random();
	}

	protected void assignWorldReferenceToGenes() {
		this.getEveGene().setWorld(this);
		Collection<DirectReplicationFitnessGene> allGenes = this.getEveGene().getSubGenes();
		for (DirectReplicationFitnessGene gene : allGenes) {
			gene.setWorld(this);
		}
	}

	@Override
	public void init() {
		super.init();

	}

	public void initReplicatorCollection() {
		// this.getSpace().initReplicators();
		for (int i = 0; i < this.getSpace().getMaxX(); i++) {
			for (int j = 0; j < this.getSpace().getMaxY(); j++) {
				Region region = this.getSpace().getRegions()[i][j];
				if (region != null) {
					Collection<Replicator> regionalCollection = region
							.getReplicators();
					this.replicators.getCollections().add(regionalCollection);
				}
			}
		}
	}

	@Override
	public WorldStatusEvent createStatus() {
		return new WorldStatusEvent(this);
	}

	public DirectReplicationFitnessGene getEveGene() {
		return this.eveGene;
	}

	public void setEveGene(DirectReplicationFitnessGene eveGene) {
		this.eveGene = eveGene;
	}

	public DirectReplicationFitnessGene lookupGene(String id) {
		DirectReplicationFitnessGene gene = World.this.getEveGene();

		String[] variantStrings = id.replace('.', 'x').split("x");
		int variantsLength = variantStrings.length;
		int[] variants = new int[variantsLength];
		for (int i = 1; i < variantsLength; i++) {
			variants[i] = Integer.parseInt(variantStrings[i]);
			gene = World.this.getEveGene().getSubGene(variants[i]);
		}
		return gene;

	}


	@Override
	public void finish() {

		super.finish();

		try {
			this.getEventSource().fireEvent(new World.RunEndedEvent());
		} catch (Exception e) {
			logger.warn("Error at finishing case ", e);

		}

		// this.getEventSource().getListeners().clear();
		// this.getTableLoggers().eventOccurred(new World.RunEndedEvent());
	}

	@Override
	public void run() {

		try {

			super.run();

			logger.debug("World is running with dimension {} ", this
					.getDimensionSetPoint().toString());

			World.Event runStartedEvent = new RunStartedEvent();

			this.getEventSource().fireEvent(runStartedEvent);
			// this.getTableLoggers().eventOccurred(new
			// World.RunStartedEvent());

			this.setLifeCycleState(LifeCycleState.RUNNING);
			while (!this.isWorldEnded()) {

				this.tick();

			}
			this.finish();

			logger.debug("World finished.");

		} catch (Throwable e) {
			logger.debug("Exception while running case #" + this.getCaseId()
					+ " : ", e);

		}

	}

	@Override
	public long getAge() {
		return this.age;
	}

	public void tick() throws Exception {
		/*
		 * this.getExperiment().getPauseLock().lock(); try { while
		 * (this.getExperiment().isPaused())
		 * this.getExperiment().getUnpaused().await(); } catch
		 * (InterruptedException ie) { Thread.currentThread().interrupt(); }
		 * finally { this.getExperiment().getPauseLock().unlock(); }
		 */

		this.getSpace().tick();
		this.getEventSource().fireEvent(new World.TickEndedEvent());

		this.age++;

	}

	public void setMaxLifeTime(int maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
	}

	protected void clean() {
		if (this.getAge() % 100 == 0) {
			this.getEveGene().clean();
			System.gc();
		}
		// GammaLiff.startStopwatch();

		/*
		 * Runtime runtime = Runtime.getRuntime(); long minRunningMemory =
		 * this.getConstants().getFullGeneTreeCleaningBarrier();
		 * 
		 * if(this.getAge() % 50 == 0 || runtime.maxMemory()==
		 * runtime.totalMemory() && runtime.freeMemory() < minRunningMemory) {
		 * 
		 * System.out.println("\nGene tree cleaning at age " + this.getAge());
		 * System.out.println("Free Memory : " + runtime.freeMemory());
		 * System.out.println("Max Memory : " + runtime.maxMemory());
		 * System.out.println("Total Memory : " + runtime.totalMemory());
		 * 
		 * this.getEveGene().clean(); System.gc();
		 * 
		 * System.out.println("Free Memory : " + runtime.freeMemory());
		 * System.out.println("Max Memory : " + runtime.maxMemory());
		 * System.out.println("Total Memory : " + runtime.totalMemory());
		 * 
		 * }
		 */

		// GammaLiff.stopStopwatch();
	}

	@Override
	public long getBirthTick() {
		return 0;
	}

	final public Random getRandom() {
		return this.random;
	}

	/**
	 * Returns a read-only list of all the replicators on the world.
	 * 
	 * @return
	 */
	public Collection<Replicator> getReplicators() {
		return this.replicators;
	}

	@Override
	public int getMaxLifeTime() {
		return maxLifeTime;
	}

	public int getMaxPopulation() {
		return maxPopulation;
	}

	/**
	 * @return the endOfWorldEventSource
	 */
	/*
	 * public EventSource<DestructedEvent> getEndOfWorldEventSource() { return
	 * endOfWorldEventSource; }
	 */
	/**
	 * @return the space
	 */
	public Space getSpace() {
		return space;
	}

	/**
	 * @param space
	 *            the space to set
	 */
	public void setSpace(Space space) {
		this.space = space;
	}

	/**
	 * @return the minAllTimePopulationForEternalFame
	 */
	public int getMinAllTimePopulationForEternalFame() {
		return minAllTimePopulationForEternalFame;
	}

	/**
	 * @param minAllTimePopulationForEternalFame
	 *            the minAllTimePopulationForEternalFame to set
	 */
	public void setMinAllTimePopulationForEternalFame(
			int minAllTimePopulationForEternalFame) {
		this.minAllTimePopulationForEternalFame = minAllTimePopulationForEternalFame;
	}

	public boolean isWorldEnded() {
		return this.getExitChecker().isCaseFinished();
	}

	

	public boolean isEarlyExit() {
		return earlyExit;
	}

	public void setEarlyExit(boolean earlyExit) {
		this.earlyExit = earlyExit;
	}

	public ExitChecker getExitChecker() {
		return exitChecker;
	}

	public void setExitChecker(ExitChecker exitChecker) {
		this.exitChecker = exitChecker;
	}
}
