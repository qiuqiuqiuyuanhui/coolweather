package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuanhui on 2018/5/30.
 */

public class Now {
    @SerializedName( "cond_txtl" )
    public String info;

    @SerializedName( "tmp" )
    public String temperature;
}
