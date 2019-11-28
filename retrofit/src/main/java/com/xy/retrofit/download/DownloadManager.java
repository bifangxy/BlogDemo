package com.xy.retrofit.download;

import android.os.Environment;
import android.text.TextUtils;

import com.xy.common.utils.LogUtils;
import com.xy.retrofit.download.data.DownloadFile;
import com.xy.retrofit.download.data.DownloadResult;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

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

    private String DEFAULT_FILE_DIR;

    private static DownloadManager INSTANCE;

    private Map<String, DownloadTask> mDownloadTaskMap;

    private SingleDownloadListener mSingleDownloadListener;

    private Disposable mAllDispose;

    private String currentDownloadUrl;

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

    public static DownloadManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DownloadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadManager();
                }
            }
        }
        return INSTANCE;
    }

    private DownloadManager() {
        mDownloadTaskMap = new LinkedHashMap<>();
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
            addDownloadFile(downloadFile, listener);
        }
    }

    public void addDownloadFile(final DownloadFile downloadFile, final DownloadListener downloadListener) {
        DownloadTask downloadTask = new DownloadTask(downloadFile);
        downloadTask.setDownloadListener(downloadListener);
        addDownloadTask(downloadTask);
    }

    /**
     * 添加下载集合任务
     *
     * @param downloadFileList 下载文件集合
     */
    public void addDownloadFile(List<DownloadFile> downloadFileList) {
        for (DownloadFile downloadFile : downloadFileList) {
            final String url = downloadFile.getUrl();
            DownloadListener downloadListener = new DownloadListener() {
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
            };
            addDownloadFile(downloadFile,downloadListener);
        }
    }

    private void addDownloadTask(DownloadTask downloadTask) {
        if (downloadTask == null) {
            return;
        }
        mDownloadTaskMap.put(downloadTask.getDownloadFile().getUrl(), downloadTask);
    }

    public void addDownloadTask(List<DownloadTask> downloadTaskList) {
        for (DownloadTask downloadTask : downloadTaskList) {
            addDownloadTask(downloadTask);
        }
    }

    public void startDownload(DownloadFile downloadFile, DownloadListener downloadListener) {
        switch (getUrlState(downloadFile.getUrl())) {
            case STATE_NO_EXIST:
                addDownloadFile(downloadFile, downloadListener);
                startDownload(downloadFile.getUrl());
                break;
            case STATE_EXIST_NO_DOWNLOADING:
                startDownload(downloadFile.getUrl());
                break;
            case STATE_EXIST_DOWNLOADING:
                addDownloadListener(downloadFile.getUrl(), downloadListener);
                break;
        }
    }

    private void startDownload(String url) {
        DownloadTask downloadTask = mDownloadTaskMap.get(url);
        if (downloadTask != null) {
            currentDownloadUrl = url;
            downloadTask.startDownload();
        }
    }

    public void addDownloadListener(String url, DownloadListener downloadListener) {
        DownloadTask downloadTask = mDownloadTaskMap.get(url);
        if (downloadTask != null) {
            downloadTask.setDownloadListener(downloadListener);
        }
    }

    /**
     * 判断url是否正在下载
     *
     * @param url 下载地址
     * @return 是否正在下载
     */
    public boolean isDownloading(String url) {
        boolean result = false;
        DownloadTask downloadTask = mDownloadTaskMap.get(url);
        if (downloadTask != null) {
            result = downloadTask.isDownloading();
        }
        return result;
    }

    /**
     * 获取当前url的状态
     * @param url url
     * @return 状态
     */
    private int getUrlState(String url) {
        DownloadTask downloadTask = mDownloadTaskMap.get(url);
        if (downloadTask != null) {
            if (downloadTask.isDownloading()) {
                return STATE_EXIST_DOWNLOADING;
            } else {
                return STATE_EXIST_NO_DOWNLOADING;
            }
        } else {
            return STATE_NO_EXIST;
        }
    }


    /**
     * 开始并行下载
     */
    public void startParallelDownload() {
        Observable.mergeDelayError(getAllDownloadObserver())
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.d("all dispose");
                    }
                })
                .subscribe(mWholeDownloadObserver);
    }

    /**
     * 开始串行下载
     */
    public void startSerialDownload() {
        Observable.concatDelayError(getAllDownloadObserver())
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.d("all dispose");
                    }
                })
                .subscribe(mWholeDownloadObserver);
    }

    private List<Observable<DownloadResult>> getAllDownloadObserver() {
        List<Observable<DownloadResult>> observableList = new ArrayList<>();
        for (DownloadTask downloadTask : mDownloadTaskMap.values()) {
            observableList.add(downloadTask.getDownloadObservable());
        }
        return observableList;
    }

    public void cancelTask(String... urls) {
        for (String url : urls) {
            DownloadTask downloadTask = mDownloadTaskMap.get(url);
            if (downloadTask != null) {
                downloadTask.cancel();
                mDownloadTaskMap.remove(url);
            }
        }
    }

    /**
     * 仅针对串行下载和单个下载，并行下载不起作用
     */
    public void cancelCurrentTask() {
        cancelTask(currentDownloadUrl);
    }


    public void cancelAllTask() {
        if (mAllDispose != null && !mAllDispose.isDisposed()) {
            mAllDispose.dispose();
        }
    }

    public void pauseTask(String... urls) {
        for (String url : urls) {
            DownloadTask downloadTask = mDownloadTaskMap.get(url);
            if (downloadTask != null && downloadTask.isDownloading()) {
                downloadTask.pause();
            }
        }
    }

    public void pauseCurrentTask() {
        if (mDownloadTaskMap.containsKey(currentDownloadUrl)) {
            mDownloadTaskMap.get(currentDownloadUrl).pause();
        }
    }

    /**
     * 暂停所有任务
     */
    public void pauseAllTask() {
        Set<String> urls = mDownloadTaskMap.keySet();
        for (String url : urls) {
            pauseTask(url);
        }
    }

    private void sendSingleStartCallBack(String url) {
        if (mSingleDownloadListener != null) {
            mSingleDownloadListener.singleStart(url);
        }
    }

    private void sendSingleProgressCallBack(String url, float progress) {
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

    private void sendSingleSuccessCallBack(String url, String path) {
        if (mSingleDownloadListener != null) {
            mSingleDownloadListener.singleSuccess(url, path);
        }
    }

    public void setSingleDownloadListener(SingleDownloadListener singleDownloadListener) {
        mSingleDownloadListener = singleDownloadListener;
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
}
