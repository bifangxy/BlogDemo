package com.xy.retrofit.download;

import android.util.Log;

import com.xy.common.utils.LogUtils;
import com.xy.retrofit.download.data.DownloadFile;
import com.xy.retrofit.download.data.DownloadStatus;
import com.xy.retrofit.download.data.DownloadStatusRepository;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xieying on 2019-11-19.
 * Description：
 */
public class DownloadTask implements Runnable {
    private static final int MAX_THREAD_COUNT = 8;

    private volatile int mThreadCount = 3;

    private DownloadFile mDownloadFile;

    private boolean mMultithreading;

    private boolean mOverrideFile;

    private Disposable mDisposable;

    private volatile long mDownloadLength;

    private float mDownloadProgress;

    private long mContentLength;

    private File mFile;

    private File mTmpFile;

    private boolean success = true;

    private volatile boolean pause;

    private DownloadListener mDownloadListener;

    private DownloadStatusRepository mDownloadStatusRepository;

    public DownloadTask(DownloadFile downloadFile) {
        mDownloadFile = downloadFile;
        mMultithreading = downloadFile.isNeedMultithreading();
        mOverrideFile = downloadFile.isNeedOverrideFile();
    }

    @Override
    public void run() {
        startDownload(mDownloadFile);
    }

    private void startDownload(DownloadFile downloadFile) {
        mFile = new File(mDownloadFile.getFilePath(), mDownloadFile.getFileName());
        mFile.getParentFile().mkdirs();
        pause = false;

        mDownloadStatusRepository = DownloadStatusRepository.getInstance();

        getFileContentLength();
    }

    private void getFileContentLength() {
        DownloadApi.api.download(mDownloadFile.getUrl())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, Long>() {
                    @Override
                    public Long apply(ResponseBody responseBody) throws Exception {
                        long contentLength = responseBody == null ? 0 : responseBody.contentLength();
                        close(responseBody);
                        return contentLength;
                    }
                })
                .flatMap(new Function<Long, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(final Long aLong) throws Exception {
                        if (aLong == 0) {
                            return Observable.error(new Throwable("url error or internet error"));
                        }
                        mContentLength = aLong;
                        LogUtils.d("contentLength = " + aLong);
                        if (mFile.exists() && mFile.length() == aLong) {
                            //如果需要覆盖，则把之前的文件删除掉，如果不需要，则直接返回成功
                            if (mOverrideFile) {
                                mFile.delete();
                            } else {
                                return new Observable<Boolean>() {
                                    @Override
                                    protected void subscribeActual(Observer<? super Boolean> observer) {
                                        observer.onNext(true);
                                        observer.onComplete();
                                    }
                                };
                            }
                        }
                        //创建一个临时文件
                        mTmpFile = new File(mDownloadFile.getFilePath(), mDownloadFile.getFileName() + ".tmp");
                        if (!mTmpFile.exists()) {
                            mDownloadStatusRepository.deleteDataByUrl(mDownloadFile.getUrl());
                        }

                        mDownloadProgress = 0;

                        if (!mMultithreading) {
                            return downloadFile(0, aLong);
                        }
                        List<Observable<Boolean>> responseDataList = new ArrayList<>();

                        long blockSize = aLong / mThreadCount;

                        for (int threadId = 0; threadId < mThreadCount; threadId++) {
                            long startIndex = threadId * blockSize;
                            long endIndex = (threadId + 1) * blockSize - 1;
                            if (threadId == mThreadCount - 1) {
                                endIndex = aLong - 1;
                            }
                            responseDataList.add(downloadFile(startIndex, endIndex));
                        }
                        return Observable.merge(responseDataList);
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        mTmpFile.delete();
                        mDownloadStatusRepository.deleteDataByUrl(mDownloadFile.getUrl());
                        sendCancelCallBack();
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.d("onSubscribe = ");
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Boolean responseData) {
                        success = success && responseData;
                        LogUtils.d("value = " + responseData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        success = false;
                        sendFailCallBack();
                        LogUtils.d("---error---" + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        if (success) {
                            if (mTmpFile != null) {
                                mTmpFile.renameTo(mFile);
                            }
                            sendSuccessCallBack();
                        } else {
                            sendFailCallBack();
                        }
                        LogUtils.d("---complete---");
                        LogUtils.d("mDownloadProgress = " + mDownloadLength);
                        LogUtils.d("success = " + success);
                    }
                });

    }

    private Observable<Boolean> downloadFile(final long startIndex, final long endIndex) throws Exception {
        final String name = mDownloadFile.getUrl() + "-" + mDownloadFile.getFileName() + "-" + startIndex;

        DownloadStatus downloadStatus = new DownloadStatus(name, mDownloadFile.getUrl(), startIndex, 0, endIndex, 0);
        DownloadStatus dbDownloadStatus = mDownloadStatusRepository.getDownloadStatusByName(name);
        if (dbDownloadStatus != null) {
            downloadStatus = dbDownloadStatus;
        }
        mDownloadStatusRepository.insertDownloadStatus(downloadStatus);
        if (downloadStatus.getStatus() == 2) {
            return new Observable<Boolean>() {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer) {
                    observer.onNext(true);
                }
            };
        }
        long currentIndex = downloadStatus.getCurrentIndex();
        addProgress(currentIndex == 0 ? 0 : downloadStatus.getCurrentIndex() - downloadStatus.getStartIndex());
        final long realStartIndex = currentIndex == 0 ? downloadStatus.getStartIndex() : downloadStatus.getCurrentIndex();
        return DownloadApi.api.download("bytes=" + realStartIndex + "-" + endIndex, mDownloadFile.getUrl())
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        RandomAccessFile tmpAccessFile = null;
                        try {
                            LogUtils.d("downloadFile" + Thread.currentThread().getId());
                            InputStream is = responseBody.byteStream();
                            byte[] buffer = new byte[2048];
                            long progress = 0;
                            long total = 0;
                            int length;
                            tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");
                            tmpAccessFile.seek(realStartIndex);
                            while ((length = is.read(buffer)) != -1) {
                                if (pause) {
                                    return false;
                                }
                                total += length;
                                //每100kb保存一次，不然影响速度
                                if ((total - progress) > 1024 * 500) {
                                    progress = total;
                                    mDownloadStatusRepository.updateCurrentIndexByName(name, realStartIndex + progress);
                                }
                                tmpAccessFile.write(buffer, 0, length);
                                addProgress(length);
                            }
                            mDownloadStatusRepository.updateCurrentIndexByName(name, endIndex);
                            mDownloadStatusRepository.updateStatusByName(name, 2);
                        } catch (Exception e) {
                            return false;
                        } finally {
                            close(responseBody, tmpAccessFile);
                        }
                        return true;
                    }


                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.d("----doOnDispose----");
                        mDownloadStatusRepository.deleteDataByName(name);
                    }
                });
    }

    private synchronized void addProgress(long length) {
        mDownloadLength += length;
        float progress = (float) mDownloadLength / mContentLength;
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        String p = decimalFormat.format(progress);
        if (Float.valueOf(p) != mDownloadProgress) {
            mDownloadProgress = Float.valueOf(p);
            sendProgressCallBack(mDownloadProgress);
        }
    }


    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public void pause() {
        pause = true;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    private void sendProgressCallBack(float progress) {
        if (mDownloadListener != null) {
            mDownloadListener.progress(progress);
        }
    }

    private void sendCancelCallBack() {
        if (mDownloadListener != null) {
            mDownloadListener.cancel();
        }
    }

    private void sendSuccessCallBack() {
        if (mDownloadListener != null) {
            mDownloadListener.success();
        }
    }

    private void sendFailCallBack() {
        if (mDownloadListener != null) {
            mDownloadListener.fail();
        }
    }

    /**
     * 关掉IO资源
     *
     * @param closeables IO资源
     */
    private void close(Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < closeables.length; i++) {
                closeables[i] = null;
            }
        }
    }

}
