package com.xy.retrofit.net;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xieying on 2019/9/25.
 * Description：
 */
public class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return addPlatform(chain, request);
    }

    /**
     *
     * 这里可以配置token信息
     */
    private Response addPlatform(@NonNull Chain chain, Request request) throws IOException {
        Request newRequest = request.newBuilder()
                .addHeader("", "")
                .build();
        return chain.proceed(newRequest);
    }
}
