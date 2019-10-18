package com.xy.retrofit.data;


import com.xy.retrofit.net.RetrofitService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by xieying on 2019/9/26.
 * Description：
 */
public interface TranslationApi {
    TranslationApi api = RetrofitService.create(TranslationApi.class);

    @GET("http://fy.iciba.com/ajax.php")
    Call<Translation> getTranslateContent(@Query("a") String a,
                                          @Query("f") String fy,
                                          @Query("t") String auto,
                                          @Query("w") String content);

}
