package org.exreco.experiment.log;

import java.rmi.Remote;
import java.util.Map;

public class RemoteInsertableAdapter implements Insertable, Remote {

	final private Insertable insertable;

	public RemoteInsertableAdapter(Insertable insertable) {
		super();
		this.insertable = insertable;
	}

	@Override
	public void insertRow(Map<String, ? super Object> row) throws Exception {
		this.insertable.insertRow(row);
	}

	/**
	 * @return the insertable
	 */
	public Insertable getInsertable() {
		return insertable;
	}

}
