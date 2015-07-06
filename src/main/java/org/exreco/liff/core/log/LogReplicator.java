package org.exreco.liff.core.log;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.exreco.liff.core.RegionalReplicator;
import org.exreco.liff.core.Replicator;
import org.exreco.liff.core.ReplicatorCollection;
import org.exreco.liff.core.World;
import org.exreco.liff.experiments.delta.DeltaGene;


public class LogReplicator extends LogBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9117115979007338937L;

	@Override
	protected void logOneParameter(String rootParamName,
			ReplicatorCollection<Replicator>.Parameter<Double> param,
			Map<String, Object> logMap) {
		String paramName = rootParamName + ".min";
		if (this.getParams().contains(paramName)
				|| this.getParams().contains(rootParamName)) {
			logMap.put(paramName, param.min());
		}

		paramName = rootParamName + ".avg";
		if (this.getParams().contains(paramName)
				|| this.getParams().contains(rootParamName)) {
			logMap.put(paramName, param.average());
		}
		paramName = rootParamName + ".max";
		if (this.getParams().contains(paramName)
				|| this.getParams().contains(rootParamName)) {
			logMap.put(paramName, param.max());
		}
	}

	/**
	 * Logs one raw in the table It starts with the markers and then ads name of
	 * the replicator set, Finally puts all the date on the collections.
	 * 
	 * @param setName
	 * @param replicators
	 */
	@Override
	protected void logReplicatorCollection(Collection<Replicator> replicators,
			Map<String, Object> logMap) {
		// ------------

		ReplicatorCollection<Replicator> replicatorCollection = new ReplicatorCollection<Replicator>(
				replicators);
		String paramName = "population";
		if (this.getParams().contains(paramName)) {
			logMap.put(paramName, replicatorCollection.size());
		}
		// Replication fitness
		ReplicatorCollection<Replicator>.Parameter<Double> param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {

				return ((DeltaGene) replicator.getGene())
						.getReplicationFitness();
			}
		};

		this.logOneParameter("replicator-fitness", param, logMap);

		// Survival Chance
		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {

				return ((DeltaGene) replicator.getGene()).getSurvivalChance();
			}
		};
		this.logOneParameter("survival-chance", param, logMap);

		// Mutation Rate

		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {

				return ((DeltaGene) replicator.getGene()).getMutationRate();
			}
		};
		this.logOneParameter("mutation-rate", param, logMap);

		// Genetic Distance form Eve

		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {

				return (double) replicator.getGene()
						.calculateGeneticDistanceFromEve();
			}
		};
		this.logOneParameter("genetic-dist-to-Eve", param, logMap);

		// Genetic Distance to Eve

		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {

				return (double) replicator.getGene()
						.calculateGeneticDistanceFromEve();
			}
		};
		this.logOneParameter("genetic-dist-to-Eve", param, logMap);

		// Replicator Age

		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {

				return (double) replicator.getAge();
			}
		};
		this.logOneParameter("replicator-age", param, logMap);

		// Genetic diversity
		int geneticDiversity = replicatorCollection.calculateGeneticDiversity();

		logMap.put("genetic-diversity", geneticDiversity);

	}

	@Override
	public void log(World world) {
		try {
			Collection<Replicator> allReplictors = world.getSpace()
					.getRegions()[0][0].getReplicators();

			Map<String, Object> logMap = new LinkedHashMap<String, Object>();
			for (Replicator replicator : allReplictors) {
				RegionalReplicator regReplicator = (RegionalReplicator) replicator;
				this.logMarkers(world, logMap);
				String paramName;
				paramName = "hash-code";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, replicator.hashCode());
				}

				paramName = "region-x";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, regReplicator.getRegion().getX());
				}
				paramName = "region-y";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, regReplicator.getRegion().getY());
				}
				/*
				 * paramName = "regional-x"; if
				 * (this.getParams().contains(paramName)) {
				 * logMap.put(paramName, replicator.getX() -
				 * replicator.getRegion().getX()); } paramName = "regional-y";
				 * if (this.getParams().contains(paramName)) {
				 * logMap.put(paramName, replicator.getY() -
				 * replicator.getRegion().getY()); }
				 */
				paramName = "global-x";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, regReplicator.getX());
				}
				paramName = "global-y";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, regReplicator.getY());
				}

				paramName = "replicator-age";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, replicator.getAge());
				}
				paramName = "gene";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, replicator.getGene().toString());
				}
				paramName = "lineage";
				if (this.getParams().contains(paramName)) {
					String geneString = replicator.getGene().toString();
					int index = geneString.indexOf('.', 2);
					index = (index == -1) ? geneString.length() : index;
					String lineageString = geneString.substring(0, index);
					logMap.put(paramName, lineageString);
				}
				this.getTableLogger().getInsertable().insertRow(logMap);
				logMap.clear();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
