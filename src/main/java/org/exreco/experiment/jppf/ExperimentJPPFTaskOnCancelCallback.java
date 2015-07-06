package org.exreco.experiment.jppf;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.Case;
import org.jppf.node.protocol.Task;


public class ExperimentJPPFTaskOnCancelCallback extends
		ExperimentJPPFTaskCallback implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4548888243465574556L;
	private static Logger logger = LogManager
			.getLogger(ExperimentJPPFTaskOnCancelCallback.class.getName());

	@Override
	public void run() {
		Task<Object> task = this.getTask();
		Object wrappedRunnable = task.getTaskObject();
		Case expCase = (Case) wrappedRunnable;
		logger.debug("Case #" + expCase.getCaseId() + " was cancelled");

	}

}
