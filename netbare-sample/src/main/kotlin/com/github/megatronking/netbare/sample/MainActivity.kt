package com.github.megatronking.netbare.sample

import android.app.Activity
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
import java.util.zip.Deflater

import java.util.zip.Inflater;


class MainActivity : AppCompatActivity(), NetBareListener {

    companion object {
        private const val REQUEST_CODE_PREPARE = 1
    }

    private lateinit var mNetBare : NetBare

    private lateinit var mActionButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        mNetBare.registerNetBareListener(this);
        val edata:String;
        edata="H4sIAAAAAAAA/6pWKkrNTczMC85ILEp1zi/NK1GyMtBRSsqvcM1LLUqvBPOyUyuhUiY6SnmpFSVg1e6JmXlwRbUAAAAA//8="
        val data:String;

        //data=encrydata.decompressData(edata);

        ZipUtil.unzipString(edata);



        /*
               val encodedText="H4sIAAAAAAAA/6pWKkrNTczMC85ILEp1zi/NK1GyMtBRSsqvcM1LLUqvBPOyUyuhUiY6SnmpFSVg1e6JmXlwRbUAAAAA//8=";


               var decoder = Base64.decode(encodedText,0);
               Log.e("Base64", "Base64---->$decoder");

               val inputString = "{\"remainShareCount\":0,\"boxEnergy\":0,\"keyCount\":4,\"nextShareGainEnergy\":0}";

               val inputString2: ByteArray = byteArrayOf(100);

               //val input = inputString.toByteArray(charset("UTF-8"))

               //decompress
               var decompress= Inflater();
               var compressDataLength:Int;


               compressDataLength=decoder.toString().length;

               decompress.setInput(decoder, 0, compressDataLength);
               var resultLength:Int;

               resultLength = decompress.inflate(inputString2);
               System.out.println(resultLength);
               decompress.end();

               Log.e("Base64", "Base64---->$inputString2")





               // Compress the bytes
               val output = ByteArray(500);
               val compresser = Deflater();

               compresser.setInput(input);
               compresser.finish();
               val compressedDataLength = compresser.deflate(output);
               compresser.end();
               System.out.println(Base64.encodeToString(output,0));

               */
    }

    override fun onDestroy() {
        super.onDestroy()
        mNetBare.unregisterNetBareListener(this)
        mNetBare.stop()
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


}
