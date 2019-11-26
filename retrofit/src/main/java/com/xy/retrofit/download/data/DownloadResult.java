package com.xy.retrofit.download.data;

/**
 * Created by xieying on 2019-11-26.
 * Description：下载结果
 */
public class DownloadResult {

    private String url;

    private String name;

    //文件地址，仅当state为2的状态下，该字段才有值
    private String path;

    //0：未下载 1：下载失败 2：下载成功
    private int state = 0;

    private Throwable mThrowable;

    public DownloadResult(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public void setThrowable(Throwable throwable) {
        mThrowable = throwable;
    }
}
