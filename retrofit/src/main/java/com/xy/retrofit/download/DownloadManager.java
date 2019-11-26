package com.xy.retrofit.download;

import android.os.Environment;
import android.text.TextUtils;

import com.xy.common.utils.LogUtils;
import com.xy.retrofit.download.data.DownloadFile;
import com.xy.retrofit.download.data.DownloadResult;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by xieying on 2019-11-19.
 * Description：
 */
public class DownloadManager {
    //不存在此下载任务
    private static final int STATE_NO_EXIST = 0;
    //存在此下载任务，但是未下载
    private static final int STATE_EXIST_NO_DOWNLOADING = 1;
    //存在此下载任务，并且正在下载
    private static final int STATE_EXIST_DOWNLOADING = 2;

    private static final int DEFAULT_THREAD_COUNT = 3;

    private String DEFAULT_FILE_DIR;

    private static DownloadManager INSTANCE;

    private Map<String,DownloadTask> mDownloadTaskMap;

    private Disposable mAllDispose;

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

    private DownloadManager() {
        mDownloadTaskMap = new HashMap<>();

    }

    public void add(String url, DownloadListener listener) {
        add(url, null, null, listener);
    }

    public void add(String url, String filePath, DownloadListener listener) {
        add(url, filePath, null, listener);
    }

    /**
     * 添加下载任务，注意：只是添加，未开始下载，下载需要调用download方法
     *
     * @param url      下载地址
     * @param filePath 文件路径 为null则使用默认的
     * @param fileName 文件名 为null则根据url获取
     * @param listener 下载回调
     */
    public void add(String url, String filePath, String fileName, DownloadListener listener) {
        if (TextUtils.isEmpty(fileName)) {
            filePath = getDefaultDirectory();
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = getFileName(url);
        }
        //添加任务的时候判断该任务是否正在下载，如果正在下载则不添加
        if (!isDownloading(url)) {
            DownloadFile downloadFile = new DownloadFile(url, fileName, filePath);
            addDownloadTask(downloadFile,listener);
        }
    }

    public void addDownloadTask(DownloadFile downloadFile, DownloadListener downloadListener) {
        DownloadTask downloadTask = new DownloadTask(downloadFile);
        downloadTask.setDownloadListener(downloadListener);
        mDownloadTaskMap.put(downloadFile.getUrl(), downloadTask);
    }

    public void startDownload(String... urls) {
        for (String url : urls) {
            if (mDownloadTaskMap.containsKey(url)) {
                if (!isDownloading(url)) {
                    new Thread(mDownloadTaskMap.get(url)).start();
                }
            }
        }
    }

    public void startDownload(DownloadFile downloadFile, DownloadListener downloadListener) {
        switch (getUrlState(downloadFile.getUrl())) {
            case STATE_NO_EXIST:
                addDownloadTask(downloadFile, downloadListener);
                startDownload(downloadFile.getUrl());
                break;
            case STATE_EXIST_NO_DOWNLOADING:
                addDownloadListener(downloadFile.getUrl(), downloadListener);
                startDownload(downloadFile.getUrl());
                break;
            case STATE_EXIST_DOWNLOADING:
                addDownloadListener(downloadFile.getUrl(), downloadListener);
                break;
        }
    }

    public void addDownloadListener(String url, DownloadListener downloadListener) {
        if (mDownloadTaskMap.containsKey(url)) {
            mDownloadTaskMap.get(url).setDownloadListener(downloadListener);
        }
    }


    /**
     * 暂停指定url的任务
     *
     * @param urls 下载地址
     */
    public void pause(String... urls) {
        for (String url : urls) {
            if (mDownloadTaskMap.containsKey(url)) {
                mDownloadTaskMap.get(url).pause();
                mDownloadTaskMap.remove(url);
            }
        }
    }

    /**
     * 暂停所有任务
     */
    public void pauseAll() {
        Set<String> urls = mDownloadTaskMap.keySet();
        for (String url : urls) {
            if (mDownloadTaskMap.get(url).isDownloading()) {
                mDownloadTaskMap.get(url).pause();
                mDownloadTaskMap.remove(url);
            }
        }
    }

    /**
     * 取消指定url的任务
     *
     * @param urls 下载地址
     */
    public void cancel(String... urls) {
        for (String url : urls) {
            if (mDownloadTaskMap.containsKey(url)) {
                mDownloadTaskMap.get(url).cancel();
                mDownloadTaskMap.remove(url);
            }
        }
    }

    /**
     * 取消所有任务
     */
    public void cancelAll() {
        Set<String> urls = mDownloadTaskMap.keySet();
        for (String url : urls) {
            mDownloadTaskMap.get(url).cancel();
            mDownloadTaskMap.remove(url);
        }
    }

    /**
     * 获取默认下载路径
     *
     * @return 下载路径
     */
    private String getDefaultDirectory() {
        if (TextUtils.isEmpty(DEFAULT_FILE_DIR)) {
            DEFAULT_FILE_DIR = Environment.getExternalStorageDirectory() + File.separator
                    + "ZYPlay" + File.separator;
        }
        return DEFAULT_FILE_DIR;
    }

    /**
     * 根据url获取文件名
     *
     * @param url 下载地址
     * @return
     */
    private String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    /**
     * 判断url是否正在下载
     *
     * @param urls 下载地址
     * @return 是否正在下载
     */
    public boolean isDownloading(String... urls) {
        boolean result = false;
        for (String url : urls) {
            if (mDownloadTaskMap.containsKey(url))
                result = mDownloadTaskMap.get(url).isDownloading();
        }
        return result;
    }

    public int getUrlState(String url) {
        if (mDownloadTaskMap.containsKey(url)) {
            if (mDownloadTaskMap.get(url).isDownloading()) {
                return STATE_EXIST_DOWNLOADING;
            } else {
                return STATE_EXIST_NO_DOWNLOADING;
            }
        } else {
            return STATE_NO_EXIST;
        }
    }


    //------------------------------串行下载相关-----------------------------------------
    private String currentDownloadUrl;

    private DownloadTask currentDownloadTask;

    private ArrayDeque<DownloadTask> mDownloadTaskArrayDeque = new ArrayDeque<>();

    private float downloadProgress = 0;

    private Observer<DownloadResult> mWholeDownloadObserver = new Observer<DownloadResult>() {
        @Override
        public void onSubscribe(Disposable d) {
            mAllDispose = d;
        }

        @Override
        public void onNext(DownloadResult downloadResult) {
            LogUtils.d("path = " + downloadResult.getPath());
        }

        @Override
        public void onError(Throwable e) {
            LogUtils.d("error");
        }

        @Override
        public void onComplete() {
            LogUtils.d("complete");
        }
    };

    private SingleDownloadListener mSingleDownloadListener;



    public void addSerialDownloadTask(List<DownloadTask> downloadTaskList){
        List<Observable<DownloadResult>> observableList = new ArrayList<>();
        for (DownloadTask downloadTask :downloadTaskList){
            mDownloadTaskMap.put(downloadTask.getDownloadFile().getUrl(), downloadTask);
            observableList.add(downloadTask.serialDownload());
        }
        Observable.concatDelayError(observableList)
                .subscribe(mWholeDownloadObserver);
    }


    /**
     * 添加下载集合任务
     *
     * @param downloadFileList 下载文件集合
     */
    public void addSerialDownloadFile(List<DownloadFile> downloadFileList) {
        List<Observable<DownloadResult>> observableList = new ArrayList<>();
        for (DownloadFile downloadFile : downloadFileList) {
            DownloadTask downloadTask = new DownloadTask(downloadFile);
            final String url = downloadFile.getUrl();
            downloadTask.setDownloadListener(new DownloadListener() {
                @Override
                public void start() {
                    currentDownloadUrl = url;
                    sendSingleStartCallBack(url);
                }

                @Override
                public void success(String path) {
                    sendSingleSuccessCallBack(url, path);
                }

                @Override
                public void progress(float progress) {
                    sendSingleProgressCallBack(url, progress);
                }

                @Override
                public void pause() {
                    sendSinglePauseCallBack(url);
                }

                @Override
                public void fail() {
                    sendSingleFailCallBack(url);
                }

                @Override
                public void cancel() {
                    sendSingleCancelCallBack(url);
                }
            });
            observableList.add(downloadTask.serialDownload());
            mDownloadTaskMap.put(url, downloadTask);
        }

        Observable.concatDelayError(observableList)
                .subscribe(mWholeDownloadObserver);
    }

    public void cancelAllTask(){
        if (mAllDispose != null && !mAllDispose.isDisposed()) {
            mAllDispose.dispose();
        }
    }

    public void cancelCurrentTask(){
        if (mDownloadTaskMap.containsKey(currentDownloadUrl)) {
            mDownloadTaskMap.get(currentDownloadUrl).cancel();
        }
    }

    private void sendSingleStartCallBack(String url) {
        if (mSingleDownloadListener != null) {
            mSingleDownloadListener.singleStart(url);
        }
    }

    private void sendSingleProgressCallBack(String url,float progress) {
        if (mSingleDownloadListener != null) {
            mSingleDownloadListener.singleProgress(url, progress);
        }
    }

    private void sendSingleCancelCallBack(String url) {
        if (mSingleDownloadListener != null) {
            mSingleDownloadListener.singleCancel(url);
        }
    }

    private void sendSinglePauseCallBack(String url) {
        if (mSingleDownloadListener != null) {
            mSingleDownloadListener.singlePause(url);
        }
    }

    private void sendSingleFailCallBack(String url) {
        if (mSingleDownloadListener != null) {
            mSingleDownloadListener.singleFail(url);
        }
    }

    private void sendSingleSuccessCallBack(String url,String path) {
        if (mSingleDownloadListener != null) {
            mSingleDownloadListener.singleSuccess(url,path);
        }
    }

    public void setSingleDownloadListener(SingleDownloadListener singleDownloadListener) {
        mSingleDownloadListener = singleDownloadListener;
    }
}
