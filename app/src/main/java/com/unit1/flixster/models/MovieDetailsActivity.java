package com.unit1.flixster.models;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.unit1.flixster.databinding.ActivityDetailBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    private ActivityDetailBinding binding;
    public static final String YOUTUBE_API_KEY = "AIzaSyAue8c8L8cKOR_FaoOSdeZHr6QjmEjRrJY";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        binding.tvDetailTitle.setText(movie.getTitle());
        binding.tvDetailOverview.setText(movie.getOverview());
        binding.ratingBar.setRating((float) movie.getRating());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0) {
                        return;
                    } else {
                        String youtubeKey = results.getJSONObject(0).getString("key");
                        Log.d("MovieDetailsActivity", youtubeKey);
                        initializeYoutube(youtubeKey);
                    }
                } catch (JSONException e) {
                    Log.d("MovieDetailsActivity", "Failed to parse JSON");
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });
    }

    private void initializeYoutube(final String youtubeKey) {
        binding.player.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("MovieDetailsActivity", "onInitializationSuccess");
                if (binding.ratingBar.getRating() > 5) {
                    youTubePlayer.loadVideo(youtubeKey);
                    Log.d("MovieDetailsActivity", "Video autoplayed based on rating");
                    Toast.makeText(MovieDetailsActivity.this, "Video autoplayed based on high rating", Toast.LENGTH_SHORT).show();
                } else {
                    youTubePlayer.cueVideo(youtubeKey);
                    Log.d("MovieDetailsActivity", "Video cued");
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("MovieDetailsActivity", "onInitializationFailure");
                Log.d("MovieDetailsActivity", youTubeInitializationResult.toString());
            }
        });
    }
}