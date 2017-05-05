package com.parking.parkingguide.eventbus;

/**
 * Created by 37266 on 2017/4/27.
 */

public class MessageEvent {
    //预报发布时间
    String reportTime;
    //获取实时温度
    String temp;
    //获取风向以
    String windy;
    //获取风力
    String windyPower;
    //获取天气湿度
    String humidity;
    //获取城市
    String city;
    //获取天气描述
    String status;

    public MessageEvent(String reportTime, String temp, String windy, String windyPower, String humidity, String city,String status) {
        this.reportTime = reportTime;
        this.temp = temp;
        this.windy = windy;
        this.windyPower = windyPower;
        this.humidity = humidity;
        this.city = city;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWindy() {
        return windy;
    }

    public void setWindy(String windy) {
        this.windy = windy;
    }

    public String getWindyPower() {
        return windyPower;
    }

    public void setWindyPower(String windyPower) {
        this.windyPower = windyPower;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "reportTime='" + reportTime + '\'' +
                ", temp='" + temp + '\'' +
                ", windy='" + windy + '\'' +
                ", windyPower='" + windyPower + '\'' +
                ", humidity='" + humidity + '\'' +
                ", city='" + city + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
