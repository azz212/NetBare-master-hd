package com.github.megatronking.netbare.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Button
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareConfig
import com.github.megatronking.netbare.NetBareListener
import com.github.megatronking.netbare.http.HttpInjectInterceptor
import com.github.megatronking.netbare.http.HttpInterceptorFactory
import com.github.megatronking.netbare.ssl.JKS
import java.io.IOException
import java.util.Base64.getEncoder
import android.R.attr.end
import android.app.*
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import java.util.zip.Deflater

import java.util.zip.Inflater
import android.support.v4.app.NotificationCompat
import android.view.KeyEvent
import android.view.View

import android.widget.TextView
import android.widget.EditText
import com.google.gson.JsonElement
import org.json.JSONObject


class MainActivity : AppCompatActivity(), NetBareListener {
    companion object {
        private const val REQUEST_CODE_PREPARE = 1
        private lateinit var notify1: Notification
        private lateinit var mNManager: NotificationManager
        private lateinit var  channel: NotificationChannel

        fun sendmsg(title:String,content: JSONObject){
            //val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            var mBuilder= NotificationCompat.Builder(App.getInstance(),"1")
            if (content.get("result").toString()=="帆船任务"){
                val uri = Uri.parse(content.get("url").toString())
                val intent = Intent(Intent.ACTION_VIEW, uri)
                val pendingIntent:PendingIntent  = PendingIntent.getActivity(App.getInstance(), 0, intent, 0);
                mBuilder.setContentIntent(pendingIntent)
                        .setContentText("帆船任务已经刷新,点击查看")
            }else{
                mBuilder.setContentText(content.get("result").toString())
            }

            mBuilder.setContentTitle(title)//设置通知栏标题

                    //.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                    //.setNumber(5) //设置通知集合的数量
                    .setTicker("通知来啦") //通知首次出现在通知栏，带上升动画效果的
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setSmallIcon(R.drawable.netbare_notification)            //设置小图标
                    .setLargeIcon(BitmapFactory.decodeResource( App.getInstance().getResources(),R.mipmap.ic_launcher))                     //设置大图标
                    .setChannelId("com.github.megatronking.netbare.sample.NOTIFICATION_CHANNEL_ID")
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合

            notify1 = mBuilder.build();
            mNManager.notify(1, notify1)
        }
    }
    private val editText: EditText? = null
    private val addLogBtn: Button? = null
    private val clearLogBtn: Button? = null
    private val textView: TextView? = null
    private lateinit var mNetBare : NetBare
    private lateinit var mActionButton : Button
    private val mContext: Context? = null


    private val NOTIFYID_1 = 1
    private lateinit var btn_show_normal: Button
    private lateinit var btn_close_normal: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                    "com.github.megatronking.netbare.sample.NOTIFICATION_CHANNEL_ID", "NetBare",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mNManager.createNotificationChannel(channel);

        }

        //btn_show_normal = findViewById(R.id.btn_show_normal)
        //btn_close_normal =findViewById(R.id.btn_close_normal)
        //btn_show_normal.setOnClickListener{onClick() }

        //btn_close_normal.setOnClickListener {onClick()  }
        mNetBare = NetBare.get()

        mActionButton = findViewById(R.id.action)
        mActionButton.setOnClickListener {
            if (mNetBare.isActive) {
                mNetBare.stop()
            } else{
                prepareNetBare()
            }
        }
        // 监听NetBare服务的启动和停止
        mNetBare.registerNetBareListener(this)

    }

    private fun sendmsg(title:String,content:String){
        //val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        var mBuilder= NotificationCompat.Builder(App.getInstance(),"1")
        val uri = Uri.parse("https://www.hdgame.top/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val pendingIntent:PendingIntent  = PendingIntent.getActivity(App.getInstance(), 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content)
                //.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                //.setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
                //.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setSmallIcon(R.drawable.netbare_notification)            //设置小图标
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))                     //设置大图标
                //.setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                //.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
        //.setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        notify1 = mBuilder.build();
        mNManager.notify(1, notify1)

    }
    @Override
    public fun onClick() {
        sendmsg("title","message")
        /*
        //定义一个PendingIntent点击Notification后启动一个Activity
            //设置图片,通知标题,发送时间,提示方式等属性
            var mBuilder= NotificationCompat.Builder(this,"1")

            mBuilder.setContentTitle("叶良辰")                        //标题
                    .setContentText("我有一百种方法让你呆不下去~")      //内容
                    //.setSubText("——记住我叫叶良辰")                    //内容下面的一小段文字
                    //.setTicker("收到叶良辰发送过来的信息~")             //收到信息后状态栏显示的文字信息
                    //.setWhen(System.currentTimeMillis())           //设置通知时间
                    .setSmallIcon(R.drawable.netbare_notification)            //设置小图标
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))                     //设置大图标
                    //.setDefaults(Notification.DEFAULT_LIGHTS )    //设置默认的三色灯与振动器
                    //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.biaobiao))  //设置自定义的提示音
                    //.setAutoCancel(true)                           //设置点击后取消Notification
                    //.setContentIntent(pit);                        //设置PendingIntent
            notify1 = mBuilder.build();
            mNManager.notify(NOTIFYID_1, notify1);
            */

    }
    override fun onDestroy() {
        super.onDestroy()
        mNetBare.unregisterNetBareListener(this)
        mNetBare.stop()
    }


    override fun onKeyDown(keyCode : Int,  event:KeyEvent):Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            val home:Intent = Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    override fun onServiceStarted() {
        runOnUiThread {
            mActionButton.setText(R.string.stop_netbare)

        }


    }

    override fun onServiceStopped() {
        runOnUiThread {
            mActionButton.setText(R.string.start_netbare)
        }
    }

    private fun prepareNetBare() {
        // 安装自签证书
        if (!JKS.isInstalled(this, App.JSK_ALIAS)) {
            try {
                JKS.install(this, App.JSK_ALIAS, App.JSK_ALIAS)
            } catch(e : IOException) {
                // 安装失败
            }
            return
        }
        // 配置VPN
        val intent = NetBare.get().prepare()
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_PREPARE)
            return
        }
        // 启动NetBare服务
        mNetBare.start(NetBareConfig.defaultHttpConfig(App.getInstance().getJSK(),
                interceptorFactories()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PREPARE) {
            prepareNetBare()
        }
    }

    private fun interceptorFactories() : List<HttpInterceptorFactory> {
        // 拦截器范例1：打印请求url
        val interceptor1 = HttpUrlPrintInterceptor.createFactory()
        // 注入器范例1：替换百度首页logo
        val injector1 = HttpInjectInterceptor.createFactory(BaiduLogoInjector())
        // 注入器范例2：修改发朋友圈定位
        val injector2 = HttpInjectInterceptor.createFactory(WechatLocationInjector())
        // 可以添加其它的拦截器，注入器
        // ...
        return listOf(interceptor1, injector1, injector2)
    }

    private fun addText(textView: TextView, content: String) {
        textView.append(content)
        textView.append("\n")
        val offset = textView.lineCount * textView.lineHeight
        if (offset > textView.height) {
            textView.scrollTo(0, offset - textView.height)
        }
    }


}
