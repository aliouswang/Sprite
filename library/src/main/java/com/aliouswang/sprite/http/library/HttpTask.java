package com.aliouswang.sprite.http.library;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/1/12 0012.
 */
public class HttpTask {

    private volatile static OkHttpClient httpClient;
    private volatile static HttpTask httpTask;

    private HttpTask() {

    }

    public static HttpTask getInstance() {
        if (httpTask == null) {
            synchronized (HttpTask.class) {
                if (httpTask == null) {
                    httpTask = new HttpTask();
                }
            }
        }
        return httpTask;
    }

    public static OkHttpClient getHttpClientInstance () {
        if (httpClient == null) {
            synchronized (HttpTask.class) {
                if (httpClient == null) {
                    httpClient = new OkHttpClient();
//                    httpClient.networkInterceptors().add(new StethoInterceptor());
                }
            }
        }
        return httpClient;
    }

    public void syncGet(String url, Callback callback) {
        syncGet(url, null, callback);
    }

    public void syncGet(String url, Map<String, Object> headerMap, Callback callback) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> headerKey = headerMap.keySet();
            for(String key : headerKey) {
                builder.addHeader(key, headerMap.get(key).toString());
            }
        }
        Request request = builder.build();
        try {
            Response response = getHttpClientInstance().newCall(request).execute();
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
        Request request = new Request.Builder()
                .url(url)
                .build();
        getHttpClientInstance().newCall(request).enqueue(callback);
    }

}
