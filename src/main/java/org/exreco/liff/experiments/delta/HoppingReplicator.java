package org.exreco.liff.experiments.delta;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.liff.core.HoppingReplicatorSpace;
import org.exreco.liff.core.Region;
import org.exreco.liff.core.RegionalReplicator;
import org.exreco.liff.core.Replicator;
import org.exreco.liff.core.Space;
import org.exreco.liff.core.WanderingReplicatorSpace;
import org.exreco.liff.core.Replicator.DestructedEvent;

public class HoppingReplicator extends RegionalReplicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8416257336143899835L;
	private static Logger logger = LogManager.getLogger(HoppingReplicator.class
			.getName());

	private Region region;

	// private final DeltaGene gene;

	public HoppingReplicator(DeltaGene gene) {
		super(gene);
		// this.gene = gene;
		// this.setRegion(ancestor.getRegion());

	}

	public HoppingReplicator(HoppingReplicator ancestor) {
		super(ancestor);
		this.setRegion(ancestor.getRegion());

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

		double randomValue = rand.nextDouble();
		Space space = this.getGene().getWorld().getSpace();
		HoppingReplicatorSpace hoppingSpace = (HoppingReplicatorSpace) space;
		double hoppingChance = hoppingSpace.getRegionHoppingChance();

		if (randomValue <= hoppingChance) {
			int movesToInt = rand.nextInt(4);
			Region region = this.getRegion().getAdjacentRegions()
					.get(movesToInt);
			this.setRegion(region);
		}

	}

	private static double normalize(double x) {

		double newX = x - Math.floor(x);
		return newX;
	}

	

	public void moveTo(double newX, double newY) {
 		Region newRegion = this.locateRegion(newX, newY);
		this.setRegion(newRegion);
		

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

 		return this.region;
	}

	/**
	 * @return the region
	 */

	protected Region locateRegion(double spaceX, double spaceY) {
		Space space = this.getGene().getWorld().getSpace();
		int x = (int) (normalize(spaceX) * space.getMaxX());
		int y = (int) (normalize(spaceY) * space.getMaxY());
		Region region = space.getRegions()[x][y];
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */

	public void setRegion(Region newRegion) {

		if (this.getRegion() != null) {
			this.getRegion().getReplicators().remove(this);
		}
		this.region = newRegion;
		if (this.region != null) {
			this.region.getReplicators().add(this);
		}


	}

	/**
	 * @return the x
	 */
	public double getX() {

		return (double) this.getRegion().getX()/ (double) this.getRegion().getSpace().getMaxX();
	}



	/**
	 * @return the y
	 */
	public double getY() {

                         		return (double) this.getRegion().getY()/ (double) this.getRegion().getSpace().getMaxY();
	}

	

	public boolean checkReplication() {
		return this.getRegion().getSelectedReplicators().contains(this);
	}

	public HoppingReplicator replicate() {
		HoppingReplicator newBornReplicator = (HoppingReplicator) this.getGene()
				.createReplicator();
		// newBornReplicator.setGene(this.getGene());
		// newBornReplicator.setOperationalGene(this.getGene());
		// newBornReplicator.setAge(0);
		// newBornReplicator.setRegion(getRegion());
 		newBornReplicator.moveTo(this.getX(), this.getY());

		//this.getRegion().getReplicators().add(newBornReplicator);
		return newBornReplicator;
	}

	public void decay() {
		try {
			this.getEventSource().fireEvent(new DestructedEvent());
		} catch (Exception e) {
			logger.error("Exception caught", e);
		}

		boolean ok = this.getRegion().getReplicators().remove(this);
		if( !ok) {
			logger.warn("Replicator to be removed was not in the region list");
		}
		this.setRegion(null);
		// this.setOperationalGene(null);
		this.setGene(null);

	}

}
