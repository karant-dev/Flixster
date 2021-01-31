package com.unit1.flixster.models;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.unit1.flixster.MainActivity;
import com.unit1.flixster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import butterknife.BindView;
import okhttp3.Headers;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    public static final String YOUTUBE_API_KEY = "AIzaSyAue8c8L8cKOR_FaoOSdeZHr6QjmEjRrJY";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    @BindView(R.id.tvDetailTitle) TextView tvDetailTitle;
    @BindView(R.id.tvDetailOverview) TextView tvDetailOverview;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.player) YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvDetailTitle.setText(movie.getTitle());
        tvDetailOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getRating());

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
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("MovieDetailsActivity", "onInitializationSuccess");
                if(ratingBar.getRating() > 5) {
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