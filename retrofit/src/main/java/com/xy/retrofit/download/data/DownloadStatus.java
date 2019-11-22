package com.xy.retrofit.download.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by xieying on 2019-11-22.
 * Description：
 */
@Entity(tableName = "download_status")
public class DownloadStatus {

    @NonNull
    @PrimaryKey
    private String name;

    @NonNull
    private String url;

    @NonNull
    private long startIndex;

    @NonNull
    private long currentIndex;

    @NonNull
    private long endIndex;

    @NonNull
    //0:未开始下载 1：正在下载 2：下载完成
    private int status;

    public DownloadStatus(@NonNull String name, @NonNull String url, long startIndex, long currentIndex, long endIndex, int status) {
        this.name = name;
        this.url = url;
        this.startIndex = startIndex;
        this.currentIndex = currentIndex;
        this.endIndex = endIndex;
        this.status = status;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    public long getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(long currentIndex) {
        this.currentIndex = currentIndex;
    }

    public long getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(long endIndex) {
        this.endIndex = endIndex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
