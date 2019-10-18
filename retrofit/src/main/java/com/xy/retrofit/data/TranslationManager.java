package com.xy.retrofit.data;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xieying on 2019/9/26.
 * Description：
 */
public class TranslationManager {


    public static void translation(String content, final OnTranslationResultListener listener){

        TranslationApi.api.getTranslateContent("fy","auto","auto",content)
                .enqueue(new Callback<Translation>() {
                    @Override
                    public void onResponse(Call<Translation> call, Response<Translation> response) {
                        listener.success(response.body());
                    }

                    @Override
                    public void onFailure(Call<Translation> call, Throwable t) {
                        listener.fail(t.toString());
                    }
                });
    }





    public interface OnTranslationResultListener{
        void success(Translation translation);

        void fail(String error);
    }
}
