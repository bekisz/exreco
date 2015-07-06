package org.exreco.experiment.log;

import java.io.Serializable;
import java.util.Map;

import org.exreco.experiment.util.events.LiffEvent;


public class CreateTableRowEvent extends LiffEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -865428791131447968L;
	final private Map<String, ? super Object> row;

	public CreateTableRowEvent(Map<String, ? super Object> row) {
		super();
		this.row = row;
	}

	/**
	 * @return the row
	 */
	public Map<String, ? super Object> getRow() {
		return row;
	}
}
