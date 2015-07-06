package org.exreco.liff.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.exreco.experiment.util.CollectionProxy;


public class ReplicatorCollection<ReplicatorType extends Replicator> extends
		CollectionProxy<ReplicatorType> implements Collection<ReplicatorType>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6521139299598188928L;

	// private Filter filter = new NullFilter();

	public abstract class Parameter<Type extends Number> {
		public abstract Type getNumber(ReplicatorType replicator);

		public double average() {

			return this.sum() / ReplicatorCollection.this.size();

		}

		public double sum() {
			double sum = 0;
			Iterator<ReplicatorType> it = ReplicatorCollection.this.proxiedCollection
					.iterator();
			while (it.hasNext()) {
				ReplicatorType replicator = it.next();
				sum += this.getNumber(replicator).doubleValue();
			}
			return sum;
		}

		public double min() {

			Iterator<ReplicatorType> it = ReplicatorCollection.this.proxiedCollection
					.iterator();
			double minValue = Double.NaN;
			while (it.hasNext()) {
				ReplicatorType replicator = it.next();
				double currentValue = this.getNumber(replicator).doubleValue();
				if (Double.isNaN(minValue) || currentValue < minValue) {
					minValue = currentValue;
				}
			}
			return minValue;

		}

		public double max() {

			Iterator<ReplicatorType> it = ReplicatorCollection.this.proxiedCollection
					.iterator();
			double maxValue = Double.NaN;
			while (it.hasNext()) {
				ReplicatorType replicator = it.next();
				double currentValue = this.getNumber(replicator).doubleValue();
				if (Double.isNaN(maxValue) || currentValue > maxValue) {
					maxValue = currentValue;
				}
			}
			return maxValue;

		}

		public double sampleStandardDeviation() {

			int n = ReplicatorCollection.this.size();
			double sum = 0;
			double squareSum = 0;
			Iterator<ReplicatorType> it = ReplicatorCollection.this.proxiedCollection
					.iterator();
			while (it.hasNext()) {
				ReplicatorType replicator = it.next();
				double a = this.getNumber(replicator).doubleValue();
				sum += a;
				squareSum += a * a;
			}

			double avg = sum / n;
			double deviation = Math.sqrt(squareSum / n
					- ((double) n / (n - 1) * (avg * avg)));
			return deviation;

		}

		public double standardDeviation() {

			int n = ReplicatorCollection.this.size();
			double sum = 0;
			double squareSum = 0;
			Iterator<ReplicatorType> it = ReplicatorCollection.this.proxiedCollection
					.iterator();
			while (it.hasNext()) {
				ReplicatorType replicator = it.next();
				double a = this.getNumber(replicator).doubleValue();
				sum += a;
				squareSum += a * a;
			}

			double avg = sum / n;
			double deviation = Math.sqrt(squareSum / n - (avg * avg));
			return deviation;

		}

		public ReplicatorType maxReplicator() {

			Iterator<ReplicatorType> it = ReplicatorCollection.this.proxiedCollection
					.iterator();
			ReplicatorType fittest;
			if (it.hasNext()) {
				fittest = it.next();
			} else {
				return null;
			}
			ReplicatorType replicator = null;
			while (it.hasNext()) {
				replicator = it.next();
				double currentFitness = this.getNumber(replicator)
						.doubleValue();
				if (currentFitness > this.getNumber(fittest).doubleValue()) {
					fittest = replicator;
				}
			}
			return fittest;

		}

	}

	public ReplicatorCollection(Collection<ReplicatorType> replicatorList) {
		super(replicatorList);
	}

	public ReplicatorCollection(Object replicatorList) {
		super(replicatorList);
	}

	public double calculateAverageGeneticDistanceFrom(DirectReplicationFitnessGene otherGene) {
		long cumulatedDistance = 0;
		Iterator<ReplicatorType> it = this.proxiedCollection.iterator();

		while (it.hasNext()) {
			ReplicatorType replicator = it.next();

			cumulatedDistance += replicator.getGene()
					.calculateGeneticDistanceFrom(otherGene);
		}

		return (double) cumulatedDistance / (double) this.size();
	}

	public double calculateAverageGeneticDistance() {
		long cumulatedDistance = 0;

		Iterator<ReplicatorType> it = this.proxiedCollection.iterator();

		while (it.hasNext()) {
			Replicator replicator = it.next();

			Iterator<ReplicatorType> jt = this.proxiedCollection.iterator();

			while (jt.hasNext()) {
				Replicator otherReplicator = jt.next();

				cumulatedDistance += replicator
						.getGene()
						.calculateGeneticDistanceFrom(otherReplicator.getGene());

			}
		}

		return (double) cumulatedDistance / (double) this.size();
	}

	/**
	 * Seeks the gene with highest population number given the gene-population
	 * map
	 * 
	 * @return dominant gene
	 */
	public DirectReplicationFitnessGene seekDominantGene(Map<DirectReplicationFitnessGene, Integer> map) {
		DirectReplicationFitnessGene result = null;
		Iterator<DirectReplicationFitnessGene> it = map.keySet().iterator();
		int max = 0;
		while (it.hasNext()) {
			DirectReplicationFitnessGene gene = it.next();
			int x = map.get(gene);
			if (x > max) {
				max = x;
				result = gene;
			}
		}
		return result;

	}

	/**
	 * Seeks the gene with highest population number
	 * 
	 * @return dominant gene
	 */
	public DirectReplicationFitnessGene seekDominantGene() {
		Map<DirectReplicationFitnessGene, Integer> map = this.retrieveGene2ReplicatorNumberMap();
		return this.seekDominantGene(map);
	}

	public Map<DirectReplicationFitnessGene, Integer> retrieveGene2ReplicatorNumberMap() {
		Map<DirectReplicationFitnessGene, Integer> map = new HashMap<DirectReplicationFitnessGene, Integer>();

		Iterator<ReplicatorType> it = this.proxiedCollection.iterator();

		while (it.hasNext()) {
			Replicator replicator = it.next();

			Integer num = map.get(replicator.getGene());

			if (num == null) {
				map.put(replicator.getGene(), 1);
			} else {
				map.put(replicator.getGene(), ++num);
			}
		}

		return map;
	}

	public Set<DirectReplicationFitnessGene> retrieveGeneSet() {
		Set<DirectReplicationFitnessGene> geneSet = this.retrieveGene2ReplicatorNumberMap().keySet();
		return geneSet;
	}

	public int calculateGeneticDiversity() {
		return this.retrieveGene2ReplicatorNumberMap().keySet().size();
	}

	/*
	 * public ReplicatorListAdapter clone(Filter filter) { ReplicatorListAdapter
	 * newReplicatorList = new ReplicatorListAdapter(); Iterator<Replicator> it
	 * = this.collection.iterator();
	 * 
	 * while (it.hasNext()) { Replicator replicator = it.next(); if
	 * (filter.isIncluded(replicator)) {
	 * newReplicatorList.getReplicatorList().add(replicator); } } return
	 * newReplicatorList; }
	 * 
	 * public ReplicatorListAdapter clone() { return clone(this.filter); }
	 */
	public int countNumberOfNewMutations() {
		int result = 0;
		Iterator<ReplicatorType> it = this.proxiedCollection.iterator();

		while (it.hasNext()) {
			Replicator replicator = it.next();

			if (replicator.getGene().getAge() == 0) {
				result++;
			}

		}
		return result;
	}

	/**
	 * public double calculateAverageGeneticDistanceFromEve() { long
	 * cumulatedDistance = 0;
	 * 
	 * Iterator<ReplicatorType> it = this.collection.iterator();
	 * 
	 * while (it.hasNext()) { Replicator replicator = it.next();
	 * 
	 * cumulatedDistance += replicator.getGene()
	 * .calculateGeneticDistanceFromEve();
	 * 
	 * }
	 * 
	 * return (double) cumulatedDistance / (double) this.size(); }
	 */

}
