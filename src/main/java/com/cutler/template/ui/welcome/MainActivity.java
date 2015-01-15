package com.cutler.template.ui.welcome;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.cutler.template.R;
import com.cutler.template.common.media.MediaManager;
import com.cutler.template.common.transloader.UploadHelper;
import com.cutler.template.common.transloader.UploadHelper.UploadCallback;
import com.cutler.template.common.transloader.upload.UploadManager;
import com.cutler.template.common.transloader.upload.model.UploadFile;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		final ImageView img = new ImageView(this);
//		Drawable drawable = (Drawable) MediaManager.getInstance().getMedia(this, MediaManager.RES_SCHEMA+R.drawable.ic_launcher, false);
//		img.setImageDrawable(drawable);
//		Toast toast = new Toast(this);
//		toast.setView(img);
//		toast.setDuration(Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER, 0, 0);
//		toast.show();
		testUpload(this);
	}

	public void testUpload(Context context) {
		final UploadFile uploadFile = new UploadFile();
		uploadFile.setUrl("http://192.168.1.109:8080/UploadFileServer/upload");
		uploadFile.setLocalPath(new File(Environment.getExternalStorageDirectory(),"a.apk").getAbsolutePath());
		UploadHelper.uploadFileWithNotify(context, uploadFile, new UploadCallback() {
			public boolean onUploadFinish(String result) {
				System.out.println("上传完成: "+result);
				return false;
			}
		});
		
		final UploadFile uploadFile2 = new UploadFile();
		uploadFile2.setUrl("http://192.168.1.109:8080/UploadFileServer/upload");
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
