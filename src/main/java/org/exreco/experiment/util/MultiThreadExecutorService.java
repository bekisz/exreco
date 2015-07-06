package org.exreco.experiment.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadExecutorService extends ThreadPoolExecutor {
	static private int coreThreads = Runtime.getRuntime().availableProcessors();
	
	private static class ExperimentThreadFactory implements ThreadFactory {
		// private static final AtomicInteger poolNumber = new AtomicInteger(1);
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		protected ExperimentThreadFactory() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
					.getThreadGroup();
			// namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
			namePrefix = "Thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix
					+ threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.MIN_PRIORITY)
				t.setPriority(Thread.MIN_PRIORITY);
			return t;
		}
	}

	private static class BlockingQueuePut implements RejectedExecutionHandler {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			try {
				executor.getQueue().put(r);
			} catch (InterruptedException ie) {
				throw new RejectedExecutionException(ie);
			}
		}
	}

	public MultiThreadExecutorService() {

		super(
				coreThreads, coreThreads, 60L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(coreThreads),
				new ExperimentThreadFactory(),
				new BlockingQueuePut());
		

		
		this.prestartAllCoreThreads();
	}
	public MultiThreadExecutorService(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		
	}

}
