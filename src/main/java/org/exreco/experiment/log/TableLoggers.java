package org.exreco.experiment.log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;



public class TableLoggers  implements
		LiffEventListener<LiffEvent>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5468048636980915151L;
	transient private Session session;
	private Map<String, TableLogger> tableLoggerMap = new HashMap<String, TableLogger>();
	




	public void closeAllTables() throws Exception {
		Iterator<TableLogger> it = this.getTableLoggerMap().values().iterator();
		while (it.hasNext()) {
			it.next().close();
		}
		this.getTableLoggerMap().clear();
	}

	/**
	 * @return the sessions
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param sessions
	 *            the sessions to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public void eventOccurred(LiffEvent event) throws Exception {
		for (TableLogger logger : this.getTableLoggerMap().values()) {
			logger.eventOccurred(event);
		}
	}
	public Map<String, TableLogger> getTableLoggerMap() {
		return tableLoggerMap;
	}

	public void setTableLoggerMap(Map<String, TableLogger> tableLoggerMap) {
		this.tableLoggerMap = tableLoggerMap;
	}
}
