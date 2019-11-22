package com.xy.retrofit.download;

import com.xy.retrofit.download.data.DownloadFile;

/**
 * Created by xieying on 2019-11-19.
 * Description：
 */
public class DownloadManager {

    private static DownloadManager INSTANCE;

    private DownloadTask mDownloadTask;


    public static DownloadManager getInstance(){
        if(INSTANCE == null){
            synchronized (DownloadManager.class){
                if(INSTANCE == null){
                    INSTANCE = new DownloadManager();
                }
            }
        }
        return INSTANCE;
    }


    public void startDownload(DownloadFile downloadFile){
        mDownloadTask = new DownloadTask(downloadFile);
        new Thread(mDownloadTask).start();
    }

    public void cancel(){
        mDownloadTask.cancel();
    }

    public void pause(){
        mDownloadTask.pause();
    }

    public void setDownloadListener(DownloadListener downloadListener){
        mDownloadTask.setDownloadListener(downloadListener);
    }
}
