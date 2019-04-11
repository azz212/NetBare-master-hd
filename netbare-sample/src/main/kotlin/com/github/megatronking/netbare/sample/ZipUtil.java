package com.github.megatronking.netbare.sample;

import android.provider.SyncStateContract;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * 对字符串进行加解密和加解压
 * @author ***
 *
 */

public class ZipUtil {



    /**
     * 将字符串压缩后Base64
     * @param primStr 待加压加密函数
     * @return
     */
    public static String zipString(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;
        try{
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(primStr.getBytes("UTF-8"));
            zout.closeEntry();
            String newbytes="";
            newbytes=Base64.encodeToString(out.toByteArray(),Base64.DEFAULT);

            return newbytes;

        } catch (IOException e) {
            //log.error("对字符串进行加压加密操作失败：", e);
            return null;
        } finally {
            if (zout != null) {
                try {
                    zout.close();
                } catch (IOException e) {
                    //log.error("对字符串进行加压加密操作，关闭zip操作流失败：", e);
                    return null;
                }
            }
        }
    }

    /**
     * 将压缩并Base64后的字符串进行解密解压
     * @param compressedStr 待解密解压字符串
     * @return
     */
    public static final String unzipString(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        byte[] compressed2=new byte[100];
        compressed2=Base64.decode(compressedStr.getBytes(),Base64.DEFAULT);

        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        String decompressed = null;
        try {
            byte[] compressed=new byte[100];

            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed2);
            zin = new ZipInputStream(in);
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString("UTF-8");
        } catch (IOException e) {
            //log.error("对字符串进行解密解压操作失败：", e);
            decompressed = null;
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                    //log.error("对字符串进行解密解压操作，关闭压缩流失败：", e);
                    return null;
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //log.error("对字符串进行解密解压操作，关闭输入流失败：", e);
                    return null;
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //log.error("对字符串进行解密解压操作，关闭输出流失败：", e);
                    return  null;
                }
            }
        }
        return decompressed;
    }
}