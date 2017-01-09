package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by lenovo on 2016-11-3.
 */

public class Utilty {

    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length >0){
                for (String p : allProvinces){
                    String[] arrry = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(arrry[1]);
                    province.setProvinceCode(arrry[0]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCityResonpse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvince_id(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCountyResonpse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] Counties = response.split(",");
            if (Counties != null && Counties.length > 0){
                for (String s : Counties){
                    String[] array = s.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCity_id(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response){
        try {
            JSONObject jsonobject = new JSONObject(response);
            JSONObject weatherInfo = jsonobject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }

}
