package org.exreco.experiment.log;

import java.rmi.Remote;
import java.util.Map;

public interface Insertable extends Remote {
	public void insertRow(Map<String, ? super Object> row) throws Exception;
}
