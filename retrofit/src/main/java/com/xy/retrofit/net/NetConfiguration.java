package com.xy.retrofit.net;

import okhttp3.Interceptor;

/**
 * Created by xieying on 2019/9/25.
 * Description：
 */
public class NetConfiguration {

    private long timeout = 15 * 1000;

    private String host = "http:www.baidu.com";

    private boolean logEnable = true;

    private Interceptor logInterceptor = new ApiLogInterceptor();

    private Interceptor requestInterceptor = new RequestInterceptor();

    private static volatile NetConfiguration CONFIGURATION;

    public static NetConfiguration create() {
        if (CONFIGURATION == null) {
            synchronized (NetConfiguration.class) {
                if (CONFIGURATION == null) {
                    CONFIGURATION = new NetConfiguration();
                }
            }
        }
        return CONFIGURATION;
    }

    public long getTimeout() {
        return timeout;
    }

    public NetConfiguration setTimeout(long time_out) {
        this.timeout = time_out;
        return CONFIGURATION;
    }

    public String getHost() {
        return host;
    }

    public NetConfiguration setHost(String host) {
        this.host = host;
        return CONFIGURATION;
    }

    public boolean isLogEnable() {
        return logEnable;
    }

    public NetConfiguration setLogEnable(boolean log_enable) {
        this.logEnable = log_enable;
        return CONFIGURATION;
    }

    public Interceptor getLogInterceptor() {
        return logInterceptor;
    }

    public NetConfiguration setLogInterceptor(Interceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
        return CONFIGURATION;
    }

    public Interceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    public NetConfiguration setRequestInterceptor(Interceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
        return CONFIGURATION;
    }

}
