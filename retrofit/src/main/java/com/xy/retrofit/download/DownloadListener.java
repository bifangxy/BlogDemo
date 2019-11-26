package com.xy.retrofit.download;

/**
 * Created by xieying on 2019-11-21.
 * Description：
 */
public interface DownloadListener {

    void start();

    void success(String path);

    void progress(float progress);

    void pause();

    void fail();

    void cancel();

}
