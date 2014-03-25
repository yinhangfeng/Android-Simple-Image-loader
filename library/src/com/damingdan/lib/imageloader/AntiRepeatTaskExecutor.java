package com.damingdan.lib.imageloader;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Process;
import android.util.Log;

public class AntiRepeatTaskExecutor<K> {
	private static final String TAG = "AntiRepeatTaskExecutor";
	private static final boolean DEBUG = true;
	
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.min(CPU_COUNT + 1, 3);
	private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;
	private static final int KEEP_ALIVE = 60;
	private static final int THREAD_PRIORITY = Process.THREAD_PRIORITY_BACKGROUND;

	private ThreadPoolExecutor executor;
	private ConcurrentHashMap<K, AntiRepeatTask> submittedTasks
			= new ConcurrentHashMap<K, AntiRepeatTask>(32, 0.75f, 8);
	
	private class InternalExecutor extends ThreadPoolExecutor {
		
		public InternalExecutor(int corePoolSize,
                int maximumPoolSize,
                long keepAliveTime,
                TimeUnit unit,
                BlockingQueue<Runnable> workQueue,
                ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		}
		
		@Override
		protected void terminated() {
			super.terminated();
			if(DEBUG) Log.i(TAG, "terminated");
			AntiRepeatTaskExecutor.this.terminated();
		}
	}
	
	private class AntiRepeatTask implements Runnable {
		
		private K key;
		private Runnable firstTask;
		private volatile LinkedList<Runnable> repeatTaskQueue;
		private volatile boolean isRunning = true;
		private Object lock = new Object();
		
		public AntiRepeatTask(K key, Runnable firstTask) {
			this.key = key;
			this.firstTask = firstTask;
		}
		
		@Override
		public void run() {
			try {
				firstTask.run();
			} finally {
				firstTask = null;
				runRepeatTask();
			}
		}
		
		/**
		 * @return null 任务已结束 没有初始化repeatTaskQueue
		 */
		public LinkedList<Runnable> lazyGetRepeatTaskQueue() {
			if(repeatTaskQueue == null) {
				synchronized(lock) {
					if(isRunning && repeatTaskQueue == null) {
						repeatTaskQueue = new LinkedList<Runnable>();
					}
				}
			}
			return repeatTaskQueue;
		}
		
		/**
		 * 添加重复任务
		 * @return false 任务一结束 添加失败
		 */
		public boolean addRepeatTask(Runnable task) {
			if(DEBUG) Log.i(TAG, "addRepeatTask key=" + key);
			LinkedList<Runnable> repeatTaskQueue = lazyGetRepeatTaskQueue();
			if(repeatTaskQueue == null) {
				return false;
			}
			synchronized(lock) {
				if(!isRunning) {
					return false;
				}
				repeatTaskQueue.add(task);
			}
			return true;
		}
		
		/**
		 * 执行重复任务
		 */
		private void runRepeatTask() {
			if(DEBUG) Log.i(TAG, "runRepeatTask");
			synchronized(lock) {
				if(repeatTaskQueue == null) {
					isRunning = false;
					removeSubmittedTasks(key, this);
					return;
				}
			}
			LinkedList<Runnable> repeatTaskQueue = this.repeatTaskQueue;
			if(DEBUG) Log.i(TAG, "runRepeatTask repeatTaskQueue != null repeatTaskQueue.size=" + repeatTaskQueue.size());
			Runnable repeatTask;
			for(;;) {
				synchronized(lock) {
					repeatTask = repeatTaskQueue.poll();
					if(repeatTask == null) {
						isRunning = false;
						removeSubmittedTasks(key, this);
						return;
					}
				}
				repeatTask.run();
			}
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
				KEEP_ALIVE, TimeUnit.SECONDS, workQueue, threadFactory);
	}

	public void execute(K key, Runnable task) {
		if(key == null || task == null) {
			throw new NullPointerException();
		}
		AntiRepeatTask newTask = new AntiRepeatTask(key, task);
		AntiRepeatTask oldTask = submittedTasks.putIfAbsent(key, newTask);
		if(oldTask == null) {
			if(DEBUG) Log.i(TAG, "execute oldTask == null executor.execute(newTask) key=" + key);
			executor.execute(newTask);
		} else {
			for(;;) {
				if(oldTask.addRepeatTask(task)) {
					if(DEBUG) Log.i(TAG, "execute oldTask.addRepeatTask(task) == true key=" + key);
					break;
				}
				if((oldTask = submittedTasks.putIfAbsent(key, newTask)) == null) {
					if(DEBUG) Log.i(TAG, "execute (oldTask = submittedTasks.putIfAbsent(key, newTask)) == null executor.execute(newTask) key=" + key);
					executor.execute(newTask);
					break;
				}
			}
		}
	}
	
	private void removeSubmittedTasks(K key, AntiRepeatTask task) {
		submittedTasks.remove(key, task);
	}
	
	private void terminated() {
		submittedTasks.clear();
	}

	@Override
	public String toString() {
		return "AntiRepeatTaskExecutor [executor=" + executor
				+ ", submittedTasks.size=" + submittedTasks.size() + "]";
	}
}
