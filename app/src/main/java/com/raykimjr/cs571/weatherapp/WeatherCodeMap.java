package com.raykimjr.cs571.weatherapp;

import java.util.HashMap;
import java.util.Map;

public class WeatherCodeMap {
    private static Map<Integer, Integer> map;
    private static Map<Integer, String> code;

    public WeatherCodeMap() {
        map = new HashMap<>();
        code = new HashMap<>();
        map.put(4201, R.drawable.rain_heavy);
        map.put(4001, R.drawable.rain);
        map.put(4200, R.drawable.rain_light);
        map.put(6201, R.drawable.freezing_rain_heavy);
        map.put(6001, R.drawable.freezing_rain);
        map.put(6200, R.drawable.freezing_rain_light);
        map.put(6000, R.drawable.freezing_drizzle);
        map.put(4000, R.drawable.drizzle);
        map.put(7101, R.drawable.ice_pellets_heavy);
        map.put(7000, R.drawable.ice_pellets);
        map.put(7102, R.drawable.ice_pellets_light);
        map.put(5101, R.drawable.snow_heavy);
        map.put(5000, R.drawable.snow);
        map.put(5100, R.drawable.snow_light);
        map.put(5001, R.drawable.flurries);
        map.put(8000, R.drawable.tstorm);
        map.put(3000, R.drawable.light_wind);
        map.put(3001, R.drawable.wind);
        map.put(3002, R.drawable.strong_wind);
        map.put(2100, R.drawable.fog_light);
        map.put(2000, R.drawable.fog);
        map.put(1001, R.drawable.cloudy);
        map.put(1102, R.drawable.mostly_cloudy);
        map.put(1101, R.drawable.partly_cloudy_day);
        map.put(1100, R.drawable.mostly_clear_day);
        map.put(1000, R.drawable.clear_day);
        code.put(4201, "Heavy Rain");
        code.put(4001, "Rain");
        code.put(4200, "Light Rain");
        code.put(6201, "Freezing Heavy Rain");
        code.put(6001, "Freezing Rain");
        code.put(6200, "Freezing Light Rain");
        code.put(6000, "Freezing Drizzle");
        code.put(4000, "Drizzle");
        code.put(7101, "Heavy Ice Pellets");
        code.put(7000, "Ice Pellets");
        code.put(7102, "Light Ice Pellets");
        code.put(5101, "Heavy Snow");
        code.put(5000, "Snow");
        code.put(5100, "Light Snow");
        code.put(5001, "Light Flurries");
        code.put(8000, "Thunderstorm");
        code.put(3000, "Light Wind");
        code.put(3001, "Wind");
        code.put(3002, "Strong Wind");
        code.put(2100, "Light Fog");
        code.put(2000, "Fog");
        code.put(1001, "Cloudy");
        code.put(1102, "Mostly Cloudy");
        code.put(1101, "Partly Cloudy");
        code.put(1100, "Mostly Clear");
        code.put(1000, "Clear");
    }

    public int getImage(int code) {
        return map.get(code);
    }

    public String getCode(int code) {
        return this.code.get(code);
    }
}
