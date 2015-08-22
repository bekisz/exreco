package org.exreco.experiment.log;

import java.io.Serializable;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.util.events.LiffEventListener;


public class LiffEvent2InsertableAdapter implements
		LiffEventListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3027762361486618023L;
	/**
	 * 
	 */
	private static Logger logger = LogManager
			.getLogger(LiffEvent2InsertableAdapter.class.getName());

	final private Insertable instertable;

	public LiffEvent2InsertableAdapter(Insertable proxied) {
		super();
		this.instertable = proxied;
	}

	protected LiffEvent2InsertableAdapter() {
		super();
		this.instertable = null;
	}

	@Override
	public void eventOccurred(Serializable event) throws Exception {
		if (event instanceof CreateTableRowEvent) {
			CreateTableRowEvent tableRowEvent = (CreateTableRowEvent) event;
			Map<String, ? super Object> map = tableRowEvent.getRow();
			this.getInsertable().insertRow(map);
		} else {
			logger.warn("Unhandled event type {}", event.getClass().getName());
		}
	}

	/**
	 * @return the instertable
	 */
	public Insertable getInsertable() {
		return instertable;
	}

}
