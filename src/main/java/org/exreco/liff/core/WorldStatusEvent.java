package org.exreco.liff.core;

import java.io.Serializable;

import javax.persistence.Entity;

import org.exreco.experiment.CaseStatusEvent;
@Entity
public class WorldStatusEvent extends CaseStatusEvent implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 425232035825780652L;

	private long age;

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(long age) {
		this.age = age;
	}
	/**
	 * 
	 */

	private final int maxLifeTime;
	private final int maxPopulation;
	private final int actualPopulation;

	public WorldStatusEvent(World sourceWorld) {
		super(sourceWorld);
		this.age = sourceWorld.getAge();
		this.maxLifeTime = sourceWorld.getMaxLifeTime();
		this.actualPopulation = sourceWorld.getReplicators().size();
		this.maxPopulation = sourceWorld.getMaxPopulation();
	}

	/**
	 * @return the age
	 */
	public long getAge() {
		return age;
	}

	/**
	 * @return the maxLifeTime
	 */
	public int getMaxLifeTime() {
		return maxLifeTime;
	}

	/**
	 * @return the maxPopulation
	 */
	public int getMaxPopulation() {
		return maxPopulation;
	}

	/**
	 * @return the actualPopulation
	 */
	public int getActualPopulation() {
		return actualPopulation;
	}

}