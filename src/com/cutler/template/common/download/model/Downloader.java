package com.cutler.template.common.download.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Handler;
import android.widget.Toast;

import com.cutler.template.MainApplication;
import com.cutler.template.common.Config;
import com.cutler.template.common.download.DownloadManager.DownloadStates;
import com.cutler.template.common.download.DownloadManager.DownloadTypes;
import com.cutler.template.common.download.goal.DownloadObserver;
import com.cutler.template.common.download.goal.DownloadTaskListener;
import com.cutler.template.util.StorageUtils;

/**
 * 下载器类。 
 * 维护了一个下载队列，并提供了对该队列的各种操作方法。 
 * 考虑到下载队列中可能存在n个下载任务，因此本类继承于Thread。
 *
 */
public class Downloader extends Thread {
	private static final int MAX_TASK_COUNT = 100;
    private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;
    
	// 保存所有注册到本Manager中的观察者。
	private List<DownloadObserver> observers;
	// 下载列表。
	private List<DownloadTask> mDownloadingTaskList;
	private List<DownloadTask> mPausingTaskList;
	private Handler mHandler = new Handler(MainApplication.getInstance().getMainLooper());
    // 等待下载的队列。
	private TaskQueue mWatingTaskQueue;
	private boolean isRunning;

	public Downloader() {
		mWatingTaskQueue = new TaskQueue();
		observers = new CopyOnWriteArrayList<DownloadObserver>();
		mDownloadingTaskList = new CopyOnWriteArrayList<DownloadTask>();
		mPausingTaskList = new CopyOnWriteArrayList<DownloadTask>();
	}
	
	/**
	 * 启动下载器。
	 */
	public void startManage() {
        isRunning = true;
        this.start();
        loadUncompleteTasks();
    }
	
	/**
	 * 从本地数据库中加载上次未完成的下载任务。
	 */
	private void loadUncompleteTasks() {
		
	}

	@Override
    public void run() {
        while (isRunning) {
            DownloadTask task = mWatingTaskQueue.poll();
			if (task != null) {
				mDownloadingTaskList.add(task);
				task.execute();
			}
        }
    }
	
	/**
	 * 下载器是否正在运行。
	 * @return
	 */
	public boolean isRunning() {
        return isRunning;
    }
	
	/**
	 * 检查指定文件是否已经存在于下载队列中。
	 * @param file
	 * @return
	 */
	public boolean hasTask(DownloadFile file) {
        for (DownloadTask task : mDownloadingTaskList) {
        	if (task.getDownloadFile().getUrl().equals(file.getUrl())) {
                return true;
            }
        }
        for (int i = 0; i < mPausingTaskList.size(); i++) {
        	DownloadTask task = mPausingTaskList.get(i);
            if (task.getDownloadFile().getUrl().equals(file.getUrl())) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * 添加下载任务到下载队列中。
	 * @param url
	 */
	public void addTask(DownloadFile file) {
        if (!StorageUtils.isSdCardWrittenable()) {
            Toast.makeText(MainApplication.getInstance(), "未发现SD卡", Toast.LENGTH_LONG).show();
            return;
        }
        if (getTotalTaskCount() >= MAX_TASK_COUNT) {
            Toast.makeText(MainApplication.getInstance(), "任务列表已满", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            addTask(newDownloadTask(file));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * 返回当前下载队列中任务的总个数。
	 * @return
	 */
	public int getTotalTaskCount() {
        return getQueueTaskCount() + getDownloadingTaskCount() + getPausingTaskCount();
    }
	
	/**
     * Create a new download task with default config
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     */
    private DownloadTask newDownloadTask(DownloadFile file) throws MalformedURLException {
        DownloadTaskListener taskListener = new DownloadTaskListener() {
            public void onProgressChanged(DownloadTask task) {
            	// 通知所有观察者，数据已经改变。
            	Map<String,Object> params = new HashMap<String,Object>();
        		params.put(DownloadObserver.KEY_TYPE, DownloadStates.PROGRESS);
        		// params.put(DownloadObserver.KEY_SPEED, task.getDownloadSpeed() + "kbps | "
                //        + task.getDownloadSize() + " / " + task.getTotalSize());
        		params.put(DownloadObserver.KEY_TOTAL_SIZE, task.getTotalSize());
        		params.put(DownloadObserver.KEY_PROGRESS, task.getDownloadPercent());
        		params.put(DownloadObserver.KEY_FILE, task.getDownloadFile());
            	notifyObservers(params);
            }

            @Override
            public void onPreExecute(DownloadTask task) {
            	// 下载之前先将文件保存到本地数据库中。
            }

            @Override
            public void onFinished(DownloadTask task) {
                completeTask(task);
            }

            @Override
            public void onError(DownloadTask task, Throwable error) {
            	System.out.println("onError  "+mWatingTaskQueue.size()+","+task.getDownloadFile().getFileName());//TODO
            	onErrorTask(task, error);
            }

			@Override
			public void onCanceled(DownloadTask task) {
				onCanceledTask(task);
			}
			
			@Override
			public void onPaused(DownloadTask task) {
				onPausedTask(task);
			}

        };
        return new DownloadTask(file, StorageUtils.getDiskCacheDir(MainApplication.getInstance(), Config.CACHE_APK).getAbsolutePath(), taskListener);
    }
    
    /**
     * 将任务添加到等待下载的队列中。
     * @param task
     */
    private void addTask(DownloadTask task) {
        notifyAddTask(task.getDownloadFile());
        mWatingTaskQueue.offer(task);
        // 如果没有启动下载器，则此时启动。
        if (!this.isAlive()) {
            this.startManage();
        }
    }
    
    private void notifyAddTask(DownloadFile file) {
    	notifyAddTask(file, false);
    }

    private void notifyAddTask(DownloadFile file, boolean isInterrupt) {
    	// 通知所有观察者，数据已经改变。
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put(DownloadObserver.KEY_TYPE, DownloadTypes.ADD);
		params.put(DownloadObserver.KEY_IS_PAUSED, isInterrupt);
		params.put(DownloadObserver.KEY_FILE, file);
    	notifyObservers(params);
    }
    
    /**
	 * 添加一个观察者。
	 * @param observer
	 */
	public void addObserver(DownloadObserver observer) {
		if (observer != null) {
			observers.add(observer);
		}
	}
    
	/**
	 * 删除一个观察者。
	 * @param observer
	 */
	public void removeObserver(DownloadObserver observer) {
		if (observers != null) {
			observers.remove(observer);
		}
	}
	
    /**
     * 在主线程中，通知所有观察者数据已经改变。 
     */
    public void notifyObservers(final Map<String,Object> params){
    	if(observers != null){
    		mHandler.post(new Runnable() {
				public void run() {
					if(observers != null){
						// 通知所有观察者，数据已经改变。
		            	Iterator<DownloadObserver> iter = observers.iterator();
		            	while(iter.hasNext()){
		            		iter.next().onDownloadDataChanged(params);
		            	}
					}
				}
			});
    	}
    }
    
    /**
     * 暂停下载。
     * @param downloadFile
     */
    public synchronized void pauseTask(DownloadFile downloadFile) {
    	for (DownloadTask task : mDownloadingTaskList) {
    		if (task != null && task.getDownloadFile().getUrl().equals(downloadFile.getUrl())) {
                pauseTask(task);
            }
		}
	}
    
    /**
     * 暂停下载。
     * @param task
     */
    private synchronized void pauseTask(DownloadTask task) {
        if (task != null) {
            task.pause();
            // move to pausing list
            try {
            	mDownloadingTaskList.remove(task);
                task = newDownloadTask(task.getDownloadFile());
                mPausingTaskList.add(task);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 继续下载刚暂停的任务。
     * @param url
     */
    public synchronized void continueTask(DownloadFile downloadFile) {
    	for (DownloadTask task : mPausingTaskList) {
    		if (task != null && task.getDownloadFile().getUrl().equals(downloadFile.getUrl())) {
                continueTask(task);
            }
    	}
    }
    
    /**
     * 继续下载。
     * @param task
     */
    private synchronized void continueTask(DownloadTask task) {
        if (task != null) {
        	mPausingTaskList.remove(task);
            mWatingTaskQueue.offer(task);
        }
    }
	
    /**
     * 删除下载任务。
     * @param url
     */
    public synchronized void deleteTask(DownloadFile downloadFile, boolean deleteCache) {
        String url = downloadFile.getUrl();
        for (DownloadTask task : mDownloadingTaskList) {
        	if (task != null && task.getDownloadFile().getUrl().equals(url)) {
				if (deleteCache) {
					File file = new File(task.getDownloadFileLocalPath());
					file.delete();
					file = new File(task.getDownloadFileLocalTempPath());
					file.delete();
				}
                task.stop();
                onCanceledTask(task);
                return;
            }
        }
        for (int i = 0; i < mWatingTaskQueue.size(); i++) {
        	DownloadTask task = mWatingTaskQueue.get(i);
            if (task != null && task.getDownloadFile().getUrl().equals(url)) {
            	mWatingTaskQueue.remove(task);
            }
        }
        for (DownloadTask task : mPausingTaskList) {
        	if (task != null && task.getDownloadFile().getUrl().equals(url)) {
            	mPausingTaskList.remove(task);
            }
        }
    }
    
    /**
     * 停止所有下载任务，并终止下载器。
     */
    public void close() {
        isRunning = false;
        pauseAllTask();
        this.interrupt();
    }
    
    /**
     * 暂停所有正在下载、等待下载的任务。
     */
    public synchronized void pauseAllTask() {
        for (int i = 0; i < mWatingTaskQueue.size(); i++) {
        	DownloadTask task = mWatingTaskQueue.get(i);
            mWatingTaskQueue.remove(task);
            if(task != null) {
            	mPausingTaskList.add(task);
            }
        }
		for (DownloadTask task : mDownloadingTaskList) {
			if (task != null) {
                pauseTask(task);
            }
		}
    }
    
    /**
     * 文件下载完成。
     * @param task
     */
    public synchronized void completeTask(DownloadTask task) {
        if (mDownloadingTaskList.contains(task)) {
        	mDownloadingTaskList.remove(task);
        	// 通知所有观察者，数据已经改变。
        	Map<String,Object> params = new HashMap<String,Object>();
    		params.put(DownloadObserver.KEY_TYPE, DownloadTypes.COMPLETE);
    		params.put(DownloadObserver.KEY_FILE, task.getDownloadFile());
    		params.put(DownloadObserver.KEY_LOCAL_PATH, task.getDownloadFileLocalPath());
        	notifyObservers(params);
        }
    }
    
    /*
     * 取消文件下载。
     * @param task
     */
	private synchronized void onCanceledTask(DownloadTask task) {
		// 通知所有观察者，数据已经改变。
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put(DownloadObserver.KEY_TYPE, DownloadTypes.DELETE);
		params.put(DownloadObserver.KEY_FILE, task.getDownloadFile());
		params.put(DownloadObserver.KEY_LOCAL_PATH, task.getDownloadFileLocalPath());
    	notifyObservers(params);
	}
	
	/*
	 * 文件下载出错。
	 * @param task
	 * @param t
	 */
	private synchronized void onErrorTask(DownloadTask task, Throwable error) {
		if (error == null) {
			error = new RuntimeException("unkown error");
		}
    	// 通知所有观察者，数据已经改变。
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put(DownloadObserver.KEY_TYPE, DownloadStates.ERROR);
		params.put(DownloadObserver.KEY_FILE, task.getDownloadFile());
    	notifyObservers(params);
        Toast.makeText(MainApplication.getInstance(), "Error: \n" + error.getMessage(), Toast.LENGTH_LONG).show();
	} 
	
	/*
	 * 文件暂停下载。
	 */
	private synchronized void onPausedTask(DownloadTask task) {
		// 通知所有观察者，数据已经改变。
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put(DownloadObserver.KEY_TYPE, DownloadTypes.PAUSE);
		params.put(DownloadObserver.KEY_FILE, task.getDownloadFile());
		params.put(DownloadObserver.KEY_LOCAL_PATH, task.getDownloadFileLocalPath());
    	notifyObservers(params);
	}
    
	
	/**
	 * 删除本地的缓存文件。
	 * @param url
	 */
	public boolean deleteLocalFileByUrl(String url){
		try {
			URL localUrl = new URL(url);
			String fileName = new File(localUrl.getFile()).getName();
			File file = new File(StorageUtils.getDiskCacheDir(MainApplication.getInstance(), Config.CACHE_APK), fileName);
			return file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public int getQueueTaskCount() {
        return mWatingTaskQueue.size();
    }

    public int getDownloadingTaskCount() {
        return mDownloadingTaskList.size();
    }

    public int getPausingTaskCount() {
        return mPausingTaskList.size();
    }
	
    /*
     * 等待下载的任务队列。
     */
	private class TaskQueue {
        private Queue<DownloadTask> taskQueue;

        public TaskQueue() {
            taskQueue = new LinkedList<DownloadTask>();
        }

        public void offer(DownloadTask task) {
            taskQueue.offer(task);
        }

        public DownloadTask poll() {
            DownloadTask task = null;
            while (isRunning && (mDownloadingTaskList.size() >= MAX_DOWNLOAD_THREAD_COUNT
                    || (task = taskQueue.poll()) == null)) {
            	System.out.println("1秒后继续检测");//TODO
                try {
                    Thread.sleep(1000); // sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return task;
        }

        public DownloadTask get(int position) {
            if (position >= size()) {
                return null;
            }
            return ((LinkedList<DownloadTask>) taskQueue).get(position);
        }

        public int size() {
            return taskQueue.size();
        }

        @SuppressWarnings("unused")
        public boolean remove(int position) {
            return taskQueue.remove(get(position));
        }

        public boolean remove(DownloadTask task) {
            return taskQueue.remove(task);
        }
    }

}
