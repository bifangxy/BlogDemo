package com.xy.retrofit.download.data;

import android.content.Context;

/**
 * Created by xieying on 2019-11-22.
 * Description：
 */
public class DownloadStatusRepository implements IDownloadStatusRepository{
    private static volatile DownloadStatusRepository mInstance;

    private DownloadStatusDao mDownloadStatusDao;

    private AppDatabase mDb;

    private Context mContext;

    public static DownloadStatusRepository getInstance(){
        if(mInstance == null){
            synchronized (DownloadStatusRepository.class){
                if(mInstance == null){
                    mInstance = new DownloadStatusRepository();
                }
            }
        }
        return mInstance;
    }

    public DownloadStatusRepository() {
        mDb = AppDatabase.getInstance();
        mDownloadStatusDao = mDb.downloadStatusDao();
    }

    @Override
    public DownloadStatus getDownloadStatusByName(String name) {
        return mDownloadStatusDao.getDownloadStatusByName(name);
    }

    @Override
    public void insertDownloadStatus(DownloadStatus downloadStatus) {
        mDownloadStatusDao.insertDownloadStatusa(downloadStatus);
    }

    @Override
    public void updateCurrentIndexByName(String name, long currentIndex) {
        mDownloadStatusDao.updateCurrentIndexByName(name,currentIndex);
    }

    @Override
    public void updateStatusByName(String name, int status) {
        mDownloadStatusDao.updateStatusByName(name,status);
    }

    @Override
    public void deleteDataByUrl(String url) {
        mDownloadStatusDao.deleteDataByUrl(url);
    }

    @Override
    public void deleteDataByName(String name) {
        mDownloadStatusDao.deleteDataByName(name);
    }
}
