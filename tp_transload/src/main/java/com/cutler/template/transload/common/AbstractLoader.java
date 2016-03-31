package com.cutler.template.transload.common;

import android.widget.Toast;

import com.cutler.template.base.Template;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 传输器类。
 * 维护了一个传输队列，并提供了对该队列的各种操作方法。
 * 考虑到传输队列中可能存在n个下载任务，因此本类继承于Thread。
 *
 * @author cutler
 */
public abstract class AbstractLoader extends Thread {
    // 正在执行 + 等待执行 + 暂停执行 <= 100
    private int maxTaskCount = 100;
    // 正在执行的最大任务数量
    private int maxThreadCount = 3;
    // 所有注册到本loader中的观察者。
    private List<TransferObserver> observers;
    // 正在执行列表。
    private List<TransferableTask> mDoingTaskList;
    // 暂停执行列表。
    private List<TransferableTask> mPausingTaskList;
    // 等待执行队列。
    private TaskQueue mWaitingTaskQueue;
    // 此列表中保存的任务是： 传输完成、传输出错、取消传输
    private List<TransferableTask> mOtherTaskList;
    // 标识当前Loader是否正在运行。
    private boolean isRunning;

    public AbstractLoader() {
        observers = new CopyOnWriteArrayList<TransferObserver>();
        mWaitingTaskQueue = new TaskQueue();
        mDoingTaskList = new CopyOnWriteArrayList<TransferableTask>();
        mPausingTaskList = new CopyOnWriteArrayList<TransferableTask>();
        mOtherTaskList = new CopyOnWriteArrayList<TransferableTask>();
    }

    public AbstractLoader(int maxTaskCount, int maxThreadCount) {
        this();
        if (maxTaskCount >= 1) {
            this.maxTaskCount = maxTaskCount;
        }
        if (maxThreadCount >= 1) {
            this.maxThreadCount = maxThreadCount;
        }
    }

    /**
     * 当前Loader是否正在运行。
     *
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 启动Loader。
     */
    public void startLoader() {
        onPreStartLoader();
        isRunning = true;
        start();
    }

    /**
     * 终止当前Loader。
     */
    public void stopLoader() {
        onPreStopLoader();
        isRunning = false;
        pauseAllTransferableTask();
    }

    /**
     * 停止当前Loader之前调用此方法，子类可以重写它，执行一些收尾操作。
     */
    protected void onPreStopLoader() {
    }

    /**
     * 启动当前Loader之前调用此方法，子类可以重写它，执行一些初始化操作。
     */
    protected void onPreStartLoader() {
    }

    @Override
    public void run() {
        while (isRunning) {
            TransferableTask task = mWaitingTaskQueue.poll();
            if (task != null) {
                mDoingTaskList.add(task);
                task.startTask();
            }
        }
    }

    /**
     * 检查指定文件是否已经存在于任务队列中。
     *
     * @param file
     * @return
     */
    public boolean hasTransferableFile(TransferableFile file) {
        for (TransferableTask task : mDoingTaskList) {
            if (task.getTransferableFile().getUniqueNumber().equals(file.getUniqueNumber())) {
                return true;
            }
        }
        for (TransferableTask task : mPausingTaskList) {
            if (task.getTransferableFile().getUniqueNumber().equals(file.getUniqueNumber())) {
                return true;
            }
        }
        for (int i = 0; i < mWaitingTaskQueue.size(); i++) {
            TransferableTask task = mWaitingTaskQueue.get(i);
            if (task.getTransferableFile().getUniqueNumber().equals(file.getUniqueNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加任务到队列中。
     *
     * @param file
     */
    public void addTransferableFile(TransferableFile file) {
        if (!verifyTransferableFileForAdd(file)) {
            return;
        }
        if (getTotalTaskCount() >= maxTaskCount) {
            Toast.makeText(Template.getApplication(), "任务列表已满，拒绝继续添加", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            addTransferableTask(createTransferableTask(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 任务添加到等待队列中。
     */
    private void addTransferableTask(TransferableTask task) {
        mWaitingTaskQueue.offer(task);
        // 如果没有启动下载器，则此时启动。
        if (!this.isAlive()) {
            startLoader();
        }
        onTransferableFileAddedTask(task.getTransferableFile());
    }

    /**
     * 验证即将被添加到队列中的文件是否符合规范。
     *
     * @return
     */
    protected abstract boolean verifyTransferableFileForAdd(TransferableFile file);

    /**
     * 创建一个新的任务。
     *
     * @param file
     * @return
     */
    protected abstract TransferableTask createTransferableTask(TransferableFile file) throws Exception;

    /**
     * 当TransferableFile被加入到等待队列中时回调此方法。
     *
     * @param file
     */
    protected abstract void onTransferableFileAddedTask(TransferableFile file);

    /**
     * 暂停文件传输。
     *
     * @param file
     */
    public synchronized void pauseTransferableFile(TransferableFile file) {
        for (TransferableTask task : mDoingTaskList) {
            if (task.getTransferableFile().getUniqueNumber().equals(file.getUniqueNumber())) {
                pauseTransferableTask(task);
            }
        }
    }

    /**
     * 暂停指定的TransferableTask。
     *
     * @param task
     */
    protected synchronized void pauseTransferableTask(TransferableTask task) {
        if (task != null) {
            task.pauseTask();
            // move to pausing list
            try {
                mDoingTaskList.remove(task);
                task = createTransferableTask(task.getTransferableFile());
                mPausingTaskList.add(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 继续刚被暂停的文件。
     *
     * @param file
     */
    public synchronized void continueTransferableFile(TransferableFile file) {
        for (TransferableTask task : mPausingTaskList) {
            if (task.getTransferableFile().getUniqueNumber().equals(file.getUniqueNumber())) {
                continueTransferableTask(task);
            }
        }
    }

    /**
     * 继续传输指定的TransferableTask。
     *
     * @param task
     */
    protected synchronized void continueTransferableTask(TransferableTask task) {
        if (task != null) {
            mPausingTaskList.remove(task);
            mWaitingTaskQueue.offer(task);
        }
    }

    /**
     * 暂停所有TransferableTask（包括正在执行的和等待执行的）
     */
    public synchronized void pauseAllTransferableTask() {
        for (int i = 0; i < mWaitingTaskQueue.size(); i++) {
            TransferableTask task = mWaitingTaskQueue.get(i);
            mWaitingTaskQueue.remove(task);
            if (task != null) {
                mPausingTaskList.add(task);
            }
        }
        for (TransferableTask task : mDoingTaskList) {
            if (task != null) {
                pauseTransferableTask(task);
            }
        }
    }

    /**
     * 立刻停止文件传输
     *
     * @param file
     */
    public synchronized void deleteTransferableFile(TransferableFile file) {
        String uniqueNumber = file.getUniqueNumber();
        TransferableTask findTask = null;
        for (TransferableTask task : mDoingTaskList) {
            if (task != null && task.getTransferableFile().getUniqueNumber().equals(uniqueNumber)) {
                task.stopTask();
                findTask = task;
                break;
            }
        }
        // 若要删除的TransferableFile没有开始执行。
        if (findTask == null) {
            for (int i = 0; i < mWaitingTaskQueue.size(); i++) {
                TransferableTask task = mWaitingTaskQueue.get(i);
                if (task != null && task.getTransferableFile().getUniqueNumber().equals(uniqueNumber)) {
                    mWaitingTaskQueue.remove(task);
                    findTask = task;
                    break;
                }
            }
            if (findTask == null) {
                for (TransferableTask task : mPausingTaskList) {
                    if (task != null && task.getTransferableFile().getUniqueNumber().equals(uniqueNumber)) {
                        mPausingTaskList.remove(task);
                        findTask = task;
                        break;
                    }
                }
            }
            if (findTask != null) {
                onDeletedTransferableTask(findTask);
            }
        }
    }

    /**
     * 当TransferableTask被删除时回调此方法。
     *
     * @param file
     */
    protected abstract void onDeletedTransferableTask(TransferableTask file);

    /*
     * 等待下载的任务队列。
     */
    protected class TaskQueue {
        private Queue<TransferableTask> taskQueue;

        public TaskQueue() {
            taskQueue = new LinkedList<TransferableTask>();
        }

        /**
         * 入队
         *
         * @param task
         */
        public void offer(TransferableTask task) {
            taskQueue.offer(task);
        }

        /**
         * 将task插入到队首
         *
         * @param task
         */
        public void offerFirst(TransferableTask task) {
            ((LinkedList<TransferableTask>) taskQueue).add(0, task);
        }

        /**
         * 出队，若队列为空，则1秒后再次尝试出队，直到出队成功，或者当前Loader被停止。
         *
         * @return
         */
        public TransferableTask poll() {
            TransferableTask task = null;
            while (isRunning && (mDoingTaskList.size() >= maxThreadCount
                    || (task = taskQueue.poll()) == null)) {
                try {
                    Thread.sleep(1000); // sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return task;
        }

        public TransferableTask get(int position) {
            if (position >= size()) {
                return null;
            }
            return ((LinkedList<TransferableTask>) taskQueue).get(position);
        }

        @SuppressWarnings("unused")
        public boolean remove(int position) {
            return taskQueue.remove(get(position));
        }

        public boolean remove(TransferableTask task) {
            return taskQueue.remove(task);
        }

        public int size() {
            return taskQueue.size();
        }
    }

    /**
     * 返回当前队列中任务的总个数。
     *
     * @return
     */
    public int getTotalTaskCount() {
        return getQueueTaskCount() + getDoingTaskCount() + getPausingTaskCount();
    }

    public int getQueueTaskCount() {
        return mWaitingTaskQueue.size();
    }

    public int getDoingTaskCount() {
        return mDoingTaskList.size();
    }

    public int getPausingTaskCount() {
        return mPausingTaskList.size();
    }

    /**
     * 添加观察者。
     *
     * @param observer
     */
    public void addObserver(TransferObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    /**
     * 删除观察者。
     *
     * @param observer
     */
    public void removeObserver(TransferObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    protected List<TransferableTask> getDoingTaskList() {
        return mDoingTaskList;
    }

    public List<TransferableTask> getPausingTaskList() {
        return mPausingTaskList;
    }

    public TaskQueue getWaitingTaskQueue() {
        return mWaitingTaskQueue;
    }

    public List<TransferableTask> getOtherTaskList() {
        return mOtherTaskList;
    }

    /**
     * 在主线程中，通知所有观察者数据已经改变。
     */
    public void notifyObservers(final TransferObserver.TransferState state, final Map<String, Object> params) {
        Template.getMainHandler().post(new Runnable() {
            public void run() {
                if (observers != null) {
                    // 通知所有观察者，数据已经改变。
                    Iterator<TransferObserver> iter = observers.iterator();
                    while (iter.hasNext()) {
                        iter.next().onTransferDataChanged(state, params);
                    }
                }
            }
        });
    }
}
