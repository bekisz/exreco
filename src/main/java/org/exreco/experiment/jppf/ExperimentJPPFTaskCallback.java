package org.exreco.experiment.jppf;

import java.io.Serializable;

import org.jppf.client.taskwrapper.JPPFTaskCallback;
/*JPPFTaskCallback<Object> */
public class ExperimentJPPFTaskCallback extends JPPFTaskCallback implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6914844478297208863L;

	@Override
	public void run() {
		// this.getTask().getTaskObject();

	}

}
