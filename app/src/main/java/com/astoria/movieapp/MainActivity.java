package com.astoria.movieapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import com.astoria.movieapp.adapter.MovieAdapter;
import com.astoria.movieapp.data.MovieContract;
import com.astoria.movieapp.interfaces.DaggerMovieComponent;
import com.astoria.movieapp.interfaces.MovieApi;
import com.astoria.movieapp.interfaces.MovieComponent;
import com.astoria.movieapp.model.Movie;
import com.astoria.movieapp.model.ResultMovie;
import com.astoria.movieapp.modules.ContextModule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private MovieApi movieApi;
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private Observable<Movie> movies;
    private static int page = 0;
    private boolean canBeLoaded = true;
    private EndlessScrollListener scrollListener;
    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        int numberOfColumns = calculateNoOfColumns(this);
        final GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, numberOfColumns, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        scrollListener = new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, final int totalItemsCount, final RecyclerView view) {
                if (canBeLoaded) {
                    scrollListener.setLoading(true);
                    loadNextPage(getSortType(PreferenceManager.getDefaultSharedPreferences(MainActivity.this)));
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        MovieComponent daggerMovieComponent = DaggerMovieComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
        movieApi = daggerMovieComponent.getMoveiApi();
        Picasso picasso = daggerMovieComponent.getPicasso();
        movieAdapter = new MovieAdapter(MainActivity.this, picasso);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.hasExtra("MOVIE_ID_EXTRA")){
                    String id = intent.getStringExtra("MOVIE_ID_EXTRA");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    if (getString(R.string.sort_favorites).equals(getSortType(sharedPreferences))){
                        movieAdapter.removeItemById(id);
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("favourite-updated"));
        SetupSharedPrefrences();
    }
    private void SetupSharedPrefrences()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = getSortType(sharedPreferences);
        loadNextPage(sort);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private String getSortType(SharedPreferences sharedPreferences)
    {
        return sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_popularity));
    }
    private Cursor listFavourites(){
        ContentResolver contentResolver = getContentResolver();
        return contentResolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                "state = 1",
                null,
                null
        );
    }
    private List<ResultMovie> retrieveData(){
        Cursor cursor = listFavourites();
        if (cursor != null && cursor.getCount() > 0)
        {
            List<ResultMovie> resultMovieList = new ArrayList<>();
            while (cursor.moveToNext()) {
                int indexId = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);
                int indexTitle = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
                int indexDescription = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCRIPTION);
                int indexDate = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE);
                int indexRating = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
                int indexURL = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URI);
                ResultMovie resultMovie = new ResultMovie();
                resultMovie.setId(cursor.getInt(indexId));
                resultMovie.setTitle(cursor.getString(indexTitle));
                resultMovie.setOverview(cursor.getString(indexDescription));
                resultMovie.setReleaseDate(cursor.getString(indexDate));
                resultMovie.setVoteAverage(cursor.getDouble(indexRating));
                resultMovie.setPosterPath(cursor.getString(indexURL));
                resultMovieList.add(resultMovie);
            }
            return resultMovieList;
        }
        return null;
    }
    private void onLoadFavourites() {
        movies = getFavourites();
        movies.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Movie>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Movie movie) {
                        scrollListener.setLoading(false);
                        movieAdapter.setData(movie.getResultMovies());
                        recyclerView.setAdapter(movieAdapter);
                    }
                });
    }
    private void loadNextPage(String type){
        switch (type) {
            case "favourite":
                onLoadFavourites();
                break;
            case "popular":
            case "top_rated":
                loadMoviesPage(type);
                break;
        }
    }
    private void loadMoviesPage(String type) {
        movies = getMovies(type);
        movies.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Movie>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Movie movie) {
                        if (page == 1) {
                            scrollListener.resetState();
                            movieAdapter.setData(movie.getResultMovies());
                            recyclerView.setAdapter(movieAdapter);
                        } else {
                            scrollListener.setLoading(false);
                            movieAdapter.addData(movie.getResultMovies());
                        }
                    }
                });
    }

    private Observable<Movie> getFavourites(){
        canBeLoaded = false;
        Movie movie = new Movie();
        movie.setResultMovies(retrieveData());
        return Observable.just(movie);
    }
    private Observable<Movie> getMovies(String sort) {
        canBeLoaded = true;
        return movieApi.getMovie(
                sort,
                BuildConfig.THE_MOVIE_DB_API_TOKEN,
                getString(R.string.language),
                ++page);
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
            String sort = getSortType(sharedPreferences);
            page = 0;
            loadNextPage(sort);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}