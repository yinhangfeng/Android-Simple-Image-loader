package com.damingdan.lib.imageloader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Process;

public class AntiRepeatTaskExecutor<K> implements RejectedExecutionHandler {
	
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.min(CPU_COUNT + 1, 3);
	private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;
	private static final int KEEP_ALIVE = 60;
	private static final int THREAD_PRIORITY = Process.THREAD_PRIORITY_BACKGROUND;

	private ThreadPoolExecutor executor;
	private ConcurrentHashMap<K, AntiRepeatTask<K>> submittedTasks
			= new ConcurrentHashMap<K, AntiRepeatTask<K>>(32, 0.75f, 8);
	
	private class InternalExecutor extends ThreadPoolExecutor {
		
		public InternalExecutor(int corePoolSize,
                int maximumPoolSize,
                long keepAliveTime,
                TimeUnit unit,
                BlockingQueue<Runnable> workQueue,
                ThreadFactory threadFactory,
                RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			AntiRepeatTaskExecutor.this.afterExecute((AntiRepeatTask<K>) r);
		}
		
		@Override
		protected void terminated() {
			super.terminated();
			AntiRepeatTaskExecutor.this.terminated();
		}
	}
	
	public static abstract class AntiRepeatTask<K> implements Runnable {
		
		@Override
		public void run() {
			
		}
		
		protected abstract K getTaskKey();
		
		final private void addRepeatTask(AntiRepeatTask<K> task) {
			
		}
		
		final private void runRepeatTask() {
			
		}
		
	}
	
	public AntiRepeatTaskExecutor() {
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		ThreadFactory threadFactory = new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			public Thread newThread(Runnable r) {
				return new Thread(r, "AntiRepeatTaskExecutor #"
						+ mCount.getAndIncrement()) {
					@Override
					public void run() {
						Process.setThreadPriority(THREAD_PRIORITY);
						super.run();
					}
				};
			}
		};
		executor = new InternalExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
				KEEP_ALIVE, TimeUnit.SECONDS, workQueue, threadFactory, this);
	}

	public void execute(K key, AntiRepeatTask<K> task) {
		AntiRepeatTask<K> oldTask = submittedTasks.putIfAbsent(key, task);
		if(oldTask == null) {
			executor.execute(task);
		} else {
			oldTask.addRepeatTask(task);//TODO
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		removeSubmittedTasks((AntiRepeatTask<K>) r);
		//TODO
	}
	
	private void removeSubmittedTasks(AntiRepeatTask<K> task) {
		K key = task.getTaskKey();
		submittedTasks.remove(key, task);
	}
	
	private void afterExecute(AntiRepeatTask<K> task) {
		try {
			task.runRepeatTask();
		} finally {
			removeSubmittedTasks(task);//TODO
		}
	}
	
	private void terminated() {
		submittedTasks.clear();
	}

}
