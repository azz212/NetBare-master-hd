package com.github.megatronking.netbare.sample;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class encrydata {

    public static String percnet(double d,double e){
        double p = d/e;
        DecimalFormat nf = (DecimalFormat) NumberFormat.getPercentInstance();
        nf.applyPattern("00%"); //00表示小数点2位
        nf.setMaximumFractionDigits(2); //2表示精确到小数点后2位
        return nf.format(p);
    }
    public static void main() {
        String inputStr="{\"remainShareCount\":0,\"boxEnergy\":0,\"keyCount\":4,\"nextShareGainEnergy\":0}";
        try {
            byte[] outputStr = inputStr.getBytes("UTF-8");

            byte[] output =new byte[100];
            Deflater compress = new Deflater();
            compress.setInput(outputStr);
            compress.finish();
            int compressDataLength = compress.deflate(output);
            System.out.println(compressDataLength);
            String encry="";
            encry=Base64.encodeToString(output,Base64.DEFAULT);


            encry="H4sIAAAAAAAA/6pWKkrNTczMC85ILEp1zi/NK1GyMtBRSsqvcM1LLUqvBPOyUyuhUiY6SnmpFSVg1e6JmXlwRbUAAAAA//8=";
            byte[] output2=new byte[100];

            output2=Base64.decode(encry,Base64.DEFAULT);



            //decompress
            Inflater decompress = new Inflater();
            byte[] input = new byte[100];
            output=output2;
            decompress.setInput(output, 0, compressDataLength);
            int resultLength = decompress.inflate(input);
            System.out.println(resultLength);
            decompress.end();

            if (resultLength < compressDataLength) {
                System.out.println("负压缩比");
            }
            else {
                System.out.println(percnet( (double)compressDataLength , (double)resultLength));
            }
            System.out.println(new String(input, 0, resultLength,"UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * zlib解压+base64
     */
    public static String decompressData(String encdata) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InflaterOutputStream zos = new InflaterOutputStream(bos);
            //zos.write(Base64.decodeBase64(encdata.getBytes()));
            zos.write(Base64.decode(encdata.getBytes(),Base64.DEFAULT));
            zos.close();
            return new String(bos.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
