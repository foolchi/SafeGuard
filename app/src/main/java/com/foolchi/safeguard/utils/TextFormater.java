package com.foolchi.safeguard.utils;

import java.text.DecimalFormat;
/**
 * Created by foolchi on 7/19/14.
 */
public class TextFormater {

    public static String dataSizeFormat(long size){
        DecimalFormat formater = new DecimalFormat("####.00");
        if (size < 1024){
            return size + "byte";
        }
        if (size < (1 << 20)){
            float kSize = size >> 10;
            return formater.format(kSize) + "KB";
        }
        if (size < (1 << 30)){
            float mSize = size >> 20;
            return formater.format(mSize) + "MB";
        }
        if (size < (1 << 40)){
            float gSize = size >> 30;
            return formater.format(gSize) + "GB";
        }
        else {
            return "size : error";
        }
    }
}
