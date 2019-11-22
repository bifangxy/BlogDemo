package com.xy.retrofit.net;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xieying on 2019/9/25.
 * Description：
 */
public class RetrofitService {

    private static final RetrofitService mService = new RetrofitService();

    private Retrofit mRetrofit;

    public RetrofitService() {
        mRetrofit = initRetrofit(NetConfiguration.create());
    }

    private Retrofit initRetrofit(NetConfiguration netConfiguration) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(netConfiguration.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(netConfiguration.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(netConfiguration.getTimeout(), TimeUnit.MILLISECONDS)
                .proxy(Proxy.NO_PROXY);
        if (netConfiguration.isLogEnable()) {
            builder.addNetworkInterceptor(netConfiguration.getLogInterceptor());
        }
        OkHttpClient okHttpClient = builder.build();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(netConfiguration.getHost())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T create(final Class<T> service) {
        return mService.mRetrofit.create(service);
    }
}
