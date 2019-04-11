package com.github.megatronking.netbare.sample

import android.util.Log
import com.github.megatronking.netbare.http.HttpIndexedInterceptor
import com.github.megatronking.netbare.http.HttpInterceptorFactory
import com.github.megatronking.netbare.http.HttpRequestChain
import com.github.megatronking.netbare.http.HttpResponseChain
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer

/**
 * 拦截器反例1：打印请求url
 *
 * @author Megatron King
 * @since 2019/1/2 22:05
 */
class HttpUrlPrintInterceptor : HttpIndexedInterceptor() {

    companion object {
        const val TAG = "URL"

        fun createFactory(): HttpInterceptorFactory {
            return HttpInterceptorFactory { HttpUrlPrintInterceptor() }
        }
    }

    override fun intercept(chain: HttpRequestChain, buffer: ByteBuffer, index: Int) {
        if (index == 0) {
            // 一个请求可能会有多个数据包，故此方法会多次触发，这里只在收到第一个包的时候打印
            var Hostname="";
            var Gameurl="";
            Log.i(TAG, "Request: " + chain.request().url())
            Log.i(TAG, "Request:method"+chain.request().method())
            Log.i(TAG, "Request:method"+chain.request().host())
            Hostname=chain.request().host();
            Gameurl=chain.request().url();
            //PostRequest().request(Gameurl);
            if (Hostname=="pirate-api.hortor002.com"){
                //PostGameData(Gameurl);
                PostRequest().request(Gameurl);

            }
        }
        // 调用process将数据发射给下一个拦截器，否则数据将不会发给服务器
        chain.process(buffer)
    }

    override fun intercept(chain: HttpResponseChain, buffer: ByteBuffer, index: Int) {
        chain.process(buffer)
    }
    //登录
    fun PostGameData(Gameurl:String) {

        //创建子线程
        val t = object : Thread() {

            //运行子线程
            override fun run() {
                //不检查过时的方法
                val path = "http://192.168.10.100:8083/PostGameData"//设置网址

                try {
                    //将地址封装成Url对象
                    val url = URL(path)

                    //创建连接对象,此时未建立连接
                    val conn = url.openConnection() as HttpURLConnection

                    //设置请求方式为Post请求
                    conn.requestMethod = "POST"

                    //设置连接超时
                    conn.connectTimeout = 5000

                    //设置读取超时
                    conn.readTimeout = 5000

                    //拼接出要提交的数据的字符串

                    val data="Gameurl=" + Gameurl.replace("&","$",true);


                    //添加post请求的两行属性
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    conn.setRequestProperty("Content-Length", data.length.toString() + "")
                    conn.setRequestProperty("Charset", "UTF-8");

                    //设置打开输出流
                    conn.doOutput = true

                    //拿到输出流
                    val os = conn.outputStream

                    //使用输出流往服务器提交数据
                    os.write(data.toByteArray())

                    //如果请求发送成功





                    if (conn.responseCode == 200) {
                        Log.i(TAG, "Request:result200")

                        //获得连接对象中的输入流
                        //val `is` = conn.inputStream

                        //获得输入流中的字符串
                        //val text = HttpGetText.getTextFromStream(`is`)

                        //创建一个消息对象
                        //val msg = handler.obtainMessage()

                        //设置消息对象携带的数据
                        //msg.obj = text

                        //将消息发送到主线程的消息队列
                        //handler.sendMessage(msg)
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                }

            }
        }

        //启动子线程
        t.start()
    }
}
