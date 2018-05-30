package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yuanhui on 2018/5/29.
 */

public class Weather {
    public String status;

    public Basic basic;

    public Update update;

    public Now now;

    @SerializedName( "daily_forecast" )
    public List<Forecast> forecastList;

    @SerializedName(  "lifestyle" )
    public List<Lifestyle> lifestyleList;


}
