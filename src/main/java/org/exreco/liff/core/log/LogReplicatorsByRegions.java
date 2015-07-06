package org.exreco.liff.core.log;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.util.LiffUtils;
import org.exreco.liff.core.DirectReplicationFitnessGene;
import org.exreco.liff.core.Region;
import org.exreco.liff.core.Replicator;
import org.exreco.liff.core.World;


public class LogReplicatorsByRegions extends LogReplicators {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2838003890298775786L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(LogReplicatorsByRegions.class.getName());
	/**
	 * Logs marker data which positions this log : world dimensions, age,
	 * worldid
	 * 
	 * @param rowMap
	 * 
	 * @param logMap
	 */
	protected void logMarkers(Region region, Map<String, Object> rowMap) {
		World world = region.getSpace().getWorld();
		this.logMarkers(world, rowMap);
		rowMap.put("region-x", region.getX());
		rowMap.put("region-y", region.getY());
	}

	@Override
	public void log(World world) {
		try {
			for (int i = 0; i < world.getSpace().getMaxX(); i++) {
				for (int j = 0; j < world.getSpace().getMaxY(); j++) {
					Region region = world.getSpace().getRegions()[i][j];
					if (region != null) {

						Collection<Replicator> allReplicators = region
								.getReplicators();
						Map<String, Object> logMap = new LinkedHashMap<String, Object>();

						for (String geneId : this.getSets()) {
							DirectReplicationFitnessGene gene = world.lookupGene(geneId);

							Collection<Replicator> replicators = new LinkedList<Replicator>();

							LiffUtils.Filter<Replicator> filter = new LiffUtils.DescendantsFilter<Replicator>(
									gene);
							LiffUtils.copyElements(allReplicators, replicators,
									filter);
							this.logMarkers(region, logMap);
							logMap.put("lineage", geneId);
							this.logRelativeReplicatorsMetrics(replicators,
									allReplicators, logMap);

							this.logReplicatorCollection(replicators, logMap);
							this.getTableLogger().getInsertable()
									.insertRow(logMap);
							logMap.clear();

						}

					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
