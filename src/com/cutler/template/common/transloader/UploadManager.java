package com.cutler.template.common.transloader;

import com.cutler.template.common.transloader.common.AbstractManager;
import com.cutler.template.common.transloader.loader.Uploader;

public class UploadManager extends AbstractManager<Uploader>{

	private static UploadManager instance;

	private UploadManager() {
		service(TransloadAction.INITIALIZE);
	}

	@Override
	protected void doService(int type, Uploader... file) {
		// TODO Auto-generated method stub
		
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
}
