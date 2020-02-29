package com.example.android.connectedweather;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


import android.os.AsyncTask;
import android.util.Log;
import 	androidx.core.app.ShareCompat;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.io.InputStream;

public class ResultSearch extends AppCompatActivity {

    private TextView mRepoDtTV;
    private TextView mRepoWeatherTV;
    private TextView mRepoDescriptionTV;
    private WeatherUtils.WeatherRepo mRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast_list_item);

        mRepoDtTV = findViewById(R.id.tv_repo_dt);
        mRepoWeatherTV = findViewById(R.id.tv_repo_weather);
        mRepoDescriptionTV = findViewById(R.id.tv_repo_description);

        mRepo = null;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(WeatherUtils.EXTRA_WEATHER_REPO)) {
            mRepo = (WeatherUtils.WeatherRepo) intent.getSerializableExtra(WeatherUtils.EXTRA_WEATHER_REPO);
            mRepoDtTV.setText(mRepo.dt_txt);
            new DownloadImageTask((ImageView)findViewById(R.id.tv_repo_icon))
                    .execute(WeatherUtils.buildWeatherIconURL(mRepo.weather[0].icon));
            mRepoDescriptionTV.setText(mRepo.weather[0].description);
            double temp_min = Double.parseDouble(mRepo.main.temp_min) - 273.15;
            temp_min = (temp_min * 9/5) + 32;
            String min = String.valueOf((int)temp_min);

            double temp_max = Double.parseDouble(mRepo.main.temp_max) - 273.15;
            temp_max = (temp_max * 9/5) + 32;
            String max = String.valueOf((int)temp_max);

            String temp = "Min Tempture: " + min  + " F" + "\nMax Tempture: " + max + " F";
            mRepoWeatherTV.setText(temp);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_on_web:
                viewRepoOnWeb();
                return true;
            case R.id.action_share:
                shareRepo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void viewRepoOnWeb() {
        if (mRepo != null) {
            Uri repoURI = Uri.parse("https://www.google.com/maps/place/Corvallis");
            Intent I = new Intent(Intent.ACTION_VIEW, repoURI);
            if (I.resolveActivity(getPackageManager()) != null) {
                startActivity(I);
            }
        }
    }

    public void shareRepo() {
        if (mRepo != null) {
            String shareText = getString(R.string.share_repo_text, mRepo.dt_txt, mRepo.weather[0].main);
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(shareText)
                    .setChooserTitle("Do you want to share this repo to other?")
                    .startChooser();
        }
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView Icon;
        public DownloadImageTask(ImageView bmImage) {
            this.Icon = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error：", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            this.Icon.setImageBitmap(result);
        }
    }
}
