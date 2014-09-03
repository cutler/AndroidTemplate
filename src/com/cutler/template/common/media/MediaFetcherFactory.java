package com.cutler.template.common.media;

import android.content.Context;

public class MediaFetcherFactory {

	/**
	 * 依据指定的desc参数值，来创建不同的MediaFetcher对象。
	 * @param desc
	 * @return
	 */
	public static MediaFetcher createFetcher(Context context,String desc){
		MediaFetcher fetcher = null;
		if(desc != null){
			if(desc.startsWith(MediaManager.RES_SCHEMA)){		// 本地资源
				fetcher = new ResourceDrawableFetcher(context,desc);
			} else if(desc.startsWith(MediaManager.HTTP_SCHEMA)) {	// http文件。
				fetcher = new HttpMediaFetcher(context,desc);
			}
		}
		return fetcher;
	}
}
