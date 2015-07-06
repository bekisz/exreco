package org.exreco.experiment.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PausableThreadPoolExecutor extends ThreadPoolExecutor {
	private boolean isPaused;

	private final ReentrantLock pauseLock = new ReentrantLock();
	private final Condition unpaused = pauseLock.newCondition();

	/**
	 * @return the isPaused
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Creates a new {@code ThreadPoolExecutor} with the given initial
	 * parameters and default thread factory and rejected execution handler. It
	 * may be more convenient to use one of the {@link Executors} factory
	 * methods instead of this general purpose constructor.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are
	 *            idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param maximumPoolSize
	 *            the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 *            when the number of threads is greater than the core, this is
	 *            the maximum time that excess idle threads will wait for new
	 *            tasks before terminating.
	 * @param unit
	 *            the time unit for the {@code keepAliveTime} argument
	 * @param workQueue
	 *            the queue to use for holding tasks before they are executed.
	 *            This queue will hold only the {@code Runnable} tasks submitted
	 *            by the {@code execute} method.
	 * @throws IllegalArgumentException
	 *             if one of the following holds:<br>
	 *             {@code corePoolSize < 0}<br>
	 *             {@code keepAliveTime < 0}<br>
	 *             {@code maximumPoolSize <= 0}<br>
	 *             {@code maximumPoolSize < corePoolSize}
	 * @throws NullPointerException
	 *             if {@code workQueue} is null
	 */
	public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	/**
	 * Creates a new {@code ThreadPoolExecutor} with the given initial
	 * parameters and default rejected execution handler.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are
	 *            idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param maximumPoolSize
	 *            the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 *            when the number of threads is greater than the core, this is
	 *            the maximum time that excess idle threads will wait for new
	 *            tasks before terminating.
	 * @param unit
	 *            the time unit for the {@code keepAliveTime} argument
	 * @param workQueue
	 *            the queue to use for holding tasks before they are executed.
	 *            This queue will hold only the {@code Runnable} tasks submitted
	 *            by the {@code execute} method.
	 * @param threadFactory
	 *            the factory to use when the executor creates a new thread
	 * @throws IllegalArgumentException
	 *             if one of the following holds:<br>
	 *             {@code corePoolSize < 0}<br>
	 *             {@code keepAliveTime < 0}<br>
	 *             {@code maximumPoolSize <= 0}<br>
	 *             {@code maximumPoolSize < corePoolSize}
	 * @throws NullPointerException
	 *             if {@code workQueue} or {@code threadFactory} is null
	 */
	public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
	}

	/**
	 * Creates a new {@code ThreadPoolExecutor} with the given initial
	 * parameters and default thread factory.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are
	 *            idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param maximumPoolSize
	 *            the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 *            when the number of threads is greater than the core, this is
	 *            the maximum time that excess idle threads will wait for new
	 *            tasks before terminating.
	 * @param unit
	 *            the time unit for the {@code keepAliveTime} argument
	 * @param workQueue
	 *            the queue to use for holding tasks before they are executed.
	 *            This queue will hold only the {@code Runnable} tasks submitted
	 *            by the {@code execute} method.
	 * @param handler
	 *            the handler to use when execution is blocked because the
	 *            thread bounds and queue capacities are reached
	 * @throws IllegalArgumentException
	 *             if one of the following holds:<br>
	 *             {@code corePoolSize < 0}<br>
	 *             {@code keepAliveTime < 0}<br>
	 *             {@code maximumPoolSize <= 0}<br>
	 *             {@code maximumPoolSize < corePoolSize}
	 * @throws NullPointerException
	 *             if {@code workQueue} or {@code handler} is null
	 */
	public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				Executors.defaultThreadFactory(), handler);
	}

	/**
	 * Creates a new {@code ThreadPoolExecutor} with the given initial
	 * parameters.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are
	 *            idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param maximumPoolSize
	 *            the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 *            when the number of threads is greater than the core, this is
	 *            the maximum time that excess idle threads will wait for new
	 *            tasks before terminating.
	 * @param unit
	 *            the time unit for the {@code keepAliveTime} argument
	 * @param workQueue
	 *            the queue to use for holding tasks before they are executed.
	 *            This queue will hold only the {@code Runnable} tasks submitted
	 *            by the {@code execute} method.
	 * @param threadFactory
	 *            the factory to use when the executor creates a new thread
	 * @param handler
	 *            the handler to use when execution is blocked because the
	 *            thread bounds and queue capacities are reached
	 * @throws IllegalArgumentException
	 *             if one of the following holds:<br>
	 *             {@code corePoolSize < 0}<br>
	 *             {@code keepAliveTime < 0}<br>
	 *             {@code maximumPoolSize <= 0}<br>
	 *             {@code maximumPoolSize < corePoolSize}
	 * @throws NullPointerException
	 *             if {@code workQueue} or {@code threadFactory} or
	 *             {@code handler} is null
	 */
	public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		pauseLock.lock();
		try {
			while (isPaused)
				unpaused.await();
		} catch (InterruptedException ie) {
			t.interrupt();
		} finally {
			pauseLock.unlock();
		}
	}

	public void pause() {
		pauseLock.lock();
		try {
			isPaused = true;
		} finally {
			pauseLock.unlock();
		}
	}

	public void resume() {
		pauseLock.lock();
		try {
			isPaused = false;
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}
}