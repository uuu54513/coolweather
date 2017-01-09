package com.coolweather.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utilty;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout weatherInfoLayout;
    private TextView cityName;
    private TextView publishText;
    private TextView wetherDespText;
    private TextView temp1;
    private TextView temp2;
    private TextView currentDate;
    private Button switchCity;
    private Button refreshCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        getSupportActionBar().hide();
        weatherInfoLayout = (RelativeLayout) findViewById(R.id.weather_info);
        cityName = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_parent);
        wetherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1 = (TextView) findViewById(R.id.temp1);
        temp2 = (TextView) findViewById(R.id.temp2);
        currentDate = (TextView) findViewById(R.id.current);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshCity = (Button) findViewById(R.id.refresh);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            publishText.setText("正在同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else {
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshCity.setOnClickListener(this);
    }

    private void showWeather() {
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        cityName.setText(pres.getString("city_name",""));
        temp1.setText(pres.getString("temp1",""));
        temp2.setText(pres.getString("temp2",""));
        wetherDespText.setText(pres.getString("weather_desp",""));
        publishText.setText("今天"+pres.getString("publish_time","")+"发布");
        currentDate.setText(pres.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address,"countyCode");
    }

    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendRequestHttp(address, new HttpCallbackListener() {
            @Override
            public void onFinsh(final String response) {
                if ("countyCode".equals(type)){
                    if (!TextUtils.isEmpty(response)){
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if ("weatherCode".equals(type)){
                    Utilty.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh:
                publishText.setText("正在同步中...");
                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = pre.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
