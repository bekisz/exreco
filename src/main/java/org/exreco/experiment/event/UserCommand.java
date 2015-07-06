package org.exreco.experiment.event;

import org.exreco.experiment.util.events.LiffEvent;

public class UserCommand extends LiffEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2289961690452612781L;
	private final long experimentId;

	public UserCommand(long experimentId) {
		super();
		this.experimentId = experimentId;
	}

	/**
	 * @return the experimentId
	 */
	public long getExperimentId() {
		return experimentId;
	}
}
