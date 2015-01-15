package com.cutler.template.common.transloader;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.cutler.template.MainApplication;
import com.cutler.template.R;
import com.cutler.template.common.transloader.common.AbstractManager.TransloadAction;
import com.cutler.template.common.transloader.common.TransferObserver;
import com.cutler.template.common.transloader.download.DownloadManager;
import com.cutler.template.common.transloader.download.model.DownloadFile;
import com.cutler.template.util.NotificationUtils;
import com.cutler.template.util.NotificationUtils.OnProgressListener;

/**
 * 对DownloadManager类的再次封装，提供了多个下载的工具方法
 * @author cutler
 */
public class DownloadHelper {
	// 全局的通知Id，每添加一个下载任务，都会让此Id加1。
	private static int globalNotifyId;
	private static Map<String, Integer> notifyIds = new HashMap<String, Integer>();
	
	/**
	 * 下载APK文件，并向状态栏中添加一个进度条通知。
	 * @param context
	 * @param downloadFile
	 */
	public static void downloadApkFileWithNotify(final Context context, final DownloadFile downloadFile, final DownloadCallback dCallback){
		if(notifyIds.get(downloadFile.getUrl()) != null){
			return;
		}
		globalNotifyId++;
		notifyIds.put(downloadFile.getUrl(), globalNotifyId);
		// 创建观察者。
		TransferObserver observer = new TransferObserver() {
			OnProgressListener callback;
			@Override
			public void onTransferDataChanged(TransferState state, Map<String, Object> params) {
				DownloadFile file = (DownloadFile) params.get(TransferObserver.KEY_FILE);
				if(!file.getUrl().equals(downloadFile.getUrl())){
					return;
				}
				switch(state){
				case ADDED:
					pushNotify();
					break;
				case PROGRESS:
					pushNotify();
					callback.onProgessChanged((Integer) params.get(TransferObserver.KEY_PROGRESS));
					break;
				case FINISHED:
					NotificationUtils.cancelNotify(context, notifyIds.get(downloadFile.getUrl()));
					notifyIds.remove(downloadFile.getUrl());
					DownloadManager.getInstance().removeObserver(this);
					String localPath = params.get(TransferObserver.KEY_LOCAL_PATH).toString();
					// 前端未处理，则默认打开安装程序。
					if(dCallback != null && !dCallback.onDownloadFinish(localPath)){
						new DefaultDownloadCallback().onDownloadFinish(localPath);
					}
					break;
				case ERROR:
				case DELETED:
					NotificationUtils.cancelNotify(context, notifyIds.get(downloadFile.getUrl()));
					notifyIds.remove(downloadFile.getUrl());
					DownloadManager.getInstance().removeObserver(this);
					if(state == TransferState.ERROR){
						DownloadManager.getInstance().service(TransloadAction.DELETE, file);
					}
					break;
				}
			}
			
			private void pushNotify(){
				if (callback == null) {
					callback = NotificationUtils.showProgressNotify(context, 
							R.string.download_start,
							R.string.download_doing, downloadFile.getFileName(), notifyIds.get(downloadFile.getUrl()));
				}
			}
		};
		DownloadManager.getInstance().addObserver(observer);
		// 加入到下载队列中。
		DownloadManager.getInstance().service(TransloadAction.ADD, downloadFile);
	}
	
	public static interface DownloadCallback {
		/**
		 * 当下载完成后 DownloadHelper 会回调此方法通知前端，如果前端处理掉了这个事件，则返回true，此时DownloadHelper将不再处理。
		 * @param localPath
		 * @return
		 */
		public boolean onDownloadFinish(String localPath);
	}
	
	/**
	 * 默认的处理器。
	 * @author cutler
	 */
	public static class DefaultDownloadCallback implements DownloadCallback {
		public boolean onDownloadFinish(String localPath) {
			if (localPath.endsWith("apk")) {
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + localPath), "application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				MainApplication.getInstance().startActivity(intent);
			}
			return true;
		}
	}
}
