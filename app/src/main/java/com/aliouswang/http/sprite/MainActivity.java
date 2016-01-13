package com.aliouswang.http.sprite;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.aliouswang.http.sprite.api.UserApi;
import com.aliouswang.sprite.http.processor.annotation.InjectFactory;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
                try {
                    doHttpReqeust();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void doHttpReqeust() throws Exception{

        UserApi api = InjectFactory.inject(UserApi.class);
        api.login("geminiwen", "password");

//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("http://test.api.51jiabo.com:1080/hxjb/decoration/case/v1.0/list.do")
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                }
//
//                Log.e("sprite", response.body().string());
//            }
//        });
    }
}
