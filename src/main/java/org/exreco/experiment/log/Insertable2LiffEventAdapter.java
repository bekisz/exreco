package org.exreco.experiment.log;

import java.io.Serializable;
import java.util.Map;

import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;


public class Insertable2LiffEventAdapter implements Insertable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 345108772156652896L;
	final private LiffEventListener<LiffEvent> eventHandler;

	public Insertable2LiffEventAdapter(LiffEventListener<LiffEvent> eventHandler) {
		super();
		this.eventHandler = eventHandler;
	}

	@Override
	public void insertRow(Map<String, ? super Object> row) throws Exception {
		CreateTableRowEvent tableRowEvent = new CreateTableRowEvent(row);
		this.getEventHandler().eventOccurred(tableRowEvent);
	}

	/**
	 * @return the eventHandler
	 */
	public LiffEventListener<LiffEvent> getEventHandler() {
		return eventHandler;
	}

}
