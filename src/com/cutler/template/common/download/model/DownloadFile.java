package com.cutler.template.common.download.model;

/**
 * 描述一个下载文件
 */
public class DownloadFile {
	private String url; 		// 下载地址
	private boolean useCache; 	// 是否使用本地已经存在的文件。

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return url;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

}
