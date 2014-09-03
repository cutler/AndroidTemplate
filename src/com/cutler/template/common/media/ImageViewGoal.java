package com.cutler.template.common.media;

import java.lang.ref.WeakReference;

import com.cutler.template.common.media.MediaManager.ImageLoaderCallback;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;

public class ImageViewGoal extends MediaFetchingGoal {
	// 内部掌握有ImageView的引用，当图片下载完毕后，将图片设置到此ImageView控件上。
	private WeakReference<ImageView> mImageView;
	private ImageLoaderCallback mCallback;
	private String mDesc;

	public ImageViewGoal(String desc, ImageView imageView,final int defImgId) {
		// 现将当前显示的图片给清空。
        synchronized (imageView) {
            imageView.setTag(desc);
            if(defImgId == -1){
                imageView.setImageDrawable(null);
            } else if(defImgId != 0){
                imageView.setImageResource(defImgId);
            }
        }
		mImageView = new WeakReference<ImageView>(imageView);
		mDesc = desc;
	}

    public static void loadImageDirectly(String desc, ImageView imageView, Drawable media) {
        synchronized (imageView) {
            imageView.setTag(desc);
            imageView.setImageDrawable(media);
        }
    }

	public ImageViewGoal(String desc,ImageView imageView, ImageLoaderCallback callback,int defImgId) {
		this(desc,imageView,defImgId);
		this.mCallback = callback;
	}

	public void setmDesc(String mDesc) {
		this.mDesc = mDesc;
	}

	@Override
	public boolean isActive() {
		boolean isActive = false;
        ImageView imageView = mImageView.get();
		if (imageView != null && mDesc.equals(imageView.getTag())) {
			isActive = true;
		}
		return isActive;
	}
	
	@Override
	public void onFetched(final boolean success,final Object media) {
		// 只有当前Goal对象保存的desc，和ImageView最后一次tag内保存的desc相同时，
        final ImageView imageView = mImageView.get();
		if (isActive()) {
			// 主线程中修改ImageView。
			new Handler(imageView.getContext().getMainLooper()).post(new Runnable() {
				public void run() {
					if(imageView != null){
                        synchronized (imageView) {
                            if (mDesc.equals(imageView.getTag())) {
                            	if(media != null){
                            		imageView.setImageDrawable((Drawable) media);
                            	}
                                if(mCallback != null) {
                                    mCallback.onImageLoadFinish(success);
                                }
                            }
                        }
					}
                    mImageView = null;
				}
			});
		}
	}

	@Override
	public void onFetchingProgress(final int curSize,final int totalSize) {
        ImageView imageView = mImageView.get();
		if (mCallback != null && imageView != null) {
			// 主线程中修改ImageView。
			new Handler(imageView.getContext().getMainLooper()).post(new Runnable() {
				public void run() {
					if(mCallback != null){
						mCallback.onProgressChange(curSize, totalSize);
					}
				}
			});
			
		}
	}

}
