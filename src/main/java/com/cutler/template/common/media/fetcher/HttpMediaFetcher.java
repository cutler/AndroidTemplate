package com.cutler.template.common.media.fetcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.widget.Toast;

import com.cutler.template.MainApplication;
import com.cutler.template.R;
import com.cutler.template.common.Config;
import com.cutler.template.util.CryptoUtil;
import com.cutler.template.util.IOUtil;
import com.cutler.template.util.StorageUtils;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;

/**
 * 从网上下载媒体文件
 * @author cutler
 */
public class HttpMediaFetcher extends AbstractMediaFetcher {
	// 具有3个线程的线程池。
	private static ExecutorService threadPool;
	// 本地磁盘缓存。
	private static DiskLruCache mDiskLruCache;
	// 标识是否进行过初始化操作。
	private static boolean isInitial;
	// 图片文件的后缀名。
	private static List<String> imgExts;

	public HttpMediaFetcher(Context ctx, String desc) {
		super(ctx, desc);
		if (!isInitial) {
			synchronized (HttpMediaFetcher.class) {
				if (!isInitial) {
					threadPool = Executors.newFixedThreadPool(Config.HTTP_DOWNLOAD_THREAD_POOL_SIZE);
					imgExts = new ArrayList<String>();
					imgExts.add(".png");
					imgExts.add(".jpg");
					isInitial = true;
				}
			}
		}
	}
	
	static {
		initDiskLruCache();
	}
	
	private static void initDiskLruCache() {
		try {
			mDiskLruCache = DiskLruCache.open(StorageUtils.getDiskCacheDir(MainApplication.getInstance(), Config.CACHE_MEDIA), 1, 1, Config.DISK_CACHE_SIZE);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(MainApplication.getInstance(),
					MainApplication.getInstance().getString(R.string.client_exception_disklrucache_open_lost), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void fetch() {
		DiskLruCache.Snapshot snapshot = checkLocalCache(getDesc());
		if (snapshot == null) { // 若本地没有缓存。
			threadPool.execute(new HttpRunnable());
		} else {
			loadMediaFromLocalCache(snapshot);
		}
	}
	
	/**
	 * 从本地或者网络上加载媒体文件。
	 */
	class HttpRunnable implements Runnable {
		public void run() {
			if(checkToCancel()) return;
			Snapshot snapshot = null;
			String key = CryptoUtil.getSha256Text(getDesc());
			try {
				DiskLruCache.Editor editor = mDiskLruCache.edit(key);
				if (editor != null) {
					if (downloadUrlToStream(getDesc(),editor.newOutputStream(Config.DISK_CACHE_INDEX))) {
						// 从网络上下载成功。
						editor.commit();
					} else {
						editor.abort();
						// 获取InputStream失败，则直接返回。 如无网络时就会为null。
						notifyFetched(false, null);
					}
				}
				// 再次尝试从本地获取该文件。
				snapshot = mDiskLruCache.get(key);
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadMediaFromLocalCache(snapshot);
		}
	}
	
	/**
	 * 检测desc是否已经存在与本地缓存中。
	 * @param desc
	 * @return 若本地没有缓存，则返回null。
	 */
	public static DiskLruCache.Snapshot checkLocalCache(String desc) {
		String key = CryptoUtil.getSha256Text(desc);
		DiskLruCache.Snapshot snapshot = null;
		try {
			if(mDiskLruCache == null){
				initDiskLruCache();
			}
			snapshot = mDiskLruCache.get(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return snapshot;
	}
	
	/**
	 * 从本地缓存中加载数据。
	 */
	private void loadMediaFromLocalCache(DiskLruCache.Snapshot snapshot){
		if (snapshot != null) {
			FileInputStream fileInputStream = null;
			FileDescriptor fileDescriptor = null;
			try {
				fileInputStream = (FileInputStream) snapshot.getInputStream(Config.DISK_CACHE_INDEX);
				fileDescriptor = fileInputStream.getFD();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
                if (fileDescriptor == null && fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {}
                }
            }
			BitmapDrawable drawable = null;
	        // 若当前文件是图片文件。
	        String ext = getDesc().substring(getDesc().lastIndexOf("."));
	        if(imgExts.contains(ext)){
	        	Bitmap bitmap = null;
	        	if (fileDescriptor != null) {
	        		bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
	        	}
	        	drawable = new BitmapDrawable(getContext().getResources(),bitmap);
				// 通知数据下载完成。
				notifyFetched(bitmap != null, bitmap != null ?drawable:null);
	        } else {
	        	notifyFetched(fileDescriptor != null, null);
	        }
			IOUtil.closeInputStream(fileInputStream);
		}
	}
	
	/**
	 * 从网络上下载媒体文件。
	 */
	public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        disableConnectionReuseIfNecessary();
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), Config.IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, Config.IO_BUFFER_SIZE);

            int b;
            int curSize = 0;
            while ((b = in.read()) != -1) {
            	curSize ++ ;
                out.write(b);
                if(curSize % Config.IO_BUFFER_SIZE == 0){	// 每下载8k数据，通知以下数据改变。
                	// 通知数据加载进度改变。
                	notifyFetchingProgress(curSize, urlConnection.getContentLength());
                }
            }
            // 下载完成后，再次通知数据加载进度改变。
            notifyFetchingProgress(curSize, urlConnection.getContentLength());
            return true;
        } catch (final IOException e) {
        	e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            IOUtil.closeOutputStream(out);
            IOUtil.closeInputStream(in);
        }
        return false;
    }
	
    /**
     * Workaround for bug pre-Froyo, see here for more info:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    public static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
