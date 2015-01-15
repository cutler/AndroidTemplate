package com.cutler.template.common.transloader.upload;

import com.cutler.template.common.transloader.common.AbstractManager;
import com.cutler.template.common.transloader.common.TransferableFile;

/**
 * 类负责接收外界的上传请求。
 * 
 * @author cutler
 */
public class UploadManager extends AbstractManager<Uploader> {
	private static UploadManager instance;

	public UploadManager() {
		super(new Uploader());
	}

	public static UploadManager getInstance() {
		if (instance == null) {
			synchronized (UploadManager.class) {
				if (instance == null) {
					instance = new UploadManager();
				}
			}
		}
		return instance;
	}

	@Override
	protected void doService(int type, TransferableFile... files) {
	}

	/**
	 * 文件上传的类型
	 * 
	 * @author cutler
	 */
	public enum UploadType {
		/**
		 * 使用HttpURLConnection上传文件。 是默认的上传方式。
		 */
		NORMARL,
	}
}
