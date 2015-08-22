package org.exreco.experiment.log;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.Experiment;
import org.exreco.experiment.Exreco;

import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;


public class TableLogger implements LiffEventListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 342782624545538329L;




	static abstract public class Command implements
			LiffEventListener,
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7653528612152456431L;
		private TableLogger tableLogger;

		public void setTableLogger(TableLogger tableLogger) {
			this.tableLogger = tableLogger;
		}

		public void init(TableLogger tableLogger) {
			this.tableLogger = tableLogger;
		}

		@Override
		abstract public void eventOccurred(Serializable event);

		/**
		 * @return the tableLogger
		 */
		public TableLogger getTableLogger() {
			return tableLogger;
		}

	}

	private LiffEventListener eventHandler;

	private String name;
	private boolean rewrite;


	transient private Table table;
	private Insertable insertable;
	//private Session session;
	private static Logger logger = LogManager.getLogger(TableLogger.class
			.getName());

	public void init()  {

		

		// this.init();
		this.initEventHandlers();
		/*
		try {
			this.table = session.createTable(this.getName(), this.rewrite);
		} catch (Exception e) {
			logger.error("Could not create table {}", this.getName(), e);
		
		} */

	}
	public void finish() {
		// TableLogger.this.getExperiment().getExperimentEventHub().getListeners()
		// .remove(this);
		try {
			this.close();
		} catch (Exception e) {
			logger.warn("Could not close table {}", this.getName(), e);
	
		}
	}

	@Override
	public void eventOccurred(Serializable event) throws Exception {
		this.getEventMap().eventOccurred(event);

		if (event instanceof Experiment.ExperimentEnded) {
			this.finish();
		}
	}

	public void initEventHandlers() {

	}

	public void close() throws Exception {
		this.getTable().close();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * @return the eventMap
	 */
	public LiffEventListener getEventMap() {
		return eventHandler;
	}

	/**
	 * @return the insertable
	 */
	public Insertable getInsertable() {
		return insertable;
	}

	/**
	 * @param insertable
	 *            the insertable to set
	 */
	public void setInsertable(Insertable insertable) {
		this.insertable = insertable;
	}

	
	public LiffEventListener getEventHandler() {
		return eventHandler;
	}
	public void setEventHandler(LiffEventListener eventHandler) {
		this.eventHandler = eventHandler;
	}
	public boolean isRewrite() {
		return rewrite;
	}
	public void setRewrite(boolean rewrite) {
		this.rewrite = rewrite;
	}


}
