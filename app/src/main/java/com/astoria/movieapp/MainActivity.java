package com.astoria.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import com.astoria.movieapp.adapter.MovieAdapter;
import com.astoria.movieapp.interfaces.DaggerMovieComponent;
import com.astoria.movieapp.interfaces.MovieApi;
import com.astoria.movieapp.interfaces.MovieComponent;
import com.astoria.movieapp.model.Movie;
import com.astoria.movieapp.modules.ContextModule;
import com.squareup.picasso.Picasso;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private MovieApi movieApi;
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private Observable<Movie> movies;
    private static int page = 1;
    private String sort = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        int numberOfColumns = calculateNoOfColumns(this);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        EndlessScrollListener scrollListener = new EndlessScrollListener(gridLayoutManager) {
            int pastVisiblesItems, visibleItemCount, totalItemCount;
            @Override
            public void onLoadMore(int page, final int totalItemsCount, final RecyclerView view) {
                loadNextPage();
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                visibleItemCount = gridLayoutManager.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
                if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                    onLoadMore(page, totalItemCount, recyclerView);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        SetupSharedPrefrences();
        MovieComponent daggerMovieComponent = DaggerMovieComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
        movieApi = daggerMovieComponent.getMoveiApi();
        movieAdapter = new MovieAdapter(MainActivity.this);
        loadFirstPage();
    }
    private void SetupSharedPrefrences()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sort = getSortType(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    private String getSortType(SharedPreferences sharedPreferences)
    {
        return sharedPreferences.getBoolean(getString(R.string.sort_key), true) ?
                getString(R.string.sort_popularity) : getString(R.string.sort_top_rated);
    }
    private void loadFirstPage() {
        page = 1;
        movies = getMovies(page);
        movies.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movie -> {
                    movieAdapter.setData(movie.getResultMovies());
                    recyclerView.setAdapter(movieAdapter);
                });
    }
    private void loadNextPage() {
        movies = getMovies(++page);
        movies.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movie -> {
                    movieAdapter.addData(movie.getResultMovies());
                });
    }
    private Observable<Movie> getMovies(int page) {
        return movieApi.getMovie(
                sort,
                BuildConfig.THE_MOVIE_DB_API_TOKEN,
                getString(R.string.language),
                page);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
        {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.sort_key))) {
            sort = getSortType(sharedPreferences);
            loadFirstPage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
