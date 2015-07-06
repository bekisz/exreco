package org.exreco.liff.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import org.exreco.liff.experiments.delta.DeltaGene;

public class Region implements Active, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9063668655503802261L;

	private Collection<Replicator> replicators = new LinkedHashSet<Replicator>();
	// private final EventDrivenCollectionProxy<Replicator>
	// eventDrivenReplicatorCollection = new
	// EventDrivenCollectionProxy<Replicator>(
	// replicators);
	private Space space;
	private int x;
	private int y;
	private int maxPopulation;

	/**
	 * @param maxPopulation
	 *            the maxPopulation to set
	 */
	public void setMaxPopulation(int maxPopulation) {
		this.maxPopulation = maxPopulation;
	}

	private final Collection<Replicator> selectedReplicators = new LinkedHashSet<Replicator>();

	// private World world;
	private double totalReplicationFitness;

	public Region() {

	}

	public void init(Space space) {
		this.space = space;

	}

	/*
	 * public void init(Space space, int x, int y) { this.space = space; this.x
	 * = x;
	 * 
	 * this.y = y;
	 * 
	 * 
	 * }
	 */

	public void reset() {
		this.selectedReplicators.clear();
	}

	public Collection<Replicator> getSelectedReplicators() {
		return this.selectedReplicators;
	}

	public Collection<Replicator> getReplicators() {
		return replicators;
	}

	public void setReplicators(Collection<Replicator> replicators) {
		this.replicators = replicators;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
 		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public void tick() {
		// this.getReplicatorListLogger().log("World.Age", this.getAge());

		ReplicatorCollection<Replicator> allReplicators = new ReplicatorCollection<Replicator>(
				this.getReplicators());
		ReplicatorCollection<Replicator>.Parameter<Double> params = allReplicators.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {

				return replicator.getGene().getReplicationFitness();
			}
		};

		this.setTotalReplicationFitness(params.sum());
		this.reset();
		this.select();

		for (Replicator replicator : new ArrayList<Replicator>(
				this.getReplicators())) {
			replicator.tick();
		}

	}

	private List<Double> randomValues = null;

	public int getNmOfSelectedReplicators() {

		return this.getMaxPopulation() - this.getReplicators().size();
	}

	public int getMaxPopulation() {

		return this.maxPopulation;
	}

	public Collection<Replicator> select() {
		/*-----*/
		// GammaLiff.startStopwatch();

		int nmSelectedReplicators = this.getNmOfSelectedReplicators();

		if (nmSelectedReplicators < 1) {
			return this.getSelectedReplicators();
		}

		if (this.randomValues == null) {
			this.randomValues = new ArrayList<Double>(nmSelectedReplicators);
		} else {
			this.randomValues.clear();
		}
		Random rand = this.getSpace().getWorld().getRandom();

		for (int i = 0; i < nmSelectedReplicators; i++) {
			this.randomValues.add(rand.nextDouble()
					* this.getTotalReplicationFitness());
		}
		this.searchForLuckyReplicators();
		/*-----*/
		// GammaLiff.stopStopwatch();

		return this.getSelectedReplicators();
	}

	private void searchForLuckyReplicators() {
		// GammaLiff.startStopwatch();
		Collections.sort(this.randomValues);
		// GammaLiff.stopStopwatch();
		double cumulatedReplicationFitness = 0;

		Collection<Replicator> list = this.getReplicators();
		Collection<Replicator> listTo = this.getSelectedReplicators();
		int i = 0;
		for (Replicator replicator : list) {
			// for (int j = 0; j < list.size() && i < this.randomValues.size();
			// j++) {
			// ExplicitFitnessReplicator replicator = list.get(j);
			if (i >= this.randomValues.size())
				break;
			cumulatedReplicationFitness += replicator.getGene().getReplicationFitness();
			if (cumulatedReplicationFitness >= this.randomValues.get(i)) {
				listTo.add(replicator);
				i++;
			}

		}
	}

	/**
	 * @return the totalReplicationFitness
	 */
	public double getTotalReplicationFitness() {
		return totalReplicationFitness;
	}

	/**
	 * @param totalReplicationFitness
	 *            the totalReplicationFitness to set
	 */
	public void setTotalReplicationFitness(double totalReplicationFitness) {
		this.totalReplicationFitness = totalReplicationFitness;
	}

	public List<Region> getAdjacentRegions() {
		List<Region> adjacentRegions = new ArrayList<Region>(4);
		int maxX = this.getSpace().getMaxX();
		int maxY = this.getSpace().getMaxY();
		int x = this.getX();
		int y = this.getY();

		int northY = ((y - 1) + maxY) % maxY;
		int northX = x;
		int eastY = y;
		int eastX = (x + 1) % maxX;
		int southY = (y + 1) % maxY;
		int southX = x;
		int westY = y;
		int westX = ((x - 1) + maxX) % maxX;

		adjacentRegions.add(this.getSpace().getRegions()[northX][northY]);
		adjacentRegions.add(this.getSpace().getRegions()[eastX][eastY]);
		adjacentRegions.add(this.getSpace().getRegions()[southX][southY]);
		adjacentRegions.add(this.getSpace().getRegions()[westX][westY]);

		return adjacentRegions;
	}
}
