package org.exreco.experiment.jppf;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.Case;
import org.jppf.node.protocol.Task;



public class ExperimentJPPFTaskOnTimeOutCallback extends
		ExperimentJPPFTaskCallback implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7110434570467861750L;
	private static Logger logger = LogManager
			.getLogger(ExperimentJPPFTaskOnTimeOutCallback.class.getName());

	@Override
	public void run() {
		Task<Object> task = this.getTask();
		Object wrappedRunnable = task.getTaskObject();
		Case expCase = (Case) wrappedRunnable;
		logger.warn("Case #" + expCase.getCaseId() + " was time out'd");

	}

}
