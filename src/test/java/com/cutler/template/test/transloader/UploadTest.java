package com.cutler.template.test.transloader;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.cutler.template.common.transloader.UploadHelper;
import com.cutler.template.common.transloader.UploadHelper.UploadCallback;
import com.cutler.template.common.transloader.upload.UploadManager;
import com.cutler.template.common.transloader.upload.model.UploadFile;

public class UploadTest {

	public void testUpload(Context context) {
		final UploadFile uploadFile = new UploadFile();
		uploadFile.setUrl("http://192.168.1.107:8080/UploadFileServer/upload");
		uploadFile.setLocalPath(new File(Environment.getExternalStorageDirectory(),"a.apk").getAbsolutePath());
		UploadHelper.uploadFileWithNotify(context, uploadFile, new UploadCallback() {
			public boolean onUploadFinish(String result) {
				System.out.println("上传完成: "+result);
				return false;
			}
		});
		
		final UploadFile uploadFile2 = new UploadFile();
		uploadFile2.setUrl("http://192.168.1.107:8080/UploadFileServer/upload");
		uploadFile2.setLocalPath(new File(Environment.getExternalStorageDirectory(),"b.apk").getAbsolutePath());
		UploadHelper.uploadFileWithNotify(context, uploadFile2, new UploadCallback() {
			public boolean onUploadFinish(String result) {
				System.out.println("上传完成: "+result);
				return false;
			}
		});
		
		new Handler().postDelayed(new Runnable() {
			public void run() {
				System.out.println("暂停");//TODO
				UploadManager.getInstance().service(UploadManager.TransloadAction.PAUSE_ALL, uploadFile);
			}
		}, 5000);
		
		new Handler().postDelayed(new Runnable() {
			public void run() {
				System.out.println("继续");//TODO
				UploadManager.getInstance().service(UploadManager.TransloadAction.CONTINUE, uploadFile);
			}
		}, 15000);
		
		new Handler().postDelayed(new Runnable() {
			public void run() {
				UploadManager.getInstance().service(UploadManager.TransloadAction.DELETE, uploadFile2);
			}
		}, 20000);
	}
}
