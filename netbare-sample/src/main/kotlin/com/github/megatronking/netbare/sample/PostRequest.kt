package com.github.megatronking.netbare.sample

import android.app.Activity
import android.app.Notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity

import com.google.gson.JsonElement

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import android.content.Context.NOTIFICATION_SERVICE
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.json.JSONArray
import org.json.JSONObject
import android.media.MediaCodec.MetricsConstants.MODE
import android.os.Handler
import android.os.Message
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Carson_Ho on 17/3/21.
 */

class PostRequest() {

    private var mOnResponseListener:OnResponseListener? = null
    fun request(posturl: String, method: String, requestdata: String, responsedata: JsonElement) {
        val intent = Intent()
        val title = "海盗助手"
        var result=""

        var obj = JSONObject()
        //步骤4:创建Retrofit对象
        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.hdgame.top") // 设置 网络请求 Url
                //.baseUrl("http://192.168.10.100:8083") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build()

        // 步骤5:创建 网络请求接口 的实例
        val request = retrofit.create(PostRequest_Interface::class.java)
        //对 发送请求 进行封装(设置需要翻译的内容)
        val call = request.getCall(posturl, method, requestdata, responsedata)
        //步骤6:发送网络请求(异步)
        call.enqueue(object : Callback<Translation> {
            //请求成功时回调
            override fun onResponse(call: Call<Translation>, response: Response<Translation>) {
                // 请求处理,输出结果
                // 输出翻译的内容

                obj.put("result",response.body().getResult())
                obj.put("url",response.body().getUrl())
                result=obj.get("result").toString()
                result="test111"
                if(result!=""){

                    //todo 此处需要将obj里面的result字符串显示到MainActivity界面的textview中.
                    //todo 要求Textview一行一条消息,消息前面有系统时间,如果超过界面显示范围,自动滚动到最下面一行.

                    //mHandler.sendEmptyMessage(0x123)
                    MainActivity.sendmsg(title,obj)

                    sendResultToMain(result)

                }

            }


            //请求失败时回调
            override fun onFailure(call: Call<Translation>, throwable: Throwable) {
                println("请求失败")
                println(throwable.message)
                obj.put("result","获取数据失败")
                obj.put("url","")
                result=obj.get("result").toString()
                result="test111"
                if(result!=""){
                    MainActivity.sendmsg(title,obj)
                    sendResultToMain(result)
                }
            }

        })
    }

    companion object {

        private val NOTIFYID_1 = 1
    }
    fun setOnResponseListener(onResponseListener:OnResponseListener){
        mOnResponseListener = onResponseListener
    }

    interface OnResponseListener{
        fun onSuccess(result: String)
        fun onFailure(msg: String)
    }

    private fun sendResultToMain(result : String){
        val time = System.currentTimeMillis()
        val date = Date(time)
        val format = SimpleDateFormat("MM-dd HH:mm:ss")
        mOnResponseListener?.onSuccess("["+format.format(date)+"] "+result+"\n")
    }

    fun testSend(){
        sendResultToMain("log test test test test test test test test test test >>>> .....")
    }

}