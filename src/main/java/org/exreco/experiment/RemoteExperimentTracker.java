package org.exreco.experiment;

import java.rmi.Remote;

public interface RemoteExperimentTracker extends ExperimentTracker, Remote {
	public void onCaseUpdated(CaseStatusEvent status) throws Exception;
}
