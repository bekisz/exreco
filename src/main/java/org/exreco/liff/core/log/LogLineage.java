package org.exreco.liff.core.log;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.log.DynamicLineageClassHibernateHelper;
import org.exreco.liff.core.DirectReplicationFitnessGene;
import org.exreco.liff.core.Replicator;
import org.exreco.liff.core.ReplicatorCollection;
import org.exreco.liff.core.World;

public class LogLineage extends LogBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6226775490106377220L;
	private static Logger logger = LogManager.getLogger(LogLineage.class
			.getName());
	private DynamicLineageClassHibernateHelper hibernateLogger;
	private String dynamicAnnotatedClassName;

	public void init() {
		super.init();
		this.hibernateLogger = new DynamicLineageClassHibernateHelper(dynamicAnnotatedClassName );
	}

	public DynamicLineageClassHibernateHelper getHibernateLogger() {
		return hibernateLogger;
	}

	public void setHibernateLogger(DynamicLineageClassHibernateHelper hibernateLogger) {
		this.hibernateLogger = hibernateLogger;
	}

	@Override
	public void log(World world) {
		try {

			Map<String, Object> logMap = new LinkedHashMap<String, Object>();

			for (String geneId : this.getSets()) {

				DirectReplicationFitnessGene gene = world.lookupGene(geneId);
				logMap.put("lineage", geneId);
				this.logMarkers(world, logMap);

				ReplicatorCollection<Replicator> replicators = gene
						.fetchReplicatorsInLineage();

				ReplicatorCollection<Replicator> replicatorCollection = new ReplicatorCollection<Replicator>(
						replicators);
				String paramName = "population";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, replicatorCollection.size());
				}
				paramName = "world-population";
				if (this.getParams().contains(paramName)) {
					logMap.put(paramName, world.getReplicators().size());
				}
				logRelativeReplicatorsMetrics(replicators,
						world.getReplicators(), logMap);
				paramName = "all-time-population";
				if (this.getParams().contains(paramName)) {
					long allTimePopulation = gene
							.fetchAllTimePopulationInLineage();
					logMap.put(paramName, allTimePopulation);
				}

				paramName = "speed-of-evolution-10";
				if (this.getParams().contains(paramName)) {
					int numberOfMutationsInlastTenTicks = gene
							.countNumberOfMutationsIn(10);
					logMap.put(paramName, numberOfMutationsInlastTenTicks);
				}

				paramName = "avg-gen-distance";
				if (this.getParams().contains(paramName)) {
					double avgGeneticDistance = gene
							.calculateAvergeGeneticDistanceFromDescendantReplicators();
					logMap.put(paramName, avgGeneticDistance);
				}
				int numberOfGenesInLineage = -1;
				paramName = "nr-genes-in-lineage";
				if (this.getParams().contains(paramName)) {
					List<DirectReplicationFitnessGene> genesInLineage = gene
							.fetchGenesInLineage();
					numberOfGenesInLineage = genesInLineage.size();
					logMap.put(paramName, numberOfGenesInLineage);
				}
				long nrGenesSuccesful = -1;
				paramName = "nr-successful-genes-in-lineage-10";
				if (this.getParams().contains(paramName)) {
					nrGenesSuccesful = gene.countSuccessfulGenesInLineage(10);
					logMap.put(paramName, nrGenesSuccesful);
				}
				paramName = "evolutionary-efficiency";
				if (this.getParams().contains(paramName)) {
					double evolutionaryEfficiency = gene
							.calculateEvolutionaryEfficiency();

					logMap.put(paramName, evolutionaryEfficiency);
				}
				this.logReplicatorCollection(replicators, logMap);

				paramName = "regional-spread";
				if (this.getParams().contains(paramName)) {
					double regionalSpread = gene.calculateRegionalSpread();

					logMap.put(paramName, regionalSpread);
				}
				this.logReplicatorCollection(replicators, logMap);

				// this.getTableLogger().getInsertable().insertRow(logMap);

				hibernateLogger.persist(logMap);
				logMap.clear();

			}

		} catch (Exception e) {
			logger.error("Failed to persist dynamic annotated class", e);

		}
	}

	public String getDynamicAnnotatedClassName() {
		return dynamicAnnotatedClassName;
	}

	public void setDynamicAnnotatedClassName(String dynamicAnnotatedClassName) {
		this.dynamicAnnotatedClassName = dynamicAnnotatedClassName;
	}
}
