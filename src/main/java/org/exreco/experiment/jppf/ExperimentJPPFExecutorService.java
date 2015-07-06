package org.exreco.experiment.jppf;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jppf.client.JPPFClient;
import org.jppf.client.concurrent.JPPFExecutorService;

public class ExperimentJPPFExecutorService extends JPPFExecutorService {
	private static Logger logger = LogManager
			.getLogger(ExperimentJPPFExecutorService.class.getName());
	static private JPPFClient jppfClient = new JPPFClient();

	private final ArrayBlockingQueue<Runnable> activeCases;

	public ExperimentJPPFExecutorService(int batchSize) {
		super(jppfClient);
		this.activeCases = new ArrayBlockingQueue<Runnable>(batchSize);
		this.setBatchSize(batchSize);

	}

	/**
	 * @return the jppfClient
	 */
	public static JPPFClient getJppfClient() {
		return jppfClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jppf.client.concurrent.JPPFExecutorService#execute(java.lang.Runnable
	 * )
	 */
	@Override
	public void execute(Runnable command) {
		try {
			this.getActiveCases().put(command);
		} catch (InterruptedException e) {

			logger.debug("{} main thread was interrupted",
					JPPFExecutorService.class.getSimpleName());
		}
		super.execute(command);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jppf.client.concurrent.JPPFExecutorService#awaitTermination(long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return super.awaitTermination(timeout, unit);
	}

	/**
	 * @return the activeCases
	 */
	public ArrayBlockingQueue<Runnable> getActiveCases() {
		return activeCases;
	}

}
