package com.aliouswang.sprite.http.library;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/1/12 0012.
 */
public class HttpTask {

    private volatile static OkHttpClient httpClient;

    private HttpTask() {

    }

    public static OkHttpClient getInstance () {
        if (httpClient == null) {
            synchronized (HttpTask.class) {
                if (httpClient == null) {
                    httpClient = new OkHttpClient();
                }
            }
        }
        return httpClient;
    }

    public void syncGet(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = getInstance().newCall(request).execute();
            if (response.isSuccessful()) {
                callback.onResponse(response);
            }else {
                callback.onFailure(request, new IOException(response.body().string()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailure(request, new IOException(e.toString()));
        }

    }

    public void asyncGet(String url, Callback callback) {

    }

}
