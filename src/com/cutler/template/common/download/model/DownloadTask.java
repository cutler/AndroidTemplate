
package com.cutler.template.common.download.model;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cutler.template.MainApplication;
import com.cutler.template.common.download.exception.FileAlreadyExistException;
import com.cutler.template.common.download.exception.NoMemoryException;
import com.cutler.template.common.download.goal.DownloadTaskListener;
import com.cutler.template.util.NetworkUtils;
import com.cutler.template.util.StorageUtils;

public class DownloadTask extends AsyncTask<Void, Long, Long> {
	private final static int TIME_OUT = 30000;
	// 更新进度的时间间隔（毫秒），每载几kb的数据就通知一次太过于频繁。
    private final static int NOTIFY_PROGRESS_INTERVAL = 1000;
    private final static int BUFFER_SIZE = 1024 * 8;
    private final static String TAG = "DownloadTask";
    private final static boolean DEBUG = true;
    private final static String TEMP_SUFFIX = ".download";

    private URL URL;
    private File file;
    private File tempFile;
    private DownloadFile downloadFile;
    private RandomAccessFile outputStream;
    private DownloadTaskListener listener;

    private long downloadSize;
    private long previousFileSize;
    private long totalSize;
    private int downloadPercent;
    private long networkSpeed;
    private long previousTime;
    private long totalTime;
    private Throwable error = null;
    private boolean interrupt = false;
    private boolean pause = false;

    private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
        private long progress = 0;
        private long previousPublishTime;
        public ProgressReportingRandomAccessFile(File file, String mode)
                throws FileNotFoundException {

            super(file, mode);
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            progress += count;
            if(System.currentTimeMillis() - NOTIFY_PROGRESS_INTERVAL >= previousPublishTime){
            	previousPublishTime = System.currentTimeMillis();
            	publishProgress(progress);
            }
        }
    }

    public DownloadTask(DownloadFile downloadFile, String path) throws MalformedURLException {
        this(downloadFile, path, null);
    }

    public DownloadTask(DownloadFile downloadFile, String path, DownloadTaskListener listener)
            throws MalformedURLException {
        super();
        this.downloadFile = downloadFile;
        this.URL = new URL(downloadFile.getUrl());
        this.listener = listener;
        String fileName = new File(URL.getFile()).getName();
        this.file = new File(path, fileName);
        file.getParentFile().mkdirs();
        this.tempFile = new File(path, fileName + TEMP_SUFFIX);
    }

    @Override
    protected void onPreExecute() {
        previousTime = System.currentTimeMillis();
        if (listener != null){
        	listener.onPreExecute(this);
        }
    }

    @Override
    protected Long doInBackground(Void... params) {
        long result = -1;
        try {
            result = download();
        } catch (NetworkErrorException e) {
            error = e;
        } catch (FileAlreadyExistException e) {
        	// 如果不使用缓存的话直接去下载，否则则会抛出异常，并执行这个代码。
			error = null;
			result = totalSize;
        } catch (NoMemoryException e) {
            error = e;
        } catch (IOException e) {
            error = e;
        } finally {
            if (client != null) {
                client.close();
                client = null;
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Long result) {
        if (result == -1 || interrupt || error != null) {
            if (DEBUG && error != null) {
                Log.v(TAG, "Download failed." + error.getMessage());
            }
            if (listener != null) {
				if (error != null) {
					listener.onError(this, error);
				}
				if (pause) {
					listener.onPaused(this);
				} else if(interrupt) {
					listener.onCanceled(this);
				}
            }
            return;
        }
        // finish download
        if(tempFile.length() > 0){
        	tempFile.renameTo(file);
        }
        if (listener != null) {
        	listener.onFinished(this);
        }
    }
    
    @Override
    protected void onProgressUpdate(Long... progress) {
    	// 如果传递了2个参数。
        if (progress.length > 1) {
            totalSize = progress[1];
            if (totalSize == -1 && listener != null) {
            	listener.onError(this, error);
            }
        } else {
            totalTime = System.currentTimeMillis() - previousTime;
            downloadSize = progress[0];
            downloadPercent = (int) ((downloadSize + previousFileSize) * 100 / totalSize);
            networkSpeed = downloadSize / totalTime;
			if (listener != null) {
				listener.onProgressChanged(this);
			}
        }
    }

    /**
     * 停止任务的下载。
     */
	public void stop() {
		interrupt = true;
	}
    
    /**
     * 暂停任务的下载。
     */
	public void pause() {
		// 标识当前Task是通过暂停方式来终止下载的。
		pause = true;
		stop();
	}

    private AndroidHttpClient client;
    private HttpGet httpGet;
    private HttpResponse response;

    private long download() throws NetworkErrorException, IOException, FileAlreadyExistException,
            NoMemoryException {
        if (DEBUG) {
            Log.v(TAG, "totalSize: " + totalSize);
        }

        /*
         * check net work
         */
        if (!NetworkUtils.isNetworkAvailable(MainApplication.getInstance())) {
            throw new NetworkErrorException("Network blocked.");
        }
        
        String fileDirPath = MainApplication.getInstance().getFilesDir().getAbsolutePath();
        if(tempFile.getAbsolutePath().startsWith(fileDirPath)){
        	/*
        	 *  如果缓存的目录是在/data/data/package/files/下面，则需要做下面两个操作后，外界（比如系统的APK安装器）才可以访问到该文件。
        	 *  1. 将该文件保存到files的根目录下。
        	 *  2. 设置该文件的权限为“全世界可读写”。
        	 *  二者缺一不可。
        	 */
        	file = new File(fileDirPath, file.getName());
        	tempFile = new File(fileDirPath, tempFile.getName());
            try {
                // 若是文件不存在，则创建文件。
                if (!tempFile.exists()) {
                	OutputStream output = MainApplication.getInstance().openFileOutput(tempFile.getName(), 
                			Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_READABLE);
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*
         * check file length
         */
        client = AndroidHttpClient.newInstance("DownloadTask");
        httpGet = new HttpGet(downloadFile.getUrl());
        response = client.execute(httpGet);
        totalSize = response.getEntity().getContentLength();
        if (file.exists() && totalSize == file.length()) {
            if (DEBUG) {
                Log.v(null, "Output file already exists. Skipping download.");
            }
            if(downloadFile.isUseCache()){
            	throw new FileAlreadyExistException("Output file already exists. Skipping download.");
            }
        }
        if (tempFile.exists()) {
            httpGet.addHeader("Range", "bytes=" + tempFile.length() + "-");
            previousFileSize = tempFile.length();

            client.close();
            client = AndroidHttpClient.newInstance("DownloadTask");
            response = client.execute(httpGet);

            if (DEBUG) {
                Log.v(TAG, "File is not complete, download now.");
                Log.v(TAG, "File length:" + tempFile.length() + " totalSize:" + totalSize);
            }
        }

        /*
         * check memory
         */
        long storage = StorageUtils.getAvailableStorage();
        if (DEBUG) {
            Log.i(null, "storage:" + storage + " totalSize:" + totalSize);
        }

        if (totalSize - tempFile.length() > storage) {
            throw new NoMemoryException("SD card no memory.");
        }

        /*
         * start download
         */
        outputStream = new ProgressReportingRandomAccessFile(tempFile, "rw");

        publishProgress(0L, totalSize);

        InputStream input = response.getEntity().getContent();
        int bytesCopied = copy(input, outputStream);

        if ((previousFileSize + bytesCopied) != totalSize && totalSize != -1 && !interrupt) {
            throw new IOException("Download incomplete: " + bytesCopied + " != " + totalSize);
        }

        if (DEBUG) {
            Log.v(TAG, "Download completed successfully.");
        }

        return bytesCopied;
    }

    public int copy(InputStream input, RandomAccessFile out) throws IOException,
            NetworkErrorException {

        if (input == null || out == null) {
            return -1;
        }

        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        if (DEBUG) {
            Log.v(TAG, "length" + out.length());
        }

        int count = 0, n = 0;
        long errorBlockTimePreviousTime = -1, expireTime = 0;

        try {

            out.seek(out.length());

            while (!interrupt) {
                n = in.read(buffer, 0, BUFFER_SIZE);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                count += n;

                /*
                 * check network
                 */
                if (!NetworkUtils.isNetworkAvailable(MainApplication.getInstance())) {
                    throw new NetworkErrorException("Network blocked.");
                }

                if (networkSpeed == 0) {
                    if (errorBlockTimePreviousTime > 0) {
                        expireTime = System.currentTimeMillis() - errorBlockTimePreviousTime;
                        if (expireTime > TIME_OUT) {
                            throw new ConnectTimeoutException("connection time out.");
                        }
                    } else {
                        errorBlockTimePreviousTime = System.currentTimeMillis();
                    }
                } else {
                    expireTime = 0;
                    errorBlockTimePreviousTime = -1;
                }
            }
        } finally {
            client.close(); // must close client first
            client = null;
            out.close();
            in.close();
            input.close();
        }
        return count;

    }

    public DownloadFile getDownloadFile() {
        return downloadFile;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public int getDownloadPercent() {
        return downloadPercent;
    }

    public long getDownloadSize() {
        return downloadSize + previousFileSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getDownloadSpeed() {
        return this.networkSpeed;
    }

    public long getTotalTime() {
        return this.totalTime;
    }

    public DownloadTaskListener getListener() {
        return this.listener;
    }
    
    /**
     * 返回下载文件保存在本地的绝对路径。
     * @return
     */
    public String getDownloadFileLocalPath(){
    	return file.getAbsolutePath();
    }

    public String getDownloadFileLocalTempPath(){
    	return tempFile.getAbsolutePath();
    }
}
