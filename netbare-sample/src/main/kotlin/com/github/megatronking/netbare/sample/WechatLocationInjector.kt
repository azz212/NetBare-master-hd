package com.github.megatronking.netbare.sample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.http.HttpBody
import com.github.megatronking.netbare.http.HttpRequest
import com.github.megatronking.netbare.http.HttpResponse
import com.github.megatronking.netbare.http.HttpResponseHeaderPart
import com.github.megatronking.netbare.injector.InjectorCallback
import com.github.megatronking.netbare.injector.SimpleHttpInjector
import com.github.megatronking.netbare.io.HttpBodyInputStream
import com.github.megatronking.netbare.stream.ByteStream
import com.google.gson.*
import org.json.JSONException
import java.io.*
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

/**
 * 注入器范例2：修改发朋友圈定位
 *
 * 启动NetBare服务后，打开朋友圈发状态->所在位置，会发现POI都变成被修改地点附近的。
 *
 * @author Megatron King
 * @since 2019/1/2 22:17
 */
class WechatLocationInjector : SimpleHttpInjector() {
    var responsebody:String = ""
    var url:String =""
    var requestbody:String =""
    //定义用户信息
    private lateinit var secret:String
    private lateinit var token:String
    private var uid=0
    private var isroll=true

    private var mOnResponseListener: PostRequest.OnResponseListener? = null
    var postRequest = PostRequest()

    companion object {
        const val TAG = "WechatLocationInjector"
    }
    private var mHoldResponseHeader: HttpResponseHeaderPart? = null

    override fun sniffResponse(response: HttpResponse,body :HttpBody): Boolean {
        // 请求url匹配时才进行注入
        val shouldInject = response.url().startsWith("https://pirate-api.hortor002.com")
        if (shouldInject) {
            Log.i(TAG, "Start translate data!")
        }
        return shouldInject
    }

    override fun onResponseInject(header: HttpResponseHeaderPart, callback: InjectorCallback) {
        // 由于响应体大小不确定，这里先hold住header（需要在后面修改content-length）
        //mHoldResponseHeader = header
        callback.onFinished(header)
    }

    override fun onResponseInject(response: HttpResponse, body: HttpBody, callback: InjectorCallback) {

        var his: HttpBodyInputStream? = null
        var reader: Reader? = null
        var fos: DeflaterOutputStream? = null

        var bos: ByteArrayOutputStream? = null
        var sliceresponsebody:String?=null
        url=response.url()
        Log.i(TAG, url)
        var slicebodytext:String ?= ""

        try {
            his = HttpBodyInputStream(body)
            // 数据使用了zlib编码，这里需要先解码
            //reader = InputStreamReader(InflaterInputStream(his))
            reader = InputStreamReader(his)
            val element:JsonElement
            sliceresponsebody=reader.readText()
            if (sliceresponsebody.last()!='}') {
                responsebody+=sliceresponsebody
            }
            else {
                responsebody+=sliceresponsebody
                Log.i(TAG, responsebody)

                element=JsonParser().parse(responsebody)
                //element=JsonParser().parse(reader)
                if (element == null || !element.isJsonObject) {
                    Log.i(TAG, "element is not jsonobjcect")
                }

                val edata= element.asJsonObject.get("edata")
                val errcode= element.asJsonObject.get("errcode")
                val errmsg= element.asJsonObject.get("errmsg")
                //todo edata是一个base64+zlib 压缩后的字符串,python代码已经解开了.安卓的看怎么解开
                //todo 举例:
                /*
                edata="H4sIAAAAAAAA/6pWKkrNTczMC85ILEp1zi/NK1GyMtBRSsqvcM1LLUqvBPOyUyuhUiY6SnmpFSVg1e6JmXlwRbUAAAAA//8=",
                解出来的原始字符串应该是 {"remainShareCount":0,"boxEnergy":0,"keyCount":4,"nextShareGainEnergy":0}
                python代码是
                compresseddata=base64.b64decode(edata)

                result = zlib.decompressobj(31).decompress(compresseddata).decode('utf-8')
                print(result)
                举例2
                加密后:
                edata="H4sIAAAAAAAA/+yVQWs6MRDFv8s755Cs+v9rjt4KpRQrvZQeojuugWxmSbKui+x3L0ZbaU89VthbJu9l+DG8MCdwm5o2Rei3E2JbQyuBZEvouRreBRq2/qq251sp4E1N0IDAnkz5UFeXYhu48zE7KvIlhXy08dU20DvjIgk0gQ/WbylLW5v6q+c52IN1VNGXMyaT2niV18HcekTP3dI493SGPbdhxwF6Ii74UsBRFaGLQfx1ZvWTWY3MI/OFubjDPI9/cJzzr/N8j3POK/GbWf0XKG31+TASldBqMZMTNRcItAsU9yuqjfXQhfonpcCGjy83lM76RGFJzuVyw8dH03Ob1n1DOZ4bPq6oM6GEPoE8haqHLqRAzZ566Nl0uphJOQwfAAAA//8="
                原始字符串是
                {"outputs":[{"sum":1,"tid":81}],"points":[{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":3,"sum":0,"legs":2},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":1,"sum":0,"legs":1},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":1,"sum":0,"legs":1},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":1,"sum":0,"legs":1},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":2,"sum":0,"legs":2},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":3,"sum":0,"legs":2},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":1,"sum":0,"legs":1},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":3,"sum":0,"legs":2},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":2,"sum":0,"legs":1},{"uid":0,"name":"","headImg":"","crowns":0,"gender":0,"isVip":false,"province":0,"city":0,"isPrivilege":false,"status":0,"isTrap":false,"snowBallNum":0,"color":3,"sum":0,"legs":1}],"snowBallNum":17,"digNum":0,"seed":1950318,"refreshRemain":21600,"boxStatus":0,"winterBell":0,"boxLayoutType":1,"boxReward":{"energy":20,"money":5449500}}

                 */
                Log.i(TAG, edata.toString())
                Log.i(TAG, errcode.toString())
                Log.i(TAG, errmsg.toString())
                if (edata == null ) {
                    return
                }
                //获得response和url后,将数据传给远端接口地址
                //如果是login请求,则解析token,uid,secret
                if (url=="https://pirate-api.hortor002.com/game/basic/login"){
                    parse_login(edata)
                }
                var posturl=""
                requestbody.replace("&","$",true)

                //posturl=url.replace("&","$",true)

                //Looper.prepare()
                //var datas:PostRequest?=null;
                //datas= PostRequest()
                //在这里直接赋值 url,requestbody
                mOnResponseListener?.let { postRequest.setOnResponseListener(it) }

                postRequest.request(url,response.method().toString(),requestbody,element)
                //Looper.loop();
                responsebody=""
            }

            //element = JsonParser().parse(reader)

            /*
            // 替换经纬度，这里是珠峰的经纬度，装逼很厉害的地方
            //val location = locationNode.asJsonObject
            //location.addProperty("latitude", 27.99136f)
            //location.addProperty("longitude", 86.88915f)
            val injectedBody = element.toString()
            // 重新使用zlib编码
            bos = ByteArrayOutputStream()

            //fos = DeflaterOutputStream(bos)


            //fos.write(injectedBody.toByteArray())
            //fos.finish()
            //fos.flush()

            val injectBodyData = bos.toByteArray()
            */
            Log.i(TAG, "Inject wechat location completed!")

        }catch( e: JsonParseException) {
            Log.e(TAG, e.toString() + "");
            responsebody=""

        }
        finally {
            // 更新header的content-length
            //val injectHeader = mHoldResponseHeader!!
            //       .newBuilder()
            //       //.replaceHeader("Content-Length", injectBodyData.size.toString())
            //       .build()

            // 先将header发射出去
            //callback.onFinished(injectHeader)
            // 再将响应体发射出去
            callback.onFinished(body)
            NetBareUtils.closeQuietly(his)
            NetBareUtils.closeQuietly(reader)

        }


    }

    //拦截请求消息的form
    override fun sniffRequest(request: HttpRequest): Boolean {
        val shouldInject = request.url().startsWith("https://pirate-api.hortor002.com")
        if (shouldInject) {
            Log.i(TAG, "Start translate data!")
        }
        return shouldInject
    }

    override fun onRequestInject(request: HttpRequest, body: HttpBody,
                                 callback: InjectorCallback) {

        var his: HttpBodyInputStream? = null
        var reader: Reader? = null

        url=request.url()

        Log.i(TAG, url)

        try {
            his = HttpBodyInputStream(body)
            // 数据使用了zlib编码，这里需要先解码
            //reader = InputStreamReader(InflaterInputStream(his))
            reader = InputStreamReader(his)
            requestbody=reader.readText()
            Log.i(TAG, "request body="+requestbody)

        }finally {
            callback.onFinished(body)
            NetBareUtils.closeQuietly(his)
            NetBareUtils.closeQuietly(reader)

        }


    }
    fun parse_login(responsedata:JsonElement){
        token="";
        secret=""
        uid=0

    }
    fun roll(){
        if (isroll==true){
            //PostRequest()
            Log.i(TAG,"START TO ROLL")
        }
        else{

            Log.i(TAG,"STOP TO ROLL")
        }

        //PostRequest().request(url,response.method().toString(),requestbody,element)
    }
    fun stoproll(){
        //PostRequest()
        isroll=false
        //PostRequest().request(url,response.method().toString(),requestbody,element)
    }
    fun test(){
        mOnResponseListener?.let { postRequest.setOnResponseListener(it) }
        mHandler.sendEmptyMessageDelayed(1,3000)
    }

    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1->{
                    postRequest.testSend()
                    sendEmptyMessageDelayed(1,3000)
                }
            }

        }

    }

    fun setOnResponseListener(onResponseListener: PostRequest.OnResponseListener){
        mOnResponseListener = onResponseListener
    }
}
