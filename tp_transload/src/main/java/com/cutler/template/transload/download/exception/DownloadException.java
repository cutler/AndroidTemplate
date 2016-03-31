
package com.cutler.template.transload.download.exception;

public class DownloadException extends Exception {

    private String mExtra;

    public DownloadException(String message) {

        super(message);
    }

    public DownloadException(String message, String extra) {

        super(message);
        mExtra = extra;
    }

    public String getExtra() {

        return mExtra;
    }
}
