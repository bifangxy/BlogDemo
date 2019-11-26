package com.xy.retrofit.download;

/**
 * Created by xieying on 2019-11-26.
 * Description：
 */
public interface SingleDownloadListener {
    void singleStart(String url);

    void singleProgress(String url, float progress);

    void singleSuccess(String url, String path);

    void singleCancel(String url);

    void singlePause(String url);

    void singleFail(String url);
}
