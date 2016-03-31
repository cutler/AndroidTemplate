package com.cutler.template.transload.upload;


import com.cutler.template.transload.common.AbstractManager;
import com.cutler.template.transload.common.TransferableFile;
import com.cutler.template.transload.upload.model.UploadFile;

/**
 * 类负责接收外界的上传请求。
 *
 * @author cutler
 */
public class UploadManager extends AbstractManager<Uploader> {
    private static UploadManager instance;

    private UploadManager() {
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
     * 向上传队列中添加任务。
     *
     * @param localPath 文件的本地路径
     * @param uploadUrl 上传地址
     */
    public static UploadFile executeUpload(String localPath, String uploadUrl) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setUrl(uploadUrl);
        uploadFile.setLocalPath(localPath);
        getInstance().service(AbstractManager.TransloadAction.ADD, uploadFile);
        return uploadFile;
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
        NORMAL,
    }
}
