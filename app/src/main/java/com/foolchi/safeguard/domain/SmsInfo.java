package com.foolchi.safeguard.domain;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by foolchi on 7/5/14.
 */
public class SmsInfo {
    private String id;
    private String address;
    private String date;
    private int type;
    private String body;

    public String getAddress(){
        return  address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    public int getType(){
        return type;
    }
    public void setType(int type){
        this.type = type;
    }
    public String getBody(){
        return body;
    }
    public void setBody(String body){
        this.body = filterEmoji(body);
    }

    public String replaceEmoji(String source){
        String utf8tweet = "";
        try {
            byte[] utf8Bytes = source.getBytes("UTF-8");
            utf8tweet = new String(utf8Bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        Pattern unicodeOutliers = Pattern.compile("[\\x00-\\x7F|]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);

        utf8tweet = unicodeOutlierMatcher.replaceAll("");
        return utf8tweet;
    }

    public boolean containsEmoji(String source){
        if (source.isEmpty())
            return false;

        int len = source.length();
        for (int i = 0; i < len; i++){
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint))
                return true;
        }
        return false;
    }

    public boolean isEmojiCharacter(char codePoint){
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    public String filterEmoji(String source){
        if (!containsEmoji(source))
            return source;

        StringBuilder sb = null;
        int len = source.length();

        for (int i = 0; i < len; i++){
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)){
                if (sb == null)
                    sb = new StringBuilder(source.length());
                sb.append(codePoint);
            }
        }

        if (sb == null)
            return " ";
        else
            return sb.toString();
    }

}
