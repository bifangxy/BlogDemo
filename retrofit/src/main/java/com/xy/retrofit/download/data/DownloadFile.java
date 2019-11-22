package com.xy.retrofit.download.data;

/**
 * Created by xieying on 2019/7/21.
 * Description：下载文件
 */
public class DownloadFile {

    private String fileName;

    private String url;

    private String filePath;

    //是否需要多线程下载
    private boolean needMultithreading;

    //如果文件存在，是否需要覆盖
    private boolean needOverrideFile;

    public DownloadFile(String fileName, String url, String filePath) {
        this.fileName = fileName;
        this.url = url;
        this.filePath = filePath;
    }

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isNeedMultithreading() {
        return needMultithreading;
    }

    public void setNeedMultithreading(boolean needMultithreading) {
        this.needMultithreading = needMultithreading;
    }

    public boolean isNeedOverrideFile() {
        return needOverrideFile;
    }

    public void setNeedOverrideFile(boolean needOverrideFile) {
        this.needOverrideFile = needOverrideFile;
    }
}
