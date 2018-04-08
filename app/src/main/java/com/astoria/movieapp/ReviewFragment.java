package com.astoria.movieapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astoria.movieapp.adapter.ReviewAdapter;
import com.astoria.movieapp.interfaces.DaggerMovieComponent;
import com.astoria.movieapp.interfaces.MovieApi;
import com.astoria.movieapp.interfaces.MovieComponent;
import com.astoria.movieapp.model.Review;
import com.astoria.movieapp.modules.ContextModule;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
public class ReviewFragment extends Fragment{
    private MovieApi movieApi;
    private ReviewAdapter reviewAdapter;
    private RecyclerView recyclerView;
    private static int page = 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reviewAdapter = new ReviewAdapter();
        recyclerView = getView().findViewById(R.id.recyclerViewReview);
        String id = getArguments().getString("id");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(reviewAdapter);
        MovieComponent movieComponent = DaggerMovieComponent.builder()
                .contextModule(new ContextModule(getContext()))
                .build();
        movieApi = movieComponent.getMoveiApi();
        loadDetailedReviews(id);
    }
    private Observable<Review> getReview(String id){
        return movieApi.getReviews
                (id,
                        BuildConfig.THE_MOVIE_DB_API_TOKEN,
                        getString(R.string.language),
                        page);
    }
    private void loadDetailedReviews(String id){
        Observable<Review> reviewObservable = getReview(id);
        reviewObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(review -> {
                    reviewAdapter.setData(review.getResultReviews());
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        return view;
    }

}
