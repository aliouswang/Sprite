package com.aliouswang.http.sprite;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aliouswang.http.sprite.api.ApiService;
import com.aliouswang.sprite.http.processor.annotation.InjectFactory;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            doHttpReqeust();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public void doHttpReqeust() throws Exception{

        ApiService apiService = InjectFactory.inject(ApiService.class);
        Observable<String> task =
                apiService.getDecorateList("http://test.api.51jiabo.com:1080/hxjb/decoration/case/v1.0/list.do");

//        Observable<String> task = HttpTask.getInstance()
//                .get("http://test.api.51jiabo.com:1080/hxjb/decoration/case/v1.0/list.do");
        task.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        if (s == null) {

                        }
                    }
                });

//        Map<String, Object> header = new HashMap<>();
//        header.put("user_id", 123);
//        header.put("pageSize", "1");
//        HttpTask.getInstance().
//                syncPost("http://test.api.51jiabo.com:1080/hxjb/decoration/case/v1.0/list.do", header, header, new Callback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//                        Log.e("sprite", e.toString());
//                    }
//
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//                        if (!response.isSuccessful()) {
//                            throw new IOException("Unexpected code " + response);
//                        }
//
////                        Log.e("sprite", response.body().string());
//
//                        String jsonString = response.body().string();
//                        Pojo pojo = JSON.parseObject(jsonString, Pojo.class);
//                        if (pojo == null) {
//
//                        }
//                    }
//                });
    }
}
