package org.exreco.experiment.jppf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jppf.client.JPPFJob;
import org.jppf.client.JPPFResultCollector;

public class ExperimentJppfResultCollector extends JPPFResultCollector {
	private static Logger logger = LogManager
			.getLogger(ExperimentJppfResultCollector.class.getName());

	/*
	public ExperimentJppfResultCollector() {
		super(

	}
	*/
	public ExperimentJppfResultCollector(JPPFJob job) {
		super(job);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jppf.client.JPPFResultCollector#onComplete()
	 */
	

}
