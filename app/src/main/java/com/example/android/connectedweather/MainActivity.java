package com.example.android.connectedweather;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import android.util.Log;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnForecastItemClickListener {

    private static final OkHttpClient mHTTPClient = new OkHttpClient();
    private final static String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mForecastListRV;
    private ForecastAdapter mForecastAdapter;
    private Toast mToast;
    final static String WEATHER_SEARCH_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    final static String WEATHER_SEARCH_ID_PARAM = "id";
    final static String WEATHER_SEARCH_ID = "5720727";
    final static String WEATHER_SEARCH_APPID_PARAM = "APPID";
    final static String WEATHER_SEARCH_APPID = "283e7e64d378124fe6dac8d3f2fb23cb";
    final static String WEATHER_SEARCH_UNITS_PARAM = "units";
    final static String WEATHER_SEARCH_UNITS = "imperial";

    private static final String[] dummyForecastData = {
            "Sunny and Warm - 75F",
            "Partly Cloudy - 72F",
            "Mostly Sunny - 73F",
            "Partly Cloudy - 70F",
            "Occasional Showers - 65F",
            "Showers - 63F",
            "Occasional Showers - 64F",
            "Rainy - 62F",
            "Rainy - 61F",
            "Hurricane - 65F",
            "Windy and Clear - 70F",
            "Sunny and Warm - 77F",
            "Sunny and Warm - 81F"
    };

    private static final String[] dummyDetailedForecastData = {
            "Not a cloud in the sky today, with lows around 52F and highs near 75F.",
            "Clouds gathering in the late afternoon and slightly cooler than the day before, with lows around 49F and highs around 72F",
            "Scattered clouds all day with lows near 52F and highs near 73F",
            "Increasing cloudiness as the day goes on with some cooling; lows near 48F and highs near 70F",
            "Showers beginning in the morning and popping up intermittently throughout the day; lows near 46F and highs near 65F",
            "Showers scattered throughout the day, with lows near 46F and highs of 63F",
            "Showers increasing in intensity towards evening, with lows near 46F and highs near 64F",
            "Steady rain all day; lows near 47F and highs near 62F",
            "More steady rain, building in intensity towards evening; lows near 47F and highs near 61F",
            "Very, very strong winds and heavy rain; make sure you're wearing your raincoat today; lows near 50F and highs near 65F",
            "Rain ending in the very early morning, then clearing, with residual strong winds; lows near 61F and highs around 70F",
            "Beautiful day, with nothing but sunshine; lows near 55F and highs around 77F",
            "Another gorgeous day; lows near 56F and highs around 81F"
    };
    public static class WeatherResult implements Serializable {
        public String dateTime;
        public String temperature;
        public String description;

    }

    public static String buildWeatherSearchURL() {
        return Uri.parse(WEATHER_SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter(WEATHER_SEARCH_ID_PARAM, WEATHER_SEARCH_ID)
                .appendQueryParameter(WEATHER_SEARCH_APPID_PARAM, WEATHER_SEARCH_APPID)
                .appendQueryParameter(WEATHER_SEARCH_UNITS_PARAM, WEATHER_SEARCH_UNITS)
                .build().toString();
    }

    public static ArrayList<WeatherResult> parseWeatherResultJSON(String WeatherResultJSON) {
        try {
            JSONObject json = new JSONObject(WeatherResultJSON);
            JSONArray list = json.getJSONArray("list");
            int cnt = Integer.parseInt(json.getString("cnt"));

            System.out.println("*****cnt: " + cnt);

            ArrayList<WeatherResult> WeatherResultList = new ArrayList<WeatherResult>();

            for(int i = 0; i < cnt; i++) {
                WeatherResult result = new WeatherResult();

                JSONObject listIdx = list.getJSONObject(i);
                JSONObject main = listIdx.getJSONObject("main");

                JSONArray weather = listIdx.getJSONArray("weather");
                JSONObject weatherIdx = weather.getJSONObject(0);

                JSONObject wind = listIdx.getJSONObject("wind");

                result.dateTime = listIdx.getString("dt_txt");
                result.temperature = main.getString("temp");
                result.description = weatherIdx.getString("description");

                WeatherResultList.add(result);
            }
            return WeatherResultList;

        } catch (JSONException e) {
            return null;
        }
    }

    public static String doHttpGet(String url) throws IOException {
        Request req = new Request.Builder().url(url).build();

        Response res = mHTTPClient.newCall(req).execute();
        try {
            return res.body().string();
        } finally {
            res.close();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String weatherSearchURL = buildWeatherSearchURL();
        mForecastListRV = findViewById(R.id.rv_forecast_list);

        mForecastListRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastListRV.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this);
        mForecastListRV.setAdapter(mForecastAdapter);
        Log.d(TAG, "querying search URL: " + weatherSearchURL);
        String searchResults = null;
        try {
            searchResults = doHttpGet(weatherSearchURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        parseWeatherResultJSON(searchResults);
        mForecastAdapter.updateForecastData(
                new ArrayList<String>(Arrays.asList(dummyForecastData)),
                new ArrayList<String>(Arrays.asList(dummyDetailedForecastData))
        );


    }


    @Override
    public void onForecastItemClick(String detailedForecast) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, detailedForecast, Toast.LENGTH_LONG);
        mToast.show();
    }
}