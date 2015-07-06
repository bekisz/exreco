package org.exreco.liff.core.log;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.util.LiffUtils;
import org.exreco.liff.core.DirectReplicationFitnessGene;
import org.exreco.liff.core.Replicator;

import org.exreco.liff.core.World;


public class LogReplicators extends LogBase {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(LogReplicators.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 4089695531403438604L;

	@Override
	public void log(World world) {
		try {
			Collection<Replicator> allReplicators = world.getReplicators();
			Map<String, Object> logMap = new LinkedHashMap<String, Object>();

			for (String geneId : this.getSets()) {
				DirectReplicationFitnessGene gene = world.lookupGene(geneId);

				Collection<Replicator> replicators = new LinkedList<Replicator>();

				LiffUtils.Filter<Replicator> filter = new LiffUtils.DescendantsFilter<Replicator>(
						gene);
				LiffUtils.copyElements(allReplicators, replicators, filter);
				this.logMarkers(world, logMap);
				logMap.put("lineage", geneId);
				this.logRelativeReplicatorsMetrics(replicators, allReplicators,
						logMap);
				this.logReplicatorCollection(replicators, logMap);
				this.getTableLogger().getInsertable().insertRow(logMap);
				logMap.clear();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
