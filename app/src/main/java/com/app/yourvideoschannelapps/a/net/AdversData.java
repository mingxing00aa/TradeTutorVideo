package com.app.yourvideoschannelapps.a.net;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdversData {
    @SerializedName("msg")
    public  String msg;
    @SerializedName("code")
    public  int code;
    @SerializedName("data")
    public List<Data> list;


    public static class Data{
public String imgUrl;
public String jumpUrl;
public int uploadType;//0-图片 1-视频
public int showType;//是否显示 0-是 1-否
        public int id;

        @Override
        public String toString() {
            return "Data{" +
                    "imgUrl='" + imgUrl + '\'' +
                    ", jumpUrl='" + jumpUrl + '\'' +
                    ", uploadType=" + uploadType +
                    ", showType=" + showType +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AdversData{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", list=" + list +
                '}';
    }
}
