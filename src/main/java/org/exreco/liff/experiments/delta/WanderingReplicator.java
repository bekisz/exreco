package org.exreco.liff.experiments.delta;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.liff.core.Region;
import org.exreco.liff.core.RegionalReplicator;
import org.exreco.liff.core.Replicator;
import org.exreco.liff.core.Space;
import org.exreco.liff.core.WanderingReplicatorSpace;
import org.exreco.liff.core.Replicator.DestructedEvent;

public class WanderingReplicator extends RegionalReplicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8416257336143899835L;
	private static Logger logger = LogManager
			.getLogger(WanderingReplicator.class.getName());
	// private Region region;
	private double x;
	private double y;

	// private final DeltaGene gene;

	public WanderingReplicator(DeltaGene gene) {
		super(gene);
		// this.gene = gene;
		// this.setRegion(ancestor.getRegion());

	}

	public WanderingReplicator(WanderingReplicator ancestor) {
		super(ancestor);
		// this.gene = gene;
		// this.setRegion(ancestor.getRegion());
		this.setX(ancestor.getX());
		this.setY(ancestor.getY());

	}

	protected boolean checkDecay() {
		if (this.getGene().getMaxLifeTime() < this.getAge()) {
			return true;
		} else {
			return super.checkDecay();
		}

	}

	protected boolean checkMutation() {
		return this.getGene().getMutationRate() > this.getGene().getWorld()
				.getRandom().nextDouble()
				|| super.checkMutation();

	}

	public DeltaGene getGene() {
		return (DeltaGene) super.getGene();
	}

	public void mutate() {
		super.mutate();
		Random rand = this.getGene().getWorld().getRandom();

		double randomGaussian = rand.nextGaussian();
		Space space = this.getGene().getWorld().getSpace();
		WanderingReplicatorSpace wanderingSpace = (WanderingReplicatorSpace) space;
		double wandering = wanderingSpace.getWandering();
		double distance = randomGaussian * wandering;

		double randomUniform = rand.nextDouble();
		double angle = randomUniform * Math.PI * 2;
		this.moveByDistance(angle, distance);

		/*
		 * double wandering = this.getRegion().getWandering(); if (randomValue
		 * <= wandering) { int movesToInt = rand.nextInt(4); Region region =
		 * this.getRegion().getAdjacentRegions() .get(movesToInt);
		 * this.setRegion(region); }
		 */

	}

	private void transferToRegion(Region destinationRegion) {
		this.getRegion().getReplicators().remove(this);
		destinationRegion.getReplicators().add(this);
	}

	private double normalizeX(double x) {
		/*
		 * int multiplicator = 1; if (x < 0) { multiplicator = (int) (-x + 1); x
		 * += multiplicator; }
		 */
		double newX = x - Math.floor(x);
		return newX;
	}

	private double normalizeY(double y) {
		/*
		 * int multiplicator = 1; if (y < 0) { multiplicator = (int) (-y + 1); y
		 * += multiplicator; }
		 */
		double newY = y - Math.floor(y);
		return newY;
	}

	public void moveTo(double newX, double newY) {
		double newerX = this.normalizeX(newX);
		double newerY = this.normalizeY(newY);
		Region newRegion = this.getRegion(newerX, newerY);
		if (newRegion != this.getRegion()) {

			this.transferToRegion(newRegion);
		}
		this.setX(newerX);
		this.setY(newerY);

	}

	public void moveBy(double dx, double dy) {
		double newX = this.getX() + dx;
		double newY = this.getY() + dy;
		this.moveTo(newX, newY);

	}

	public void moveByDistance(double radian, double distance) {
		double dx = distance * Math.cos(radian);
		double dy = distance * Math.sin(radian);
		this.moveBy(dx, dy);

	}

	/**
	 * @return the region
	 */
	public Region getRegion() {

		return this.getRegion(this.getX(), this.getY());
	}

	/**
	 * @return the region
	 */
	public Region getRegion(double spaceX, double spaceY) {
		Space space = this.getGene().getWorld().getSpace();
		int x = (int) (spaceX * space.getMaxX());
		int y = (int) (spaceY * space.getMaxY());

		Region region = space.getRegions()[x][y];
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */

	/*
	 * public void setRegion(Region region) {
	 * 
	 * if (this.getRegion() != null) {
	 * this.getRegion().getReplicators().remove(this); } this.region = region;
	 * if (region != null) { region.getReplicators().add(this); }
	 * 
	 * }
	 */

	/**
	 * @return the x
	 */
	public double getX() {

		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(double x) {

		this.x = this.normalizeX(x);

	}

	/**
	 * @return the y
	 */
	public double getY() {

		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(double y) {
		this.y = this.normalizeY(y);

	}

	public boolean checkReplication() {
		return this.getRegion().getSelectedReplicators().contains(this);
	}

	public WanderingReplicator replicate() {
		WanderingReplicator newBornReplicator = (WanderingReplicator) this
				.getGene().createReplicator();
		// newBornReplicator.setGene(this.getGene());
		// newBornReplicator.setOperationalGene(this.getGene());
		// newBornReplicator.setAge(0);
		// newBornReplicator.setRegion(getRegion());
		newBornReplicator.setX(this.getX());
		newBornReplicator.setY(this.getY());

		this.getRegion().getReplicators().add(newBornReplicator);
		return newBornReplicator;
	}

	public void decay() {
		try {

			this.getEventSource().fireEvent(new DestructedEvent());
		} catch (Exception e) {
			logger.error("Exception caught", e);
		}

		boolean ok = this.getRegion().getReplicators().remove(this);
		if (!ok) {
			logger.warn("Replicator to be removed was not in the region list");
		}
		this.getRegion().getReplicators().remove(this);
		// this.setOperationalGene(null);
		this.setGene(null);

	}
}