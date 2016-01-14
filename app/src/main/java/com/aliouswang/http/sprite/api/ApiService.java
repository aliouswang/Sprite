package com.aliouswang.http.sprite.api;

import com.aliouswang.http.sprite.model.Pojo;
import com.aliouswang.sprite.http.processor.annotation.POST;

import rx.Observable;

/**
 * Created by aliouswang on 16/1/12.
 */
public interface ApiService {

//    @POST("http://test.api.51jiabo.com:1080/hxjb/decoration/case/v1.0/list.do")
//    Observable<String> getDecorateList(String url);

    @POST("http://test.api.51jiabo.com:1080/hxjb/decoration/case/v1.0/list.do")
    Observable<Pojo> getDecorateList(String url);

}
