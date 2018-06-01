package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuanhui on 2018/5/29.
 */

public class Basic {
    @SerializedName( "location" )
    public String cityName;

    @SerializedName( "cid" )
    public String weatherId;
}
