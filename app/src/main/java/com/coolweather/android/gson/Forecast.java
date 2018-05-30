package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuanhui on 2018/5/29.
 */

public class Forecast {
    public String date;

    public String tmp_max;

    public String tmp_min;

    @SerializedName( "cond_txt_d" )
    public String info;
}
