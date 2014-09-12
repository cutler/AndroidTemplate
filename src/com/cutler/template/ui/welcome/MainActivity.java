package com.cutler.template.ui.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.cutler.template.MainApplication;
import com.cutler.template.R;
import com.cutler.template.common.Config;
import com.cutler.template.common.download.DownloadHelper;
import com.cutler.template.common.download.DownloadManager;
import com.cutler.template.common.download.model.DownloadFile;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final DownloadFile downloadFile = new DownloadFile();
		downloadFile.setUseCache(false);
		downloadFile.setFileName(getString(R.string.app_name));
		downloadFile.setUrl("http://www.dev.suishenbb.com/download/suishenbb_android_1.5.1?c=Appchina");
		DownloadHelper.downloadApkFileWithNotify(MainApplication.getInstance(), downloadFile, null);
		
		new Handler().postDelayed(new Runnable() {
			public void run() {
				DownloadManager.getInstance().service(Config.DownloadTypes.PAUSE, downloadFile);
			}
		}, 3000);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				DownloadManager.getInstance().service(Config.DownloadTypes.CONTINUE, downloadFile);
			}
		}, 6000);
	}
}
