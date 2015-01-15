package com.cutler.template.common.media.fetcher;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * 从res下加载图片。
 * @author cutler
 */
public class ResourceDrawableFetcher extends AbstractMediaFetcher {

	public ResourceDrawableFetcher(Context ctx, String desc) {
		super(ctx, desc);
	}

	@Override
	public void fetch() {
		String resId = trimDescSchema();
		// 加载资源图片。
		Drawable drawable = null;
		try {
			drawable = getContext().getResources().getDrawable(Integer.parseInt(resId));
			notifyFetched(true, drawable);
		} catch (Exception e) {
			e.printStackTrace();
			notifyFetched(false, null);
		}
	}

}
