package com.xy.retrofit;

import android.app.Application;
import android.content.Context;

/**
 * Created by xieying on 2019-11-22.
 * Description：
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static MyApplication getInstance(){
        if(mInstance == null){
            synchronized (MyApplication.class){
                if(mInstance == null){
                    mInstance = new MyApplication();
                }
            }
        }
        return mInstance;
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
