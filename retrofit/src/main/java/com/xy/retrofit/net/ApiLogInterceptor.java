package com.xy.retrofit.net;

import com.xy.common.utils.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xieying on 2019/9/25.
 * Description：
 */
public class ApiLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        LogUtils.i("request = " + request.toString());
        Response response = chain.proceed(chain.request());
        if (response.body() != null) {
            MediaType mediaType = response.body().contentType();
            String content = response.body().toString();
            LogUtils.i("response body = " + content);
            ResponseBody body = ResponseBody.create(mediaType, content);
            return response.newBuilder().body(body).build();
        } else {
            return response;
        }
    }
}
