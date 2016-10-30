package com.example.administrator.haweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.haweather.R;
import com.example.administrator.haweather.model.City;
import com.example.administrator.haweather.model.Country;
import com.example.administrator.haweather.model.HaWeatherDB;
import com.example.administrator.haweather.model.Province;
import com.example.administrator.haweather.util.HttpCallbackListener;
import com.example.administrator.haweather.util.HttpUtil;
import com.example.administrator.haweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${zsf} on 2016/10/30.
 */
public class ChooseAreaActivity extends Activity {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;
    private TextView titleTextView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private HaWeatherDB haWeatherDB;
    private List<String> datas = new ArrayList<>();

    private List<Province> provinces = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
    private List<Country> countries = new ArrayList<>();

    private Province selectedProvince;
    private City selectedCity;
    private Country selectedCountry;

    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        initView();
        initData();
        initAction();
    }


    private void initView() {
        titleTextView = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);

    }

    private void initData() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        listView.setAdapter(adapter);
        haWeatherDB = HaWeatherDB.getInstance(this);
        queryProvinces();
    }


    private void initAction() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinces.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cities.get(position);
                    queryCountries();
                }
            }
        });
    }

    private void queryProvinces() {
        provinces = haWeatherDB.loadProvinces();
        if (provinces.size() > 0){
            datas.clear();
            for (Province province : provinces) {
                datas.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleTextView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }
    }

    private void queryCities() {
        cities = haWeatherDB.loadCities(selectedProvince.getId());
        if (cities.size() > 0){
            datas.clear();
            for (City city : cities) {
                datas.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleTextView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    private void queryCountries() {
        countries = haWeatherDB.loadCountries(selectedCity.getId());
        if (countries.size() > 0){
            datas.clear();
            for (Country country : countries) {
                datas.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleTextView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTRY;
        }else {
            queryFromServer(selectedCity.getCityCode(),"country");
        }
    }


    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(haWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(haWeatherDB,response,selectedProvince.getId());
                }else if ("country".equals(type)){
                    result = Utility.handleCountriesResponse(haWeatherDB,response,selectedCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("country".equals(type)){
                                queryCountries();
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTRY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }

    }
}
