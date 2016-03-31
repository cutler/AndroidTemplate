package com.cutler.template.transload.download;

import com.cutler.template.base.Template;
import com.cutler.template.transload.common.AbstractManager;
import com.cutler.template.transload.common.InnerConfig;
import com.cutler.template.transload.common.TransferableFile;
import com.cutler.template.transload.download.model.DownloadFile;
import com.cutler.template.base.util.io.StorageUtils;

import java.io.File;

/**
 * 本类负责接收外界的下载请求。
 *
 * @author cutler
 */
public class DownloadManager extends AbstractManager<Downloader> {
    private static DownloadManager instance;

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
        return StorageUtils.getDiskCacheDir(Template.getApplication(),
                InnerConfig.CACHE_DOWNLOAD);
    }

    /**
     * 向下载队列中添加任务。
     *
     * @param downloadUrl 下载地址。
     * @param useCache    如果本地已经有缓存文件了，是否直接使用它。
     * @param fileName    下载文件保存到本地的名字。
     */
    public DownloadFile executeDownload(String downloadUrl, boolean useCache, String fileName) {
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setUrl(downloadUrl);
        downloadFile.setUseCache(useCache);
        downloadFile.setFileName(fileName);
        getInstance().service(AbstractManager.TransloadAction.ADD, downloadFile);
        return downloadFile;
    }
}
