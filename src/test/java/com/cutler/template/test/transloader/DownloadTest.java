package com.cutler.template.test.transloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.Handler;

import com.cutler.template.common.transloader.DownloadHelper;
import com.cutler.template.common.transloader.DownloadHelper.DownloadCallback;
import com.cutler.template.common.transloader.common.AbstractManager.TransloadAction;
import com.cutler.template.common.transloader.download.DownloadManager;
import com.cutler.template.common.transloader.download.model.DownloadFile;

public class DownloadTest {

	protected void testDownload(Context context) {
		// 下载微信
		{
			String url = "http://gdown.baidu.com/data/wisegame/1b9392eadc3bddf1/WeChat_480.apk";
			final DownloadFile downloadFile = new DownloadFile();
			downloadFile.setUrl(url);
			downloadFile.setUseCache(true);
			DownloadHelper.downloadApkFileWithNotify(context, downloadFile,new DownloadCallback() {
				public boolean onDownloadFinish(String localPath) {
					return false;
				}
			});
		}
		
		//　下载qq
		try {
			String url = "http://gdown.baidu.com/data/wisegame/2c6a60c5cb96c593/QQ_182.apk";
			final DownloadFile downloadFile = new DownloadFile();
			downloadFile.setUrl(url);
			downloadFile.setUseCache(true);
			downloadFile.setFileName(new File(new URL(url).getFile()).getName());
			DownloadHelper.downloadApkFileWithNotify(context, downloadFile, new DownloadCallback() {
				public boolean onDownloadFinish(String localPath) {
					return false;
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
			
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 10秒后暂定所有下载。
				DownloadManager.getInstance().service(TransloadAction.PAUSE_ALL);
				// 取消微信下载。
				try {
					String url = "http://gdown.baidu.com/data/wisegame/1b9392eadc3bddf1/WeChat_480.apk";
					final DownloadFile downloadFile = new DownloadFile();
					downloadFile.setUrl(url);
					downloadFile.setDeleteCacheFile(true);
					downloadFile.setFileName(new File(new URL(url).getFile()).getName());
					DownloadManager.getInstance().service(TransloadAction.DELETE, downloadFile);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}, 10000);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				try {
					// 14秒后，继续下载qq
					String url = "http://gdown.baidu.com/data/wisegame/2c6a60c5cb96c593/QQ_182.apk";
					final DownloadFile downloadFile = new DownloadFile();
					downloadFile.setUrl(url);
					downloadFile.setUseCache(true);
					downloadFile.setFileName(new File(new URL(url).getFile()).getName());
					DownloadManager.getInstance().service(TransloadAction.CONTINUE, downloadFile);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}, 14000);
	}
}
