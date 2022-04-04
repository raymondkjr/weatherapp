package com.raykimjr.cs571.weatherapp;

import org.json.JSONArray;

import java.io.Serializable;

public class WeatherData implements Serializable {
    private JSONArray weatherData;
    private String cityData;
    private boolean currWeather;
    private String url;

    WeatherData(JSONArray data, String city, boolean curr, String url) {
        this.weatherData = data;
        this.cityData = city;
        this.currWeather = curr;
        this.url = url;
    }

    public JSONArray getWeatherData() { return this.weatherData; }
    public String getCityData() { return this.cityData; }
    public boolean getCurrWeatherData() { return this.currWeather; }
    public String getUrl() { return this.url; }
}
