package com.cutler.template.common.media;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ResourceDrawableFetcher extends MediaFetcher {

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
