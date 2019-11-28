package com.xy.retrofit.download;

import com.xy.retrofit.download.data.DownloadFile;
import com.xy.retrofit.download.data.DownloadResult;
import com.xy.retrofit.download.data.DownloadStatus;
import com.xy.retrofit.download.data.DownloadStatusRepository;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xieying on 2019-11-19.
 * Description：
 */
public class DownloadTask {
    private volatile int mThreadCount = 3;

    private DownloadFile mDownloadFile;

    private Disposable mDisposable;

    private volatile long mDownloadLength;

    private float mDownloadProgress;

    private long mContentLength;

    private File mFile;

    private File mTmpFile;

    private volatile boolean pause;

    private volatile boolean cancel;

    private DownloadListener mDownloadListener;

    private DownloadStatusRepository mDownloadStatusRepository;

    private DownloadResult mDownloadResult;

    private DecimalFormat decimalFormat;

    public DownloadTask(DownloadFile downloadFile) {
        mDownloadFile = downloadFile;
        decimalFormat = new DecimalFormat(".00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(dfs);
    }

    public void startDownload() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            return;
        }
        initDownloadData();

        download();
    }


    private void initDownloadData() {
        mFile = new File(mDownloadFile.getFilePath(), mDownloadFile.getFileName());
        mFile.getParentFile().mkdirs();

        pause = false;
        mDownloadLength = 0;
        mDownloadProgress = 0;
        mDownloadStatusRepository = DownloadStatusRepository.getInstance();

        mDownloadResult = new DownloadResult(mDownloadFile.getUrl(), mDownloadFile.getFileName());
    }

    private void download() {
        getFileContentLength()
                .flatMap(new Function<Long, ObservableSource<DownloadResult>>() {
                    @Override
                    public ObservableSource<DownloadResult> apply(final Long aLong) throws Exception {
                        return checkDownloadFile(aLong);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<DownloadResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        doSubscribe(d);
                    }

                    @Override
                    public void onNext(DownloadResult downloadResult) {
                        doNext(downloadResult);
                    }

                    @Override
                    public void onError(Throwable e) {
                        doError();
                    }

                    @Override
                    public void onComplete() {
                        doComplete();
                    }
                });
    }

    public Observable<DownloadResult> getDownloadObservable() {
        initDownloadData();
        return getFileContentLength()
                .flatMap(new Function<Long, ObservableSource<DownloadResult>>() {
                    @Override
                    public ObservableSource<DownloadResult> apply(final Long aLong) throws Exception {
                        return checkDownloadFile(aLong);
                    }
                })
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        doSubscribe(disposable);
                    }
                })
                .doOnNext(new Consumer<DownloadResult>() {
                    @Override
                    public void accept(DownloadResult downloadResult) throws Exception {
                        doNext(downloadResult);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        doComplete();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        doError();
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        sendCancelCallBack();
                    }
                });
    }




    /**
     * 获取下载文件长度
     *
     * @return 下载文件长度
     */
    private Observable<Long> getFileContentLength() {
        return DownloadApi.api.download(mDownloadFile.getUrl())
                .map(new Function<ResponseBody, Long>() {
                    @Override
                    public Long apply(ResponseBody responseBody) throws Exception {
                        long contentLength = responseBody == null ? 0 : responseBody.contentLength();
                        close(responseBody);
                        return contentLength;
                    }
                });
    }

    /**
     * 检查下载文件状态
     *
     * @param aLong 下载文件长度
     * @return 下载结果
     * @throws Exception Exception
     */
    private Observable<DownloadResult> checkDownloadFile(Long aLong) throws Exception {
        if (aLong <= 0) {
            return Observable.error(new Throwable(""));
        }
        mContentLength = aLong;

        //判断是否需要覆盖,如果需要则删除文件
        if (mFile.exists() && mDownloadFile.isNeedOverrideFile()) {
            mFile.delete();
        }
        //判断文件是否存在
        if (mFile.exists() && mFile.length() == aLong) {
            //如果存在，直接返回该文件的路径
            return Observable.create(new ObservableOnSubscribe<DownloadResult>() {
                @Override
                public void subscribe(ObservableEmitter<DownloadResult> emitter) throws Exception {
                    mDownloadResult.setState(2);
                    mDownloadResult.setPath(mFile.getAbsolutePath());
                    emitter.onNext(mDownloadResult);
                    emitter.onComplete();
                }
            });
        }
        mTmpFile = new File(mDownloadFile.getFilePath(), mDownloadFile.getFileName() + ".tmp");
        if (!mTmpFile.exists()) {
            //如果临时文件不存在，则删除数据库该url的数据
            mDownloadStatusRepository.deleteDataByUrl(mDownloadFile.getUrl());
        }
        //判断是否需要多线程下载
        if (!mDownloadFile.isNeedMultithreading()) {
            return downloadFile(0, aLong).map(new Function<Boolean, DownloadResult>() {
                @Override
                public DownloadResult apply(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        mTmpFile.renameTo(mFile);
                        mDownloadResult.setState(2);
                        mDownloadResult.setPath(mFile.getAbsolutePath());
                    } else {
                        mDownloadResult.setState(1);
                    }
                    return mDownloadResult;
                }
            });
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

        return Observable.zip(responseDataList, new Function<Object[], DownloadResult>() {
            @Override
            public DownloadResult apply(Object[] objects) throws Exception {
                boolean success = true;
                for (Object object : objects) {
                    if (object instanceof Boolean) {
                        success = success && (Boolean) object;
                    }
                }
                if (success) {
                    mTmpFile.renameTo(mFile);
                    mDownloadResult.setState(2);
                    mDownloadResult.setPath(mFile.getAbsolutePath());
                } else {
                    mDownloadResult.setState(1);
                }
                return mDownloadResult;
            }
        });
    }

    /**
     * 根据条件下载文件
     *
     * @param startIndex 下载起始位置
     * @param endIndex   下载结束位置
     * @return 下载结果
     * @throws Exception 异常
     */
    private Observable<Boolean> downloadFile(final long startIndex, final long endIndex) throws Exception {
        final String name = mDownloadFile.getUrl() + "-" + mDownloadFile.getFileName() + "-" + startIndex;
        //获取之前该片段的下载情况
        DownloadStatus downloadStatus = mDownloadStatusRepository.getDownloadStatusByName(name);
        if (downloadStatus == null) {
            downloadStatus = new DownloadStatus(name, mDownloadFile.getUrl(), startIndex, 0, endIndex, 0);
            mDownloadStatusRepository.insertDownloadStatus(downloadStatus);
        }
        //获取之前的下载位置
        long currentIndex = downloadStatus.getCurrentIndex();

        //获取真正的起始位置
        final long realStartIndex = currentIndex == 0 ? downloadStatus.getStartIndex() : currentIndex;

        //根据之前该片段的下载情况，设置下载进度
        addProgress(realStartIndex - downloadStatus.getStartIndex());

        //判断之前该片段是否已经下载完成，如果已经下载完成，则直接返回成功
        if (downloadStatus.getStatus() == 2) {
            return Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                    emitter.onNext(true);
                    emitter.onComplete();
                }
            });
        }
        return DownloadApi.api.download("bytes=" + realStartIndex + "-" + endIndex, mDownloadFile.getUrl())
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        RandomAccessFile tmpAccessFile = null;
                        try {
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
                                if (cancel) {
                                    return false;
                                }
                                total += length;
                                //每100kb保存一次，不然影响速度
                                if ((total - progress) > 1024 * 100) {
                                    progress = total;
                                    mDownloadStatusRepository.updateCurrentIndexByName(name, realStartIndex + progress);
                                }
                                tmpAccessFile.write(buffer, 0, length);
                                addProgress(length);
                            }
                            //片段下载完成，数据库进行保存
                            mDownloadStatusRepository.updateCurrentIndexByName(name, endIndex);
                            mDownloadStatusRepository.updateStatusByName(name, 2);
                            return true;
                        } catch (Exception e) {
//                            e.printStackTrace();
                            return false;
                        } finally {
                            close(responseBody, tmpAccessFile);
                        }
                    }
                });
    }

    private synchronized void addProgress(long length) {
        mDownloadLength += length;
        float progress = (float) mDownloadLength / mContentLength;
        String p = decimalFormat.format(progress);
        if (Float.valueOf(p) != mDownloadProgress) {
            mDownloadProgress = Float.valueOf(p);
            sendProgressCallBack(mDownloadProgress);
        }
    }

    public void cancel() {
        if (isDownloading()) {
            cancel = true;
        }
    }

    public void pause() {
        if (isDownloading()) {
            pause = true;
        }
    }

    private void doComplete(){
        if (mDownloadResult.getState() == 2) {
            sendSuccessCallBack();
        } else if (pause) {
            sendPauseCallBack();
        } else if (cancel) {
            sendCancelCallBack();
        } else {
            sendFailCallBack();
        }
    }

    private void doSubscribe(Disposable disposable){
        sendStartListener();
        mDisposable = disposable;
    }

    private void doNext(DownloadResult downloadResult){
        mDownloadResult = downloadResult;

    }

    private void doError(){
        sendFailCallBack();
    }

    private void sendStartListener() {
        if (mDownloadListener != null) {
            mDownloadListener.start();
        }
    }

    private void sendProgressCallBack(float progress) {
        if (mDownloadListener != null) {
            mDownloadListener.progress(progress);
        }
    }

    private void sendCancelCallBack() {
        dispose();
        mTmpFile.delete();
        mDownloadStatusRepository.deleteDataByUrl(mDownloadFile.getUrl());
        if (mDownloadListener != null) {
            mDownloadListener.cancel();
        }
    }

    private void sendSuccessCallBack() {
        dispose();
        if (mDownloadListener != null) {
            if (mTmpFile != null) {
                mTmpFile.renameTo(mFile);
            }
            mDownloadListener.success(mFile.getAbsolutePath());
        }
    }

    private void sendPauseCallBack() {
        dispose();
        if (mDownloadListener != null) {
            mDownloadListener.pause();
        }
    }

    private void sendFailCallBack() {
        dispose();
        if (mDownloadListener != null) {
            mDownloadListener.fail();
        }
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        if (downloadListener == null) {
            return;
        }
        mDownloadListener = downloadListener;
    }

    public boolean isDownloading() {
        if (mDisposable == null) {
            return false;
        }
        return !mDisposable.isDisposed();
    }

    public DownloadFile getDownloadFile() {
        return mDownloadFile;
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

    private void dispose() {
        if ((mDisposable != null && !mDisposable.isDisposed())) {
            mDisposable.dispose();
        }
    }
}
