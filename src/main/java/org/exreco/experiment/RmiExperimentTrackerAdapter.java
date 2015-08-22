package org.exreco.experiment;

import java.io.Serializable;

import org.exreco.experiment.util.events.LiffEvent;
import org.exreco.experiment.util.events.LiffEventListener;


public class RmiExperimentTrackerAdapter implements LiffEventListener,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6229893550911003543L;
	final private ExperimentTracker experimentTracker;

	public RmiExperimentTrackerAdapter(ExperimentTracker experimentTracker) {
		super();
		this.experimentTracker = experimentTracker;
	}

	/**
	 * @return the experimentTracker
	 */
	public ExperimentTracker getCaseTracker() {
		return experimentTracker;
	}

	@Override
	public void eventOccurred(Serializable event) {
		if (event instanceof Case.Event) {
			Case.Event ourCaseEvent = (Case.Event) event;
			Case ourCase = ourCaseEvent.getCase();
			CaseStatusEvent status = ourCase.createStatus();

			try {
				this.getCaseTracker().onCaseUpdated(status);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
