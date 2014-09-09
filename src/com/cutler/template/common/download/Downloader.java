package com.cutler.template.common.download;

import java.net.MalformedURLException;
import java.util.ArrayList;
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
import com.cutler.template.common.download.goal.DownloadObserver;
import com.cutler.template.common.download.goal.DownloadTaskListener;
import com.cutler.template.common.download.model.DownloadFile;
import com.cutler.template.common.manager.ModelCallback;
import com.cutler.template.common.manager.Observer;
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
		// TODO Auto-generated method stub
	}

	@Override
    public void run() {
        while (isRunning) {
            DownloadTask task = mWatingTaskQueue.poll();
            mDownloadingTaskList.add(task);
            task.execute();
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
        DownloadTask task;
        for (int i = 0; i < mDownloadingTaskList.size(); i++) {
            task = mDownloadingTaskList.get(i);
            if (task.getDownloadFile().getUrl().equals(file.getUrl())) {
                return true;
            }
        }
        for (int i = 0; i < mPausingTaskList.size(); i++) {
            task = mPausingTaskList.get(i);
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
     * Create a new download task with default config
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     */
    private DownloadTask newDownloadTask(DownloadFile file) throws MalformedURLException {
        DownloadTaskListener taskListener = new DownloadTaskListener() {
            @Override
            public void updateProgress(DownloadTask task) {
            	// 通知所有观察者，数据已经改变。
            	Map<String,Object> params = new HashMap<String,Object>();
        		params.put(DownloadObserver.KEY_TYPE, Config.DownloadTypes.PROGRESS);
        		params.put(DownloadObserver.KEY_SPEED, task.getDownloadSpeed() + "kbps | "
                        + task.getDownloadSize() + " / " + task.getTotalSize());
        		params.put(DownloadObserver.KEY_PROGRESS, task.getDownloadPercent());
        		params.put(DownloadObserver.KEY_FILE, task.getDownloadFile());
            	notifyObservers(params);
            }

            @Override
            public void preDownload(DownloadTask task) {
            	// 下载之前先将文件保存到本地数据库中。
                // ConfigUtils.storeURL(mContext, mDownloadingTasks.indexOf(task), task.getUrl()); TODO
            }

            @Override
            public void finishDownload(DownloadTask task) {
                completeTask(task);
            }

            @Override
            public void errorDownload(DownloadTask task, Throwable error) {
                if (error != null) {
                    Toast.makeText(MainApplication.getInstance(), "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        };
        return new DownloadTask(file, StorageUtils.FILE_ROOT, taskListener);
    }
    
    /**
     * 将任务添加到等待下载的队列中。
     * @param task
     */
    private void addTask(DownloadTask task) {
        notifyAddTask(task.getDownloadFile());
        mWatingTaskQueue.offer(task);
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
		params.put(DownloadObserver.KEY_TYPE, Config.DownloadTypes.ADD);
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
     * 通知所有观察者数据已经改变。 
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
     * 文件下载完成。
     * @param task
     */
    public synchronized void completeTask(DownloadTask task) {
        if (mDownloadingTaskList.contains(task)) {
        	mDownloadingTaskList.remove(task);
        	// 通知所有观察者，数据已经改变。
        	Map<String,Object> params = new HashMap<String,Object>();
    		params.put(DownloadObserver.KEY_TYPE, Config.DownloadTypes.COMPLETE);
    		params.put(DownloadObserver.KEY_FILE, task.getDownloadFile());
    		params.put(DownloadObserver.KEY_LOCAL_PATH, task.getDownloadFileLocalPath());
        	notifyObservers(params);
        }
    }
	
	/**
	 * 返回当前下载队列中任务的总个数。
	 * @return
	 */
	public int getTotalTaskCount() {
        return getQueueTaskCount() + getDownloadingTaskCount() + getPausingTaskCount();
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
            while (mDownloadingTaskList.size() >= MAX_DOWNLOAD_THREAD_COUNT
                    || (task = taskQueue.poll()) == null) {
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
