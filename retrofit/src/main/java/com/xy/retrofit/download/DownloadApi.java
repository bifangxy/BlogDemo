package com.xy.retrofit.download;

import com.xy.retrofit.net.RetrofitService;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by xieying on 2019-11-19.
 * Description：
 */
public interface DownloadApi {
    DownloadApi api = RetrofitService.create(DownloadApi.class);


    @Streaming/*大文件需要加入这个判断，防止下载过程中写入到内存中*/
    @GET
    Observable<ResponseBody> download(@Url String url);

    /*断点续传下载接口*/
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("RANGE") String start, @Url String url);

}
