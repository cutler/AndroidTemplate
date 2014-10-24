package com.cutler.template.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.cutler.template.R;
import com.cutler.template.common.download.DownloadHelper;
import com.cutler.template.common.download.DownloadHelper.DownloadCallback;
import com.cutler.template.common.download.DownloadManager;
import com.cutler.template.common.download.model.DownloadFile;

public class DownloadTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			String url = "http://gdown.baidu.com/data/wisegame/1b9392eadc3bddf1/WeChat_480.apk";
    		final DownloadFile downloadFile = new DownloadFile();
    		downloadFile.setUrl(url);
    		downloadFile.setUseCache(true);
			downloadFile.setFileName(new File(new URL(url).getFile()).getName());
			DownloadHelper.downloadApkFileWithNotify(this, downloadFile, new DownloadCallback() {
				public boolean onDownloadFinish(String localPath) {
					return false;
				}
			});
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
			DownloadHelper.downloadApkFileWithNotify(this, downloadFile, new DownloadCallback() {
				public boolean onDownloadFinish(String localPath) {
					return false;
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		new Handler().postDelayed(new Runnable() {
			public void run() {
				DownloadManager.getInstance().service(DownloadManager.DownloadTypes.PAUSE_ALL);
			}
		}, 8000);
	}
}
