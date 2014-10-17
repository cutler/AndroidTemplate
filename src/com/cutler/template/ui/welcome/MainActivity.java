package com.cutler.template.ui.welcome;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.cutler.template.R;
import com.cutler.template.common.download.DownloadHelper;
import com.cutler.template.common.download.DownloadHelper.DownloadCallback;
import com.cutler.template.common.download.DownloadManager;
import com.cutler.template.common.download.model.DownloadFile;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		System.out.println("准备！！！");
		
		try {
			String url = "http://gdown.baidu.com/data/wisegame/1b9392eadc3bddf1/WeChat_480.apk";
    		final DownloadFile downloadFile = new DownloadFile();
    		downloadFile.setUrl(url);
    		downloadFile.setUseCache(true);
			downloadFile.setFileName(new File(new URL(url).getFile()).getName());
//			DownloadHelper.downloadApkFileWithNotify(MainActivity.this, downloadFile, new DownloadCallback() {
//				public boolean onDownloadFinish(String localPath) {
//					return false;
//				}
//			});
//			new Handler().postDelayed(new Runnable() {
//				public void run() {
//					DownloadManager.getInstance().service(DownloadManager.DownloadTypes.PAUSE, downloadFile);
//				}
//			}, 6000);
//			new Handler().postDelayed(new Runnable() {
//				public void run() {
//					DownloadManager.getInstance().service(DownloadManager.DownloadTypes.CONTINUE, downloadFile);
//				}
//			}, 9000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			String url = "http://down1.app.uc.cn/pack/2014/09/25/com.tencent.mobileqq_5.1.1.apk";
			final DownloadFile downloadFile = new DownloadFile();
			downloadFile.setUrl(url);
			downloadFile.setUseCache(true);
			downloadFile.setFileName(new File(new URL(url).getFile()).getName());
//			DownloadHelper.downloadApkFileWithNotify(MainActivity.this, downloadFile, new DownloadCallback() {
//				public boolean onDownloadFinish(String localPath) {
//					return false;
//				}
//			});
			
			new AsyncTask<Object, Object, Object>() {

				protected Object doInBackground(Object... params) {
					System.out.println("*********************************************** 1"+Thread.currentThread().getName());
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
			}.execute();
			
			
			new AsyncTask<Object, Object, Object>() {

				protected Object doInBackground(Object... params) {
					System.out.println("*********************************************** 2"+Thread.currentThread().getName());
					return null;
				}
			}.execute();
			
			
			new Handler().postDelayed(new Runnable() {
				public void run() {
					new AsyncTask<Object, Object, Object>() {

						protected Object doInBackground(Object... params) {
							System.out.println("*********************************************** 3 "+Thread.currentThread().getName());
							return null;
						}
					}.execute();
//					DownloadManager.getInstance().service(DownloadManager.DownloadTypes.DELETE, downloadFile);
				}
			}, 6000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				DownloadManager.getInstance().service(DownloadManager.DownloadTypes.STOP);
//			}
//		}, 5000);
	}
}
