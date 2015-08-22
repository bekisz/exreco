package org.exreco.liff.core.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.Case;
import org.exreco.experiment.dim.DimensionValue;
import org.exreco.experiment.log.TableLogger;
import org.exreco.experiment.util.events.LiffEvent;

import org.exreco.liff.core.RegionalReplicator;
import org.exreco.liff.core.Replicator;
import org.exreco.liff.core.ReplicatorCollection;
import org.exreco.liff.core.World;
import org.exreco.liff.experiments.delta.DeltaGene;

public abstract class LogBase extends TableLogger.Command implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7017424638173864349L;
	private int sampling = 1;
	private String loggedSets;
	private String loggedParams;
	private String name;
	private final List<String> sets = new ArrayList<String>(7);
	private final List<String> params = new ArrayList<String>(7);
	@SuppressWarnings("unused")
	private static Logger logger = LogManager
			.getLogger(LogBase.class.getName());

	// private final Map<String, InfoSnippet> infoSnippetMap = new
	// LinkedHashMap<String, InfoSnippet>();

	public void init() {

		String smashedSets = this.getLoggedSets().trim();
		if (smashedSets != null && smashedSets.length() > 0) {
			String[] setsArray = smashedSets.split("[;, \t\n]+");
			for (int i = 0; i < setsArray.length; i++) {
				LogBase.this.getSets().add(setsArray[i]);
			}

		}
		String smashedParams = this.getLoggedParams().trim();
		if (smashedParams != null && smashedParams.length() > 0) {
			String[] setsArray = smashedParams.split("[;, \t\n]+");
			for (int i = 0; i < setsArray.length; i++) {
				LogBase.this.getParams().add(setsArray[i].trim());
			}

		}
	}

	/**
	 * @return the params
	 */
	public List<String> getParams() {
		return params;
	}

	/**
	 * @return the sets
	 */
	public List<String> getSets() {
		return sets;
	}

	/**
	 * @param tableLogger
	 *            the tableLogger to set
	 */

	@Override
	public void eventOccurred(Serializable event) {

		if (event instanceof World.Event) {
			Case.Event caseEvent = (Case.Event) event;
			World world = (World) caseEvent.getCase();

			if (this.getSampling() != 0
					&& world.getAge() % this.getSampling() == 0) {

				this.log(world);

			}

		}
	}

	abstract public void log(World world);

	/**
	 * Logs marker data which positions this log : world dimensions, age,
	 * worldid
	 * 
	 * @param logMap
	 */
	protected void logMarkers(World world, final Map<String, Object> logMap) {

		int worldId = world.getCaseId();
		int age = (int) world.getAge();
		long experimentId = (int) world.getExperimentId();
		logMap.put("world-id", worldId);
		logMap.put("exp-id", experimentId);

		logMap.put("world-age", age);

		// Adding the dimension actual values
		int i = 0;
		for (DimensionValue dimensionValue : world.getDimensionSetPoint()) {
			String key = "dim-" + i + "-"
					+ dimensionValue.getDimensionType().getName();
			i++;
			// Number value = dimensionValue.getValue();

			// TODO : Check if it Object can be used instead of String for map
			// value
			String stringValue = dimensionValue.getValueAsString();
			logMap.put(key, stringValue);
		}
	}

	protected void logRelativeReplicatorsMetrics(
			final Collection<Replicator> replicators,
			final Collection<Replicator> allReplicators,
			final Map<String, Object> logMap) {
		if (this.getParams().contains("rel-population")) {
			if (allReplicators.size() < replicators.size()) {
				logger.warn("Relative population is larger than 1. : {} / {}", replicators.size(),allReplicators.size());

			}
			if (allReplicators.size() != 0) {
				logMap.put("rel-population", (double) replicators.size()
						/ (double) allReplicators.size());
				
			} 	else {
				logger.warn("Base population is 0 at calculating relative population.");
				logMap.put("rel-population", (double) 0);
			}
		}

		// is-survivor
		if (this.getParams().contains("is-survivor")) {
			int isSurvivor = (replicators.size() > 0) ? 1 : 0;
			logMap.put("is-survivor", isSurvivor);
		}
		// is-exclusive-survivor
		if (this.getParams().contains("is-exclusive-survivor")) {
			int isExclusiveSurvivor = (replicators.size() == allReplicators
					.size()) ? 1 : 0;
			logMap.put("is-exclusive-survivor", isExclusiveSurvivor);
		}
		// is-dominant-50-percent
		if (this.getParams().contains("is-dominant-50-percent")) {
			int isDominant50 = (replicators.size() * 2 > allReplicators.size()) ? 1
					: 0;
			logMap.put("is-dominant-50-percent", isDominant50);
		}
		// is-dominant-95-percent
		if (this.getParams().contains("is-dominant-95-percent")) {
			int isDominant95 = (replicators.size() > 0.95 * allReplicators
					.size()) ? 1 : 0;
			logMap.put("is-dominant-95-percent", isDominant95);
		}
	}

	protected void logOneParameter(String rootParamName,
			ReplicatorCollection<Replicator>.Parameter<Double> param,
			Map<String, Object> logMap) {
		String paramName = rootParamName + "_min";
		if (this.getParams().contains(paramName)
		/* || this.getParams().contains(rootParamName) */) {
			logMap.put(paramName, param.min());
		}

		paramName = rootParamName + "_avg";
		if (this.getParams().contains(paramName)
		/* || this.getParams().contains(rootParamName) */) {
			logMap.put(paramName, param.average());
		}
		paramName = rootParamName + "_max";
		if (this.getParams().contains(paramName)
		/* || this.getParams().contains(rootParamName) */) {
			logMap.put(paramName, param.max());
		}
		paramName = rootParamName + "_sample-standard-deviation";

		if (this.getParams().contains(paramName)) {
			logMap.put(paramName, param.sampleStandardDeviation());
		}
		paramName = rootParamName + "_standard-deviation";

		if (this.getParams().contains(paramName)) {
			logMap.put(paramName, param.standardDeviation());
		}

	}

	/**
	 * Logs one raw in the table It starts with the markers and then ads name of
	 * the replicator set, Finally puts all the date on the collections.
	 * 
	 * @param setName
	 * @param replicators
	 */
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

		// Global X coordinate

		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {
				RegionalReplicator regReplicator = (RegionalReplicator) replicator;
				return (double) regReplicator.getX();
			}
		};
		this.logOneParameter("global-x", param, logMap);

		// Global Y coordinate

		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {
				RegionalReplicator regReplicator = (RegionalReplicator) replicator;
				return (double) regReplicator.getY();
			}
		};
		this.logOneParameter("global-y", param, logMap);

		// Region X coordinate

		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {
				RegionalReplicator regReplicator = (RegionalReplicator) replicator;
				return (double) regReplicator.getRegion().getX();
			}
		};
		this.logOneParameter("region-x", param, logMap);

		// Region Y coordinate

		param = replicatorCollection.new Parameter<Double>() {

			@Override
			public Double getNumber(Replicator replicator) {
				RegionalReplicator regReplicator = (RegionalReplicator) replicator;
				return (double) regReplicator.getRegion().getY();
			}
		};
		this.logOneParameter("region-y", param, logMap);

		// Genetic diversity
		int geneticDiversity = replicatorCollection.calculateGeneticDiversity();

		logMap.put("genetic-diversity", geneticDiversity);

	}

	@Override
	public void init(TableLogger tableLogger) {

		super.init(tableLogger);

		/*
		 * for (String paramName : this.getParams()) { InfoSnippet infoSnippet;
		 * 
		 * 
		 * this.getInfoSnippetMap().put(paramName, infoSnippet);
		 * this.getTableLogger().getHeader() .put(paramName,
		 * infoSnippet.getType()); }
		 */
	}

	/**
	 * @return the sampling
	 */
	public int getSampling() {
		return sampling;
	}

	/**
	 * @param sampling
	 *            the sampling to set
	 */
	public void setSampling(int sampling) {
		this.sampling = sampling;
	}

	public String getLoggedSets() {
		return loggedSets;
	}

	public void setLoggedSets(String loggedSets) {
		this.loggedSets = loggedSets;
	}

	public String getLoggedParams() {
		return loggedParams;
	}

	public void setLoggedParams(String loggedParams) {
		this.loggedParams = loggedParams;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the infoSnippetList
	 */
	/*
	 * public Map<String, InfoSnippet> getInfoSnippetMap() { return
	 * this.infoSnippetMap; }
	 */
}
