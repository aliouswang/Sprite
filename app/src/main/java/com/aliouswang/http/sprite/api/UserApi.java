package com.aliouswang.http.sprite.api;

import com.aliouswang.sprite.http.processor.annotation.POST;

/**
 * Created by Administrator on 2016/1/12 0012.
 */
public abstract class UserApi {

    @POST("http://test.api.51jiabo.com:1080/hxjb/decoration/case/v1.0/list.do")
    public abstract void login(String username, String password);

}
