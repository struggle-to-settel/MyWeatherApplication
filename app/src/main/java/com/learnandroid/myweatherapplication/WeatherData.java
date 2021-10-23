package com.learnandroid.myweatherapplication;

import java.lang.ref.WeakReference;

public class WeatherData {
    private String time,date,place,temperature,icon,condition;
    private String mx_temp,min_temp,wind_kph,pressure_in,humidity,feelsLike_c;

    public WeatherData(){
        //Empty Constructor
    }

    public WeatherData(String date,String place,String temperature,String icon,String condition){
        this.date = date;
        this.place = place;
        this.temperature = temperature;
        this.icon = icon;
        this.condition = condition;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getIcon(){
    return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getMx_temp() {
        return mx_temp;
    }

    public void setMx_temp(String mx_temp) {
        this.mx_temp = mx_temp;
    }

    public String getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(String min_temp) {
        this.min_temp = min_temp;
    }

    public String getWind_kph() {
        return wind_kph;
    }

    public void setWind_kph(String wind_kph) {
        this.wind_kph = wind_kph;
    }

    public String getPressure_in() {
        return pressure_in;
    }

    public void setPressure_in(String pressure_in) {
        this.pressure_in = pressure_in;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getFeelsLike_c() {
        return feelsLike_c;
    }

    public void setFeelsLike_c(String feelsLike_c) {
        this.feelsLike_c = feelsLike_c;
    }
}
