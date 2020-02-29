package com.example.android.connectedweather;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.SearchResultView> {

    private WeatherUtils.WeatherRepo[] mRepos;
    OnSearchItemClickListener mSearchItemClickListener;

    public interface OnSearchItemClickListener {
        void onSearchItemClick(WeatherUtils.WeatherRepo repo);
    }

    ForecastAdapter(OnSearchItemClickListener searchItemClickListener) {
        mSearchItemClickListener = searchItemClickListener;
    }

    public void updateSearchResults(WeatherUtils.WeatherRepo[] repos) {
        mRepos = repos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRepos  != null) {
            return mRepos .length;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public SearchResultView  onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_result, parent, false);
        return new SearchResultView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultView holder, int position) {
        holder.bind(mRepos[position]);
    }


    class SearchResultView extends RecyclerView.ViewHolder {
        private TextView mSearchResultTV;

        public SearchResultView(View itemView) {
            super(itemView);
            mSearchResultTV  = itemView.findViewById(R.id.tv_search_result);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeatherUtils.WeatherRepo searchResult = mRepos[getAdapterPosition()];
                    mSearchItemClickListener.onSearchItemClick(searchResult);
                }
            });
        }

        public void bind(WeatherUtils.WeatherRepo repo) {
            double main = Double.parseDouble(repo.main.temp) - 273.15;
            main = (main * 9/5) + 32;
            String temp =  String.valueOf((int)main);

            String text = repo.dt_txt + " - " + repo.weather[0].main + " - "
                    + temp + " F";
            mSearchResultTV.setText(text);
        }
    }
}
