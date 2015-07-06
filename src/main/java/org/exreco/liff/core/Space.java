package org.exreco.liff.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class Space implements Active, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7302229964562401335L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(Space.class.getName());

	private Region[][] regions;
	private List<Region> regionList;

	private World world;
	private int maxX;

	private int maxY;
	private int squareSide;
	public int getSquareSide() {
		return squareSide;
	}

	public void setSquareSide(int squareSide) {
		this.squareSide = squareSide;
	}

	private int maxPopulation;
	

	public Space() {

	}

	public Space(World world) {
		this.world = world;
	}





	/*
	 * protected void initRegions() { Space.this.regions = new
	 * Region[Space.this.getMaxX()][Space.this .getMaxX()]; for (int i = 0; i <
	 * Space.this.getMaxX(); i++) { for (int j = 0; j < Space.this.getMaxY();
	 * j++) { Region region = Space.this.createRegion(); region.init(Space.this,
	 * i, j);
	 * 
	 * Space.this.getRegions()[i][j] = region;
	 * 
	 * } }
	 * 
	 * }
	 */
	protected Region createRegion() {
		Region region = new Region();
		return region;

	}

	public Region[][] getRegions() {
		return regions;
	}

	public World getWorld() {
		return world;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
 		return maxY;
	}

	public void setRegions(Region[][] regions) {
		this.regions = regions;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	@Override
	public void tick() {
		for (Region region : this.getRegionList()) {

			region.tick();
		}

	}

	/**
	 * @return the regionList
	 */
	public List<Region> getRegionList() {
		return regionList;
	}

	/**
	 * @param regionList
	 *            the regionList to set
	 */
	public void setRegionList(List<Region> regionList) {
		this.regionList = regionList;
	}

	/**
	 * @param maxPopulation
	 *            the maxPopulation to set
	 */
	public void setMaxPopulation(int maxPopulation) {
		this.maxPopulation = maxPopulation;
	}

	public int getMaxPopulation() {

		return this.maxPopulation;
	}

	abstract public void init();



}
