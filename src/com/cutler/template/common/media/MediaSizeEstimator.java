package com.cutler.template.common.media;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class MediaSizeEstimator {

	/**
	 * 估算出参数Media所占据的字节大小。
	 * 
	 * @return
	 */
	public static int estimateMediaSize(Object media) {
		int size = 0;
		if (media instanceof BitmapDrawable) {
			// 计算图片文件的大小。
			BitmapDrawable d = (BitmapDrawable) media;
			size = d.getBitmap().getRowBytes() * d.getBitmap().getHeight();
		} else if (media instanceof Drawable) {
			// 计算图片文件的大小。
			Drawable d = (Drawable) media;
			size = d.getIntrinsicHeight() * d.getIntrinsicWidth() * 4;
		}
		if(size <= 0){
			size = 1;
		}
		return size;
	}
}
