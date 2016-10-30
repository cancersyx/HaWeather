package com.example.administrator.haweather.util;

import android.text.TextUtils;

import com.example.administrator.haweather.model.City;
import com.example.administrator.haweather.model.Country;
import com.example.administrator.haweather.model.HaWeatherDB;
import com.example.administrator.haweather.model.Province;

/**
 * Created by ${zsf} on 2016/10/30.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */

    public synchronized static boolean handleProvincesResponse(HaWeatherDB haWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province数据表
                    haWeatherDB.saveProvince(province);
                }
                return true;
            }

        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(HaWeatherDB haWeatherDB,
                                                            String response, int provinceId) {
        if (!TextUtils .isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表中
                    haWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountriesResponse(HaWeatherDB haWeatherDB ,
                                                               String response,int cityId){
            if (!TextUtils.isEmpty(response)){
                String[] allCountries = response.split(",");
                if (allCountries != null && allCountries.length > 0){
                    for (String c : allCountries) {
                        String[] array = c.split("\\|");
                        Country country = new Country();
                        country.setCountryCode(array[0]);
                        country.setCountryName(array[1]);
                        country.setCityId(cityId);
                        //将解析出来的数据存储到县级表中
                        haWeatherDB.saveCountry(country);
                    }
                    return true;
                }
            }
        return false;
    }






}
