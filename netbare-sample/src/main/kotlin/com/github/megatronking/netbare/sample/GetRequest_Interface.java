package com.github.megatronking.netbare.sample;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Carson_Ho on 17/3/20.
 */

public interface GetRequest_Interface {


    @GET("PostGameData")
    Call<Translation> getCall();
    // 注解里传入 网络请求 的部分URL地址
    // getCall()是接受网络请求数据的方法
}
