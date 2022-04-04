package com.raykimjr.cs571.weatherapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyWeather {
    private int weatherCode;
    private Date date;
    private String dateString;
    private float tempMin;
    private float tempMax;

    public DailyWeather(JSONObject obj) throws JSONException, ParseException {
        String dateVal = obj.getString("startTime");
        JSONObject valuesObj = obj.getJSONObject("values");
        this.weatherCode = valuesObj.getInt("weatherCode");
        this.tempMin = (float) valuesObj.getDouble("temperatureMin");
        this.tempMax = (float) valuesObj.getDouble("temperatureMax");
        //Log.i("DAILYWEATHER", obj.toString(3));
        //Log.i("DAILYWEATHER", tempMin + "");
        //Log.i("DAILYWEATHER", tempMax + "");
        //Log.i("DAILYWEATHER", weatherCode + "");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        date = df.parse(dateVal);
        DateFormat viewDF = new SimpleDateFormat("yyyy-MM-dd");
        dateString = viewDF.format(date);
        //Log.i("DAILYWEATHER", dateString);
    }

    public String getDateString() {
        return dateString;
    }

    public int getMinTemp() {
        return Math.round(this.tempMin);
    }

    public int getMaxTemp() {
        return Math.round(this.tempMax);
    }

    public int getWeatherCode() {
        return this.weatherCode;
    }
}
