package com.astoria.movieapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.astoria.movieapp.adapter.ReviewAdapter;
import com.astoria.movieapp.interfaces.DaggerMovieComponent;
import com.astoria.movieapp.interfaces.MovieApi;
import com.astoria.movieapp.interfaces.MovieComponent;
import com.astoria.movieapp.model.Review;
import com.astoria.movieapp.model.Video;
import com.astoria.movieapp.modules.ContextModule;
import com.squareup.picasso.Picasso;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        BottomNavigationView bottomNavigationView = findViewById(R.id.design_bottom_sheet);
        Intent intent = getIntent();
        String id = Integer.toString(intent.getIntExtra("id", 0));
        String title = intent.getStringExtra("title");
        String overview = intent.getStringExtra("overview");
        String date = intent.getStringExtra("date");
        String vote = intent.getStringExtra("vote");
        String poster = intent.getStringExtra("poster");
        String URL = intent.getStringExtra("URL");
        Bundle detailedBundle = new Bundle();
        detailedBundle.putString("id", id);
        detailedBundle.putString("title", title);
        detailedBundle.putString("overview", overview);
        detailedBundle.putString("date", date);
        detailedBundle.putString("vote", vote);
        detailedBundle.putString("poster", poster);
        detailedBundle.putString("URL", URL);
        DetailedFragment detailed = new DetailedFragment();
        detailed.setArguments(detailedBundle);
        Bundle idBundle = new Bundle();
        idBundle.putString("id", id);
        ReviewFragment fragment = new ReviewFragment();
        VideoFragment videoFragment = new VideoFragment();
        videoFragment.setArguments(idBundle);
        fragment.setArguments(idBundle);
        if (savedInstanceState == null) {
            navigateToFragment(detailed);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.detailedPage: {
                    navigateToFragment(detailed);
                    break;
                }
                case  R.id.reviews:
                    navigateToFragment(fragment);
                    break;
                case R.id.videos:
                    navigateToFragment(videoFragment);
                    break;
            }
            return true;
        });
    }
    void navigateToFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
