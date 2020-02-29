package com.example.android.connectedweather;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;




public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnSearchItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mForecastListRV;
    private ForecastAdapter mForecastAdapter;
    private EditText mSearchBoxET;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mErrorMessageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mForecastListRV = findViewById(R.id.rv_forecast_list);


        mForecastListRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastListRV.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this);
        mForecastListRV.setAdapter(mForecastAdapter);

        mErrorMessageTV = findViewById(R.id.tv_loading_error);
        mLoadingIndicatorPB = findViewById(R.id.pb_loading);

        doWeatherSearch("Corvallis");
    }

    private void doWeatherSearch(String searchQuery) {
        String url = WeatherUtils.buildGitHubSearchURL(searchQuery);
        Log.d(TAG, "querying url: " + url);
        new WeatherSearchTask().execute(url);
    }

    @Override
    public void onSearchItemClick(WeatherUtils.WeatherRepo repo) {
        Log.d(TAG, "click on item");
        Intent intent = new Intent(this, ResultSearch.class);
        intent.putExtra(WeatherUtils.EXTRA_WEATHER_REPO, repo);
        startActivity(intent);
    }

    public class WeatherSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicatorPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHttpGet(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
            if (s != null) {
                mErrorMessageTV.setVisibility(View.INVISIBLE);
                mForecastListRV.setVisibility(View.VISIBLE);
                WeatherUtils.WeatherRepo[] repos = WeatherUtils.parseWeatherSearchResults(s);
                mForecastAdapter.updateSearchResults(repos);
            } else {
                mErrorMessageTV.setVisibility(View.VISIBLE);
                mForecastListRV.setVisibility(View.INVISIBLE);
            }
        }
    }
}
