package com.cutler.testapplication.test.transload;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.cutler.template.base.Template;
import com.cutler.template.transload.common.AbstractManager;
import com.cutler.template.transload.common.TransferObserver;
import com.cutler.template.transload.download.DownloadManager;
import com.cutler.template.transload.download.model.DownloadFile;

import java.util.Map;

public class DownloadTest {

    static TransferObserver observer = new TransferObserver() {
        @Override
        public void onTransferDataChanged(TransferObserver.TransferState state, Map<String, Object> params) {
            Object file = params.get(TransferObserver.KEY_FILE);
            if (file instanceof DownloadFile) {
                DownloadFile downloadFile = (DownloadFile) file;
                if (!downloadFile.getUrl().equals(downloadFile.getUrl())) {
                    return;
                }
                switch (state) {
                    case ADDED:
                        System.out.println("add " + downloadFile.getFileName());
                        break;
                    case PROGRESS:
                        System.out.println("progress " + downloadFile.getFileName() + " " + params.get(TransferObserver.KEY_PROGRESS));
                        break;
                    case FINISHED:
                        System.out.println("finish " + downloadFile.getFileName());
                        DownloadManager.getInstance().removeObserver(this);
                        String localPath = params.get(TransferObserver.KEY_LOCAL_PATH).toString();
                        if (localPath.endsWith("apk")) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://" + localPath), "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Template.getApplication().startActivity(intent);
                        }
                        break;
                    case ERROR:
                    case DELETED:
                        if (state == TransferObserver.TransferState.ERROR) {
                            DownloadManager.getInstance().service(AbstractManager.TransloadAction.DELETE, downloadFile);
                            System.out.println("ERROR " + downloadFile.getFileName());
                        } else {
                            System.out.println("DELETED " + downloadFile.getFileName());
                        }
                        break;
                }
            }
        }
    };

    public static void testDownload() {
        DownloadManager.getInstance().addObserver(observer);

        // 需要注意的是，下载使用到了AsyncTask类，因此如果你想让多个任务同时执行，则可以把项目的targetSdkVersion设置为9
        // 当然也可以通过其他方式来让多个AsyncTask类同时执行，具体请自行搜索。


        // 下载微信
        final String wxUrl = "http://gdown.baidu.com/data/wisegame/1b9392eadc3bddf1/WeChat_480.apk";
        final DownloadFile wxFile = DownloadManager.getInstance().executeDownload(wxUrl, true, "WeChat_480.apk");

        //　下载qq
        final String qqUrl = "http://gdown.baidu.com/data/wisegame/2c6a60c5cb96c593/QQ_182.apk";
        final DownloadFile qqFile = DownloadManager.getInstance().executeDownload(qqUrl, false, "QQ_182.apk");

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // 10秒后暂定所有下载。
                DownloadManager.getInstance().service(AbstractManager.TransloadAction.PAUSE_ALL);
                // 同时取消微信下载。
                DownloadManager.getInstance().service(AbstractManager.TransloadAction.DELETE, wxFile);
            }
        }, 10000);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                System.out.println("准备接着搞");
                DownloadManager.getInstance().service(AbstractManager.TransloadAction.CONTINUE, qqFile);
            }
        }, 14000);

    }
}
