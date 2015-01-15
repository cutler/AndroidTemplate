package com.cutler.template.common.transloader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.cutler.template.R;
import com.cutler.template.common.transloader.common.AbstractManager.TransloadAction;
import com.cutler.template.common.transloader.common.TransferObserver;
import com.cutler.template.common.transloader.upload.UploadManager;
import com.cutler.template.common.transloader.upload.model.UploadFile;
import com.cutler.template.util.NotificationUtils;
import com.cutler.template.util.NotificationUtils.OnProgressListener;

public class UploadHelper {
	// 全局的通知Id，每添加一个下载任务，都会让此Id加1。
	private static int globalNotifyId = 50000;
	private static Map<String, Integer> notifyIds = new HashMap<String, Integer>();
	
	/**
	 * 下载APK文件，并向状态栏中添加一个进度条通知。
	 * @param context
	 * @param uploadFile
	 */
	public static void uploadFileWithNotify(final Context context, final UploadFile uploadFile, final UploadCallback dCallback){
		if(notifyIds.get(uploadFile.getLocalPath()) != null){
			return;
		}
		globalNotifyId++;
		notifyIds.put(uploadFile.getLocalPath(), globalNotifyId);
		// 创建观察者。
		TransferObserver observer = new TransferObserver() {
			OnProgressListener callback;
			@Override
			public void onTransferDataChanged(TransferState state, Map<String, Object> params) {
				UploadFile file = (UploadFile) params.get(TransferObserver.KEY_FILE);
				if(!file.getLocalPath().equals(uploadFile.getLocalPath())){
					return;
				}
				switch(state){
				case ADDED:
					callback = NotificationUtils.showProgressNotify(context, 
							R.string.upload_start,
							R.string.upload_doing,new File(uploadFile.getLocalPath()).getName(), notifyIds.get(uploadFile.getLocalPath()));
					break;
				case PROGRESS:
					callback.onProgessChanged((Integer) params.get(TransferObserver.KEY_PROGRESS));
					break;
				case FINISHED:
					NotificationUtils.cancelNotify(context, notifyIds.get(uploadFile.getLocalPath()));
					notifyIds.remove(uploadFile.getLocalPath());
					UploadManager.getInstance().removeObserver(this);
					if(dCallback != null){
						dCallback.onUploadFinish((String) params.get(TransferObserver.KEY_RESULT));
					}
					break;
				case ERROR:
				case DELETED:
					NotificationUtils.cancelNotify(context, notifyIds.get(uploadFile.getLocalPath()));
					notifyIds.remove(uploadFile.getLocalPath());
					UploadManager.getInstance().removeObserver(this);
					if(state == TransferState.ERROR){
						UploadManager.getInstance().service(TransloadAction.DELETE, file);
					}
					break;
				}
			}
		};
		UploadManager.getInstance().addObserver(observer);
		// 加入到下载队列中。
		UploadManager.getInstance().service(TransloadAction.ADD, uploadFile);
	}
	
	public static interface UploadCallback {
		public boolean onUploadFinish(String result);
	}
}
