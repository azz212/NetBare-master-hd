package com.github.megatronking.netbare.sample

import com.google.gson.JsonElement

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Carson_Ho on 17/3/21.
 */
interface PostRequest_Interface {


    @POST("PostGameData")
    @FormUrlEncoded
    fun getCall(@Field("posturl") targetSentence: String, @Field("method") method: String, @Field("requestdata") requestdata: String, @Field("responsedata") jsonbody: JsonElement): Call<Translation>
    //采用@Post表示Post方法进行请求（传入部分url地址）
    // 采用@FormUrlEncoded注解的原因:API规定采用请求格式x-www-form-urlencoded,即表单形式
    // 需要配合@Field使用
}

