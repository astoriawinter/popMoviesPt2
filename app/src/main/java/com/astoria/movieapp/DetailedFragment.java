package com.astoria.movieapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.method.MovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astoria.movieapp.adapter.MovieAdapter;
import com.astoria.movieapp.data.MovieContract;
import com.astoria.movieapp.interfaces.DaggerMovieComponent;
import com.astoria.movieapp.interfaces.MovieComponent;
import com.astoria.movieapp.model.ResultMovie;
import com.astoria.movieapp.modules.ContextModule;
import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DetailedFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((DetailedActivity)getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String overviewText = getArguments().getString("overview");
        TextView overview = getView().findViewById(R.id.description);
        overview.setText(overviewText);
        FloatingActionButton floatingActionButton = getView().findViewById(R.id.floatingButton);
        TextView date = getView().findViewById(R.id.dateText);
        TextView rating = getView().findViewById(R.id.userRatingText);
        CollapsingToolbarLayout collapsing_toolbar = getView().findViewById(R.id.collapsing_toolbar);
        ImageView imageView = getView().findViewById(R.id.imageViewDetailed);
        String title = getArguments().getString("title");
        collapsing_toolbar.setTitle(title);
        String dateString = getArguments().getString("date");
        date.setText(dateString);
        String ratingString = getArguments().getString("vote");
        String imageUri = getArguments().getString("poster");
        String url = getArguments().getString("URL");
        rating.setText(ratingString);
        MovieComponent daggerMovieComponent = DaggerMovieComponent.builder()
                .contextModule(new ContextModule(getActivity()))
                .build();
        Picasso picasso = daggerMovieComponent.getPicasso();
        picasso.with(getContext())
                .load(url)
                .into(imageView);
        String id = getArguments().getString("id");
        Map<String, String> argsMap = new HashMap<>();
        argsMap.put("id", id);
        argsMap.put("title", title);
        argsMap.put("description", overviewText);
        argsMap.put("date", dateString);
        argsMap.put("rating", ratingString);
        argsMap.put("image_uri", imageUri);
        floatingActionButton.setOnClickListener(view ->{
            favoriteClicked(id, argsMap);
        });
        boolean favoriteWasAdded = findFavouriteById(id);
        if (favoriteWasAdded) {
            floatingActionButton.setImageResource(R.drawable.ic_favourite_white);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_favourite);
        }
    }
    private boolean findFavouriteById(final String id) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                "id = "+ id,
                null,
                null
        );
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int idState = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_STATE);
            int state = cursor.getInt(idState);
            cursor.close();
            return state == 0;
        }
        return true;
    }
    private void addFavourite(Map<String, String> map) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", map.get("id"));
        contentValues.put("title", map.get("title"));
        contentValues.put("description", map.get("description"));
        contentValues.put("date", map.get("date"));
        contentValues.put("rating", map.get("rating"));
        contentValues.put("image_uri", map.get("image_uri"));
        contentValues.put("state", 1);
        contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
    }
    private void deleteFavourite(final String id){
        ContentResolver contentResolver = getActivity().getContentResolver();
        contentResolver.delete(
                MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(id).build(),
                "id = "+ id,
                null);

    }
    private Observable<Boolean> handleFavourites(final String id, final Map<String, String> map){
        return Observable.create(subscriber -> {
            boolean favourite = findFavouriteById(id);
            if (favourite) {
                addFavourite(map);
                subscriber.onNext(true);
            }
            else{
                deleteFavourite(id);
                Intent intent = new Intent("favourite-updated");
                intent.putExtra("MOVIE_ID_EXTRA", id);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                subscriber.onNext(false);
            }
            subscriber.onCompleted();
        });
    }
    public void favoriteClicked(final String id, Map<String, String> map) {
        handleFavourites(id, map)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean favoriteWasAdded) {
                        FloatingActionButton floatingActionButton = getView().findViewById(R.id.floatingButton);
                        if (favoriteWasAdded) {
                            floatingActionButton.setImageResource(R.drawable.ic_favourite);
                        } else {
                            floatingActionButton.setImageResource(R.drawable.ic_favourite_white);
                        }
                    }
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detailed, container, false);
    }
}
