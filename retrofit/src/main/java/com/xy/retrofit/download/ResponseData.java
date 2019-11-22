package com.xy.retrofit.download;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by xieying on 2019-11-19.
 * Description：
 */
public class ResponseData {

    private ResponseBody mResponseBody;

    private long start;

    private long end;

    public ResponseData(ResponseBody responseBody, long start, long end) {
        mResponseBody = responseBody;
        this.start = start;
        this.end = end;
    }

    public ResponseBody getResponseBody() {
        return mResponseBody;
    }

    public void setResponseBody(ResponseBody responseBody) {
        mResponseBody = responseBody;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
