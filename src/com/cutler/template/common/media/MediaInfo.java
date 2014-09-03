package com.cutler.template.common.media;

public class MediaInfo {
	private Object media; // 媒体对象。
	private int size; // 媒体对象的大小。

	public MediaInfo(Object media, int size) {
		this.media = media;
		this.size = size;
	}

	public Object getMedia() {
		return media;
	}

	public void setMedia(Object media) {
		this.media = media;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
