package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Lifestyle;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView comfortText;

    private TextView drsgText;

    private TextView sportText;

    private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);



        //初始化各控件
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById( R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        comfortText = (TextView)findViewById(R.id.comf_text);
        drsgText = (TextView)findViewById(R.id.drsg_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
        String weatherString = preferences.getString( "weather", null );
        String bingPic = preferences.getString("bing_pic", null  );
        if( weatherString != null ){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse( weatherString );
            showWeatherInfo( weather );
        } else{
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra( "weatherId" );
            weatherLayout.setVisibility(  View.INVISIBLE );
            requestWeather( weatherId );
        }
        if(bingPic != null ){
                Glide.with( this ).load( bingPic ).into( bingPicImg );
        } else{
            loadBingPic();
        }

    }

    /**
     * 根据天气id请求城市天气信息
     * @param weatherId
     */
    public void requestWeather( final String weatherId ){
        String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + weatherId + "&key=263e5b162d414da9817b216079350e73";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText( WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_LONG ).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse( responseText );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if( weather != null && "ok".equals( weather.status ) ){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( WeatherActivity.this ).edit();
                            editor.putString( "weather", responseText );
                            showWeatherInfo( weather );
                        } else {
                            Toast.makeText( WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_LONG ).show();
                        }
                    }
                });
            }
        });
        loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo( final Weather weather ) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime;
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.info;
        titleCity.setText( cityName );
        titleUpdateTime.setText( updateTime );
        degreeText.setText( degree );
        weatherInfoText.setText( weatherInfo );

        forecastLayout.removeAllViews();
        for( Forecast forecast : weather.forecastList ){
            View view = LayoutInflater.from( this ).inflate( R.layout.forecast_item, forecastLayout, false );
            TextView dateText = (TextView) view.findViewById( R.id.date_text );
            TextView infoText = (TextView) view.findViewById( R.id.info_text );
            TextView maxText = (TextView) view.findViewById( R.id.max_text );
            TextView minText = (TextView) view.findViewById( R.id.min_text );
            dateText.setText( forecast.date );
            infoText.setText( forecast.info );
            maxText.setText( forecast.tmp_max );
            minText.setText( forecast.tmp_min );
            forecastLayout.addView( view );
        }



        new Thread(new Runnable() {
            @Override
            public void run() {
                for( final Lifestyle lifestyle : weather.lifestyleList ){
                    if( "comf".equals( lifestyle.type ) )
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                comfortText.setText( "舒适度：" + lifestyle.txt );
                            }
                        });
                    if( "drsg".equals( lifestyle.type ) )
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                drsgText.setText( "穿衣指数：" + lifestyle.txt );
                            }
                        });
                    if( "sport".equals( lifestyle.type ) )
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sportText.setText( "运动建议：" + lifestyle.txt );
                            }
                        });
                }
            }
        }).start();

        weatherLayout.setVisibility( View.VISIBLE );

    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String reauestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(reauestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( WeatherActivity.this ).edit();
                editor.putString( "bing_pic", bingPic );
                editor.apply();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        Glide.with( WeatherActivity.this ).load( bingPic ).into( bingPicImg );
                    }
                });
            }
        });
    }
}
