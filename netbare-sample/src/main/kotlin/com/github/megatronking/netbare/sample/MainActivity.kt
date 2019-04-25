package com.github.megatronking.netbare.sample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.widget.Button
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareConfig
import com.github.megatronking.netbare.NetBareListener
import com.github.megatronking.netbare.http.HttpInjectInterceptor
import com.github.megatronking.netbare.http.HttpInterceptorFactory
import com.github.megatronking.netbare.ssl.JKS
import java.io.IOException
import java.util.Base64.getEncoder
import android.app.*
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.support.v4.app.NotificationCompat
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.widget.TextView
import android.widget.EditText
import com.google.gson.JsonElement
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NetBareListener {
    companion object {
        private const val REQUEST_CODE_PREPARE = 1
        private lateinit var notify1: Notification
        private lateinit var mNManager: NotificationManager
        private lateinit var  channel: NotificationChannel

        private lateinit var content: JSONObject
        private var mHandler =  Handler()
        private var haiao= WechatLocationInjector()
        //private val textView: TextView=App.getInstance()findViewById(R.id.textView);
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

            //Handler().sendEmptyMessage(0x123)
            //sendEmptyMessage()


        }

    }
    private lateinit var editText: EditText
    private lateinit var addLogBtn: Button
    private lateinit var clearLogBtn: Button
    private lateinit var textView: TextView
    private lateinit var mNetBare : NetBare
    private lateinit var mActionButton : Button
    private lateinit var mInstallButton : Button
    private lateinit var mStartButton : Button
    private lateinit var mStopButton : Button
    private val mContext: Context? = null
    private val msgKey1 = 100
    private lateinit var tv_time: TextView

    private val NOTIFYID_1 = 1
    private lateinit var btn_show_normal: Button
    private lateinit var btn_close_normal: Button

    var wechatLocationInjector = WechatLocationInjector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_time =  findViewById(R.id.textView);
        tv_time.setMovementMethod(ScrollingMovementMethod.getInstance())
        //TimeThread().start();
        mNManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                    "com.github.megatronking.netbare.sample.NOTIFICATION_CHANNEL_ID", "NetBare",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mNManager.createNotificationChannel(channel);

        }

        mNetBare = NetBare.get()
        mActionButton = findViewById(R.id.action)
        mActionButton.setOnClickListener {
            if (mNetBare.isActive) {
                mNetBare.stop()

            } else{
                prepareNetBare()
                //tv_time.setText("辅助启动成功,请最小化辅助后,打开游戏")
            }

        }
        // 监听NetBare服务的启动和停止
        mNetBare.registerNetBareListener(this)

        wechatLocationInjector.setOnResponseListener(object : PostRequest.OnResponseListener {
            override fun onFailure(msg: String) {

            }
            override fun onSuccess(result: String) {
                addMsg(tv_time, result)
            }
        }
        )
        //重新安装证书
        mInstallButton= findViewById(R.id.install)
        mInstallButton.setOnClickListener {
            Install()

        }
        //摇能量和停止
        val energy:Int
        //energy=(editText.text).to

        mStartButton= findViewById(R.id.start_roll)
        mStartButton.setOnClickListener {
            haiao.roll()

        }

        mStopButton= findViewById(R.id.start_quick_roll)
        mStopButton.setOnClickListener {
            haiao.stoproll()

        }
        //init()
        //wechatLocationInjector.test()
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
    private fun Install() {
        // 强制安装自签证书
        //if (!JKS.isInstalled(this, App.JSK_ALIAS)) {
        try {
            JKS.install(this, App.JSK_ALIAS, App.JSK_ALIAS)
        } catch(e : IOException) {
            // 安装失败
        }
        return
        //}

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PREPARE) {
            prepareNetBare()
        }
    }

    private fun interceptorFactories() : List<HttpInterceptorFactory> {
        // 拦截器范例1：打印请求url
        //val interceptor1 = HttpUrlPrintInterceptor.createFactory()
        // 注入器范例1：替换百度首页logo
        //val injector1 = HttpInjectInterceptor.createFactory(BaiduLogoInjector())
        // 注入器范例2：修改发朋友圈定位
        val injector2 = HttpInjectInterceptor.createFactory(WechatLocationInjector())
        // 可以添加其它的拦截器，注入器
        // ...
        return listOf(  injector2)
    }




    val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            var title :String

            when (msg.what) {
                msgKey1 -> {
                    val time = System.currentTimeMillis()
                    val date = Date(time)
                    //val format = SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEE")
                    //tv_time.text = format.format(date)
                    title=msg.obj.toString()
                    tv_time.text=title
                }
                0x123->{
                    tv_time.text = "发送的消息"
                }
                else -> {
                }
            }
        }
    }
    fun addMsg(logView: TextView, msg: String) {
        logView.append(msg)
        var offset = logView.getLineCount() * logView.getLineHeight();
        if (offset > logView.getHeight()) {
            logView.scrollTo(0, offset - logView.getHeight());
        }
    }

    /*
    //在handler中更新UI

    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {

            tv_time.setText("你想变的内容")

        }

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
    */

}

