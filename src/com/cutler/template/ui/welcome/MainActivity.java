package com.cutler.template.ui.welcome;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cutler.template.R;
import com.cutler.template.common.Config;
import com.cutler.template.common.download.DownloadManager;
import com.cutler.template.common.download.goal.DownloadObserver;
import com.cutler.template.common.download.model.DownloadFile;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		DownloadManager.getInstance().service(Config.DownloadTypes.INIT_DOWNLOADER);
		DownloadManager.getInstance().addObserver(new DownloadObserver() {
			@Override
			public void onDownloadDataChanged(Map<String, Object> params) {
				//TODO
				int type = (Integer) params.get(DownloadObserver.KEY_TYPE);
				if(type == Config.DownloadTypes.ADD) {
					System.out.println("准备下载："+params.get(DownloadObserver.KEY_FILE));
				} else if(type == Config.DownloadTypes.PROGRESS) {
					System.out.println(params.get(DownloadObserver.KEY_FILE) + "进度更新："
							+ params.get(DownloadObserver.KEY_SPEED)+"," 
							+ params.get(DownloadObserver.KEY_PROGRESS));
					
				} else if(type == Config.DownloadTypes.COMPLETE) {
					System.out.println("下载完成，准备安装！");
					String filePath = params.get(DownloadObserver.KEY_LOCAL_PATH).toString();
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					System.out.println(Uri.parse(filePath));//TODO
					intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			}
		});
		DownloadFile file = new DownloadFile();
		file.setUseCache(false);
		file.setUrl("http://bs.baidu.com/appstore/apk_6E0731EA8E90CDC7E67C55DB4D4F71AF.apk");
		DownloadManager.getInstance().service(Config.DownloadTypes.ADD, file);
	}
}
