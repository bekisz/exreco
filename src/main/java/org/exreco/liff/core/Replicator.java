package org.exreco.liff.core;

import java.io.Serializable;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.AgeTracked;
import org.exreco.experiment.util.events.EventSource;
import org.exreco.experiment.util.events.LiffEvent;

import org.exreco.liff.experiments.delta.DeltaGene;

public abstract class Replicator implements Active, AgeTracked, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3367633942480818869L;
	private static Logger logger = LogManager.getLogger(Replicator.class
			.getName());

	public class Event extends LiffEvent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2587703651275730229L;

		/**
		 * @return the world
		 */
		public Replicator getReplicator() {
			return Replicator.this;
		}

	}

	public class CreatedEvent extends Event {

		/**
		 * 
		 */
		private static final long serialVersionUID = -9116832514716904458L;

	}

	public class DestructedEvent extends Event {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5745152679623649098L;

	}

	public class StartTickEvent extends Event {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5474260763248351134L;

	}

	public class EndTickEvent extends Event {

		/**
		 * 
		 */
		private static final long serialVersionUID = -890228077365271759L;

	}

	private long birthThick = Long.MIN_VALUE;
	private DirectReplicationFitnessGene gene;

	private final EventSource eventSource = new EventSource();

	public Replicator(DirectReplicationFitnessGene gene) {

		this.setGene(gene);
		// this.setOperationalGene(gene);
		this.setAge(0);
	}

	public Replicator(Replicator ancestor) {

		this.setGene(ancestor.getGene());
		// this.setOperationalGene(this.getGene());
		this.setAge(0);


	}

	public Replicator() {
	}

	public void init() {

	}

	public boolean isConstructed() {
		return (this.gene != null && this.birthThick != Long.MIN_VALUE);
	}

	@Override
	public long getAge() {
		return this.getGene().getWorld().getAge() - this.getBirthTick();
	}

	public void setAge(long newAge) {
		this.birthThick = this.getGene().getWorld().getAge() - newAge;
	}

	@Override
	public long getBirthTick() {

		return this.birthThick;
	}

	public DirectReplicationFitnessGene getGene() {
		return this.gene;
	}

	public void setGene(DirectReplicationFitnessGene gene) {
	
		if ( gene == null ) {
	//		logger.error("Replicator Gene can not be set to null");
	//		return;
		} 
		if (this.gene != null) {

			this.gene.unRegisterReplicatorInstance(this);
		}
		this.gene = gene;
		if (this.gene != null) {
			this.gene.registerReplicatorInstance(this);
		}
	}

	/*
	 * public DirectReplicationFitnessGene getOperationalGene() { return operationalGene; }
	 * 
	 * 
	 * public void setOperationalGene(DirectReplicationFitnessGene operationalGene) {
	 * 
	 * if (this.operationalGene != null) {
	 * 
	 * this.operationalGene.unRegisterReplicatorInstance(this); }
	 * this.operationalGene = operationalGene; if (this.operationalGene != null)
	 * { this.operationalGene.registerReplicatorInstance(this); } }
	 */

	// abstract protected Replicator createReplicator();

	public abstract Replicator replicate();
	public abstract void decay();


	public void mutate() {
		DirectReplicationFitnessGene mutant = this.getGene().mutate();
		this.setGene(mutant);
	}
	public abstract boolean checkReplication();


	protected boolean checkDecay() {
		if ((this.getGene().getSurvivalChance() < this.getGene().getWorld()
				.getRandom().nextDouble())) {
			return true;
		}
		return false;
	}

	protected boolean checkMutation() {
		return false;
	}

	@Override
	public void tick() {
		
		if (this.checkDecay()) {
			this.decay();
			return;

		}
		// Replication
		if (this.checkReplication()) {
			this.replicate();

		}

	
		if (this.checkMutation()) {
			this.mutate();

		}
	}


	public EventSource getEventSource() {
		return eventSource;
	}


}
