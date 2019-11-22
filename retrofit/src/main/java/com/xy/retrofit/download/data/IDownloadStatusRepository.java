package com.xy.retrofit.download.data;

/**
 * Created by xieying on 2019-11-22.
 * Description：
 */
public interface IDownloadStatusRepository {

    DownloadStatus getDownloadStatusByName(String name);

    void insertDownloadStatus(DownloadStatus downloadStatus);

    void updateCurrentIndexByName(String name,long currentIndex);

    void updateStatusByName(String name,int status);

    void deleteDataByUrl(String url);

    void deleteDataByName(String name);
}
