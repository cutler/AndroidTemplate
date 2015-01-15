package com.cutler.template.common.transloader.download;

import java.io.File;

import com.cutler.template.MainApplication;
import com.cutler.template.common.Config;
import com.cutler.template.common.transloader.common.AbstractManager;
import com.cutler.template.common.transloader.common.TransferableFile;
import com.cutler.template.util.StorageUtils;

/**
 * 本类负责接收外界的下载请求。
 * 
 * @author cutler
 */
public class DownloadManager extends AbstractManager<Downloader> {
	private static DownloadManager instance;

	// 下载器对象。
	private DownloadManager() {
		super(new Downloader());
	}

	public static DownloadManager getInstance() {
		if (instance == null) {
			synchronized (DownloadManager.class) {
				if (instance == null) {
					instance = new DownloadManager();
				}
			}
		}
		return instance;
	}

	@Override
	protected void doService(int type, TransferableFile... files) {
	}

	/**
	 * 返回本地保存下载文件的目录。
	 * 
	 * @return
	 */
	public File getLocalDownloadFolder() {
		return StorageUtils.getDiskCacheDir(MainApplication.getInstance(),
				Config.CACHE_DOWNLOAD);
	}
}
