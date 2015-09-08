package com.cutler.template.common.transloader.upload.model;

import android.os.Message;

import com.cutler.template.Template;
import com.cutler.template.common.http.HttpCaller;
import com.cutler.template.common.transloader.common.TransferObserver;
import com.cutler.template.common.transloader.common.TransferObserver.TransferState;
import com.cutler.template.common.transloader.common.TransferableFile;
import com.cutler.template.common.transloader.common.TransferableFile.FileState;
import com.cutler.template.common.transloader.common.TransferableTask;
import com.cutler.template.util.io.IOUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用HttpURLConnection来完成上传操作。
 * 
 * @author cutler
 * 
 */
public class HttpURLConnectionUploadTask extends Thread implements TransferableTask {
	private UploadFile uploadFile;
	private TransferObserver listener;
	private Throwable error;
	
	// 已经上传的大小
	private long uploadedSize;
	// 总大小
	private long totalSize;
	// 上一次更新进度的时间
	private long previousPublishTime;
	// 任务是否被中断、暂停
	private boolean deleted;
    private boolean paused;
    private int uploadPercent;
    // 服务端返回的上传结果
    private String result;

	public HttpURLConnectionUploadTask(UploadFile uploadFile, TransferObserver listener) {
		this.uploadFile = uploadFile;
		this.listener = listener;
	}

	@Override
	public void startTask() {
		start();
	}
	
	@Override
	public void run() {
		upload(uploadFile, uploadFile.getParams());
	}

	@Override
	public void pauseTask() {
		paused = true;
	}

	@Override
	public void stopTask() {
		deleted = true;
	}

	@Override
	public TransferableFile getTransferableFile() {
		return uploadFile;
	}
	
	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 * @param params 普通参数
	 * @param file 文件参数
	 * @return
	 * @throws IOException
	 */
	public void upload(UploadFile file, Map<String, String> params) {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String result = null;
		InputStream inputStream = null;
		DataOutputStream outputStream = null;
		HttpURLConnection conn = null;
		try {
			URL uri = new URL(file.getUrl());
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(HttpCaller.REQUEST_READ_TIMEOUT);
			conn.setConnectTimeout(HttpCaller.REQUEST_CONNECT_TIMEOUT);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setChunkedStreamingMode(1024);		// 1K
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", HttpCaller.ENCODE_CHARSETNAME);
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
			outputStream = new DataOutputStream(conn.getOutputStream());
			// 首先组拼文本类型的参数
			if (params != null && params.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINEND);
					sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
					sb.append("Content-Type: text/plain; charset=" + HttpCaller.ENCODE_CHARSETNAME + LINEND);
					sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
					sb.append(LINEND);
					sb.append(entry.getValue());
					sb.append(LINEND);
				}
				outputStream.write(sb.toString().getBytes());
			}
			// 发送文件数据
			if (file != null) {
				File localFile = new File(file.getLocalPath());
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				// name是post中传参的键 filename是文件的名称
				sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + localFile.getName() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset=" + HttpCaller.ENCODE_CHARSETNAME + LINEND);
				sb1.append(LINEND);
				outputStream.write(sb1.toString().getBytes());
				// 将本地文件写入到内存中
				inputStream = new FileInputStream(file.getLocalPath());
				byte[] buffer = new byte[2048];
				int count = 0; 
				totalSize = localFile.length();
				while (!isInterrupt() && (count = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, count);
					uploadedSize += count;
					uploadPercent = (int) (uploadedSize * 100.0 / totalSize);
					publishProgress(count);
				}
				publishProgress(count);
				outputStream.write(LINEND.getBytes());
				// 请求结束标志
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
				outputStream.write(end_data);
				outputStream.flush();
				// 获取返回数据
				result = (String) HttpCaller.getInstance().executeRequest(conn, String.class);
			}
		}  catch (Exception e) {
			error = e;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			IOUtil.closeInputStream(inputStream);
			IOUtil.closeOutputStream(outputStream);
			onPostExecute(result);
		}
	}

	/*
	 * 更新进度
	 */
	private void publishProgress(int count) {
		long millis = System.currentTimeMillis();
		if(count == -1 || millis - NOTIFY_PROGRESS_INTERVAL >= previousPublishTime){
			previousPublishTime = millis;
			// 上传进度改变。 
			onUploadStateChanged(TransferState.PROGRESS);
		}
	}
	
	/*
	 * 上传完成
	 */
	private void onPostExecute(String result) {
		if (deleted || paused || error != null) {
			if (error != null) {
				onUploadStateChanged(TransferState.ERROR);
			}
			if (paused) {
				onUploadStateChanged(TransferState.PAUSED);
			} else if (deleted) {
				onUploadStateChanged(TransferState.DELETED);
			}
			return;
		}
		this.result = result;
		onUploadStateChanged(TransferState.FINISHED);
	}

	/*
     * 当上传状态改变时，回调此方法通知Uploader。
     */
	private void onUploadStateChanged(final TransferState state) {
		if (listener != null) {
			Message.obtain(Template.getMainHandler(), new Runnable() {
				public void run() {
					if (listener != null) {
						Map<String, Object> params = new HashMap<String, Object>();
						params.put(TransferObserver.KEY_TASK, HttpURLConnectionUploadTask.this);
						if (error != null) {
							params.put(TransferObserver.KEY_ERROR, error);
						}
						listener.onTransferDataChanged(state, params);
						FileState fileState = null;
						switch (state) {
							case ERROR:
								fileState = FileState.ERROR;
								break;
							case PAUSED:
								fileState = FileState.PAUSED;
								break;
							case DELETED:
								fileState = FileState.DELETED;
								break;
							case FINISHED:
								fileState = FileState.FINISHED;
								break;
						}
						if (fileState != null) {
							uploadFile.setState(fileState);
//							UploadListDAO.getInstance(MainApplication.getInstance()).doCreateOrUpdate(uploadFile);TODO
						}
					}
				}
			}).sendToTarget();
		}
	}
	
	/*
	 * 当前上传任务是否被中断。
	 */
	private boolean isInterrupt(){
		return deleted || paused;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public int getUploadPercent() {
		return uploadPercent;
	}

	public UploadFile getUploadFile() {
		return uploadFile;
	}

	public String getResult() {
		return result;
	}

}
