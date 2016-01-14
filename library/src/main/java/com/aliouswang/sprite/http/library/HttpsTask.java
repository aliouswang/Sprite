package com.aliouswang.sprite.http.library;

import com.aliouswang.sprite.http.library.ssl.DefaultSSLProtocolSocketFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by aliouswang on 16/1/14.
 */
public class HttpsTask {

    private volatile static OkHttpClient httpClient;
    private volatile static HttpsTask httpTask;

    private HttpsTask() {

    }

    public static HttpsTask getInstance() {
        if (httpTask == null) {
            synchronized (HttpsTask.class) {
                if (httpTask == null) {
                    httpTask = new HttpsTask();
                }
            }
        }
        return httpTask;
    }

    public static OkHttpClient getHttpClientInstance () {
        if (httpClient == null) {
            synchronized (HttpTask.class) {
                if (httpClient == null) {
                    SSLContext sslContext = new DefaultSSLProtocolSocketFactory().getSSLContext();
                    httpClient = new OkHttpClient.Builder()
                            .sslSocketFactory(sslContext.getSocketFactory())
                            .build();
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
        asyncGet(url, null, callback);
    }

    public void asyncGet(String url, Map<String, Object> headerMap, Callback callback) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> headerKey = headerMap.keySet();
            for(String key : headerKey) {
                builder.addHeader(key, headerMap.get(key).toString());
            }
        }
        Request request = builder.build();
        getHttpClientInstance().newCall(request).enqueue(callback);
    }

    public void syncPost(String url, Map<String, Object> params, Callback callback) {
        syncPost(url, null, params, callback);
    }

    public void syncPost(String url, Map<String, Object> headerMap, Map<String, Object> params ,Callback callback) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> headerKey = headerMap.keySet();
            for(String key : headerKey) {
                builder.addHeader(key, headerMap.get(key).toString());
            }
        }

        FormBody.Builder formBody = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            Set<String> paramKey = params.keySet();
            for (String key : paramKey) {
                formBody.add(key, params.get(key).toString());
            }
        }
        RequestBody requestBody = formBody.build();

        Request request = builder
                .post(requestBody)
                .build();
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

    public void asyncPost(String url, Map<String, Object> params, Callback callback) {
        asyncPost(url, null, params, callback);
    }

    public void asyncPost(String url, Map<String, Object> headerMap, Map<String, Object> params ,Callback callback) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> headerKey = headerMap.keySet();
            for(String key : headerKey) {
                builder.addHeader(key, headerMap.get(key).toString());
            }
        }

        FormBody.Builder formBody = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            Set<String> paramKey = params.keySet();
            for (String key : paramKey) {
                formBody.add(key, params.get(key).toString());
            }
        }
        RequestBody requestBody = formBody.build();

        Request request = builder
                .post(requestBody)
                .build();
        getHttpClientInstance().newCall(request).enqueue(callback);

    }

    public void uploadFile(String url, File file) throws IOException{
        MediaType mediaType = MediaType.parse("multipart/form-data; charset=utf-8");
        if (file != null) {
            throw new FileNotFoundException("File can't be null!");
        }
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, file))
                .build();
        Response response = getHttpClientInstance().newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Uxcepted code " + response);
        }
    }

}
