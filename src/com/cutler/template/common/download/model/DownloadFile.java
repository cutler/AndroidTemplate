package com.cutler.template.common.download.model;

/**
 * 描述一个下载文件
 * @author cutler
 */
public class DownloadFile {
	private String url; 			// 下载地址
	private String fileName; 		// 用于在界面上显示。
	
	private boolean useCache; 		// 是否使用本地已经存在的文件。
	private boolean deleteCache; 	// 是否删除本地的缓存文件。

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

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

	public boolean isDeleteCache() {
		return deleteCache;
	}

	public void setDeleteCache(boolean deleteCache) {
		this.deleteCache = deleteCache;
	}

}
