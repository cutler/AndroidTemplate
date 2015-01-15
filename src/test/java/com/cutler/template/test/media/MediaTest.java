package com.cutler.template.test.media;

import android.test.AndroidTestCase;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.cutler.template.R;
import com.cutler.template.common.media.MediaManager;
import com.cutler.template.common.media.MediaManager.ImageLoaderCallback;

/**
 * 测试类得继承AndroidTestCase，这样测试类才能有getContext()来获取当前的上下文变量。
 * 
 * @author cutler
 */
public class MediaTest extends AndroidTestCase {

	/**
	 * 从res目录下异步加载图片。
	 * @throws InterruptedException
	 */
	public void testLoadResImg() throws InterruptedException {
		final ImageView img = new ImageView(getContext());
		MediaManager.getInstance().loadImage(getContext(), MediaManager.RES_SCHEMA+R.drawable.ic_launcher, img, new ImageLoaderCallback() {
			public void onImageLoadFinish(boolean success) {
				Toast toast = new Toast(getContext());
				toast.setView(img);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		},false, -1);
		Thread.sleep(10 * 1000);
	}
	
	/**
	 * 从http异步加载图片。
	 * @throws InterruptedException
	 */
	public void testLoadHttpImg() throws InterruptedException {
		final ImageView img = new ImageView(getContext());
		MediaManager.getInstance().loadImage(getContext(), "http://www.baidu.com/img/bd_logo1.png", img, new ImageLoaderCallback() {
			public void onImageLoadFinish(boolean success) {
				Toast toast = new Toast(getContext());
				toast.setView(img);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		},false, -1);
		Thread.sleep(10 * 1000);
	}

}
