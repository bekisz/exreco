package org.exreco.experiment;

import java.rmi.Remote;


public interface ExperimentTrackerInput extends Remote {

	public void onCaseUpdated(CaseStatusEvent status) throws Exception;

}
