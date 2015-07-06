package org.exreco.liff.core;

import java.util.LinkedList;
import java.util.List;



public class Cell {
	
	private List<Replicator> replicators = new LinkedList<Replicator>();

	public List<Replicator> getReplicators() {
		return replicators;
	}

	public void setReplicators(List<Replicator> replicators) {
		this.replicators = replicators;
	}

}
