package com.cutler.template.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

/**
 * 图片处理
 * @author cutler
 *
 */
public class ImageUtil {

	/**
	 * 缩放BitmapDrawable到指定的尺寸。
	 * @param context
	 * @param drawable	要缩放的位图
	 * @param newWidth	新宽度
	 * @param newHeight	新高度
	 * @return
	 */
	public static BitmapDrawable resizeBitmap(Context context, BitmapDrawable drawable, int newWidth, int newHeight){
		Bitmap oldBitmap = drawable.getBitmap();
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        
        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(oldBitmap, 0, 0,
                          width, height, matrix, true);
        System.out.println("width = "+width+", height = "+height+", scaleWidth = "+scaleWidth+", scaleHeight = "+scaleHeight+
        		" resizedBitmap = "+resizedBitmap.getWidth()+","+resizedBitmap.getHeight());
        // make a Drawable from Bitmap to allow to set the BitMap
        // to the ImageView, ImageButton or what ever
        return new BitmapDrawable(context.getResources() ,resizedBitmap);
	}
	
	/**
	 * 返回一个灰度的过滤器。
	 * drawable.setColorMatrix(...)
	 * @return
	 */
	public static ColorMatrixColorFilter getGrayColorFilter() {
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.setSaturation(0);
		ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(cMatrix);
		return colorFilter;
	}
	
	/**
	 * 合并两张图片。
	 * @param d1   以d1作为背景
	 * @param d2   以d2作为前景，居中显示
	 * @return
	 */
	public static BitmapDrawable mergeBitmapDrawale(Context context, BitmapDrawable d1, BitmapDrawable d2) {
		int borderWidth = d1.getBitmap().getWidth(), borderHeight = d1.getBitmap().getHeight();
		Bitmap bitmap = Bitmap.createBitmap(borderWidth, borderHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(d1.getBitmap(), 0, 0, paint);
		canvas.drawBitmap(d2.getBitmap(), (borderWidth - d2.getBitmap().getWidth())/2, 
				(borderHeight - d2.getBitmap().getHeight())/2, paint);
		return new BitmapDrawable(context.getResources(), bitmap);
	}
}
