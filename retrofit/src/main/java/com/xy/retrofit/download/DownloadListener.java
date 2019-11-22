package com.xy.retrofit.download;

/**
 * Created by xieying on 2019-11-21.
 * Description：
 */
public interface DownloadListener {

    void cancel();

    void success();

    void progress(float progress);

    void fail();

}
