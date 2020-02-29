package com.example.android.connectedweather;

import com.google.gson.Gson;

import java.io.Serializable;

public class WeatherUtils {
    public static final String EXTRA_WEATHER_REPO = "WeatherUtils.WeatherRepo";

    public static class WeatherRepo implements Serializable {
        public String dt_txt;
        public Weather_main main;
        public Weather_weather[] weather;
    }

    public static class Weather_main implements Serializable{
        public String temp;
        public String temp_min;
        public String temp_max;
    }

    public static class Weather_weather implements Serializable{
        public String main;
        public String description;
        public String icon;
    }

    public static class WeatherSearchResults {
        public WeatherRepo[] list;
    }

    public static String buildGitHubSearchURL(String query) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="
                + query + "&APPID=958a0de1bee6b23465b44cc7122ecf4b";
        return url;
    }

    public static String buildWeatherIconURL(String icon) {
        String url = "http://openweathermap.org/img/wn/" + icon +".png";
        return url;
    }

    public static WeatherRepo[] parseWeatherSearchResults(String json) {
        Gson gson = new Gson();
        WeatherSearchResults results = gson.fromJson(json, WeatherSearchResults.class);
        if (results != null && results.list != null) {
            return results.list;
        } else {
            return null;
        }
    }

}
