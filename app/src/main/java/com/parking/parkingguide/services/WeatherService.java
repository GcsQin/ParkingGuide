package com.parking.parkingguide.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.parking.parkingguide.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class WeatherService extends Service implements AMapLocationListener,WeatherSearch.OnWeatherSearchListener{
    private WeatherSearchQuery weatherSearchQuery;
    private WeatherSearch weatherSearch;
    private LocalWeatherLive localWeatherLive;
    //定位
    private AMapLocationClient locationClient;
    private AMapLocationClientOption aMapLocationClientOption;
    private int locationFlag=0;
    public WeatherService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //服务第一次创建的时候调用该方法
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化定位服务对象
        locationClient=new AMapLocationClient(getApplicationContext());
        //初始化定位服务参数对象
        aMapLocationClientOption=new AMapLocationClientOption();
        //设置定位监听
        locationClient.setLocationListener(this);
        //设置定位模式为省电模式
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位时间间隔
        aMapLocationClientOption.setInterval(300000);
        //为定位服务设置服务参数
        locationClient.setLocationOption(aMapLocationClientOption);
        locationClient.startLocation();
    }

    //每次服务启动的时候都会调用该方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("服务启动了","11111111");
        return super.onStartCommand(intent, flags, startId);
    }
    //服务销毁的时候调用该方法
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(locationFlag!=0){
            return;
        }else {
            String city=aMapLocation.getCity();
            //查询实时天气
            weatherSearchQuery=new WeatherSearchQuery(city,WeatherSearchQuery.WEATHER_TYPE_LIVE);
            weatherSearch=new WeatherSearch(getApplicationContext());
            weatherSearch.setOnWeatherSearchListener(this);
            weatherSearch.setQuery(weatherSearchQuery);
            weatherSearch.searchWeatherAsyn();
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if(i==1000){
            if(localWeatherLiveResult!=null&&localWeatherLiveResult.getLiveResult()!=null){
                localWeatherLive=localWeatherLiveResult.getLiveResult();
                //预报发布时间
                String reportTime=localWeatherLive.getReportTime();
                //获取实时温度
                String temp=localWeatherLive.getTemperature()+"°";
                //获取风向以及风力
                String windy=localWeatherLive.getWindDirection();
                String windyPower=localWeatherLive.getWindPower();
                //获取天气湿度
                String humidity=localWeatherLive.getHumidity()+"%";
                //获取城市
                String city=localWeatherLive.getCity();
                //获取天气状态
                String status=localWeatherLive.getWeather();
                EventBus.getDefault().post(new MessageEvent(reportTime,temp,windy,windyPower,humidity,city,status));
            }
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
}
