package org.exreco.experiment.jppf;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exreco.experiment.CaseShellIf;
import org.exreco.experiment.util.StopWatch;
import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.node.protocol.AbstractTask;
import org.jppf.node.protocol.Task;



public class MultiNodeExecutorService implements ExecutorService {

	private static Logger logger = LogManager
			.getLogger(MultiNodeExecutorService.class.getName());
	private final JPPFClient client = new JPPFClient();

	private JPPFJob currentJob;
	private JPPFJob submittedJob;
	private long batchTimeout;
	private int batchSize;
	private final StopWatch stopWatch = new StopWatch();
	private int jobCounter = 0;

	@Override
	public void execute(Runnable command) {
		if (this.getCurrentJob() == null) {
			this.jobCounter++;
			String name = "Exreco Job " + this.jobCounter;
			this.currentJob = new JPPFJob(name);
		}
		CaseShellIf  myCase = (CaseShellIf) command;
		AbstractTask<String> myTask = new JppfTask2CaseAdapter(myCase);
		// JPPF 3.3.7 : JPPFTask myTask = new JppfTask2CaseAdapter(myCase);
		try {
			// JPPF 3.3.7 : this.getCurrentJob().addTask(myTask);
			this.getCurrentJob().add(myTask);
		} catch (JPPFException e) {
			logger.warn("Coul not add case {} to a job", myCase.getCaseId());
			// e.printStackTrace();
		}
		if (currentJob.getJobTasks().size() == this.getBatchSize()
		// JPPF 3.3.7 : if (currentJob.getTasks().size() == this.getBatchSize()
				|| this.stopWatch.isOver(this.getBatchTimeout())) {
			List<Task<?>> results = this.submitJob();
			// JPPF 3.3.7 : List<JPPFTask> results = this.submitJob();
			this.processResults(results);
		}
	}
	protected void processResults(List<Task<?>> results) {
	// JPPF 3.3.7 : protected void processResults(List<JPPFTask> results) {
		for (Task<?> t : results) {
			AbstractTask<String> task = (AbstractTask<String>) t;
			// JPPF 3.3.7 : JPPFTask task = (JPPFTask) t;
			Exception ex = task.getException();
			if (ex != null) {
				logger.warn(
						"Task " + task.getId() + " returned with exception\n "+ ex.getMessage() + " ", ex);
	
			} else {
				logger.debug("Task Result : " + task.getResult());
			}

		}

	}
	protected List<Task<?>> submitJob()  {
	// JPPF 3.3.7 :  protected List<JPPFTask> submitJob() {

		List<Task<?>> results = null;
		// JPPF 3.3.7 : List<JPPFTask> results = null;
		try {
			if (this.getCurrentJob() != null) {
				results = this.getClient().submitJob(this.getCurrentJob());
				// JPPF 3.3.7 : results = this.getClient().submit(this.getCurrentJob());

				this.stopWatch.reset();
				logger.debug("Job {} submitted.", this.getCurrentJob()
						.getName());
				this.setSubmittedJob(this.getCurrentJob());
				this.setCurrentJob(null);
			}
		} catch (Exception e) {
			logger.warn("Could not submit job {}.", this.getSubmittedJob()
					.getName(), e);
		}
		return results;
	}

	@Override
	public void shutdown() {
		this.submitJob();
	}

	@Override
	public List<Runnable> shutdownNow() {

		return null;
	}

	@Override
	public boolean isShutdown() {

		return true;
	}

	@Override
	public boolean isTerminated() {

		return true;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		this.submitJob();
		return true;
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {

		return null;
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {

		return null;
	}

	@Override
	public Future<?> submit(Runnable task) {

		return null;
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {

		return null;
	}

	@Override
	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {

		return null;
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {

		return null;
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {

		return null;
	}

	/**
	 * @return the client
	 */
	public JPPFClient getClient() {
		return client;
	}

	/**
	 * @return the currentJob
	 */
	public JPPFJob getCurrentJob() {
		return currentJob;
	}

	/**
	 * @param currentJob
	 *            the currentJob to set
	 */
	public void setCurrentJob(JPPFJob currentJob) {
		this.currentJob = currentJob;
	}

	/**
	 * @return the batchTimeout
	 */
	public long getBatchTimeout() {
		return batchTimeout;
	}

	/**
	 * @param batchTimeout
	 *            the batchTimeout to set
	 */
	public void setBatchTimeout(long batchTimeout) {
		this.batchTimeout = batchTimeout;
	}

	/**
	 * @return the batchSize
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * @param batchSize
	 *            the batchSize to set
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * @return the submittedJob
	 */
	public JPPFJob getSubmittedJob() {
		return submittedJob;
	}

	/**
	 * @param submittedJob
	 *            the submittedJob to set
	 */
	public void setSubmittedJob(JPPFJob submittedJob) {
		this.submittedJob = submittedJob;
	}

}
