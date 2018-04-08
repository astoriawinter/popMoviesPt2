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
import com.astoria.movieapp.adapter.VideoAdapter;
import com.astoria.movieapp.interfaces.DaggerMovieComponent;
import com.astoria.movieapp.interfaces.MovieApi;
import com.astoria.movieapp.interfaces.MovieComponent;
import com.astoria.movieapp.model.Review;
import com.astoria.movieapp.model.Video;
import com.astoria.movieapp.modules.ContextModule;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
public class VideoFragment extends Fragment{
    private MovieApi movieApi;
    private VideoAdapter videoAdapter;
    private RecyclerView recyclerView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        videoAdapter = new VideoAdapter(getActivity().getApplicationContext());
        recyclerView = getView().findViewById(R.id.recyclerViewVideo);
        String id = getArguments().getString("id");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(videoAdapter);
        MovieComponent movieComponent = DaggerMovieComponent.builder()
                .contextModule(new ContextModule(getContext()))
                .build();
        movieApi = movieComponent.getMoveiApi();
        loadDetailedReviews(id);
    }
    private Observable<Video> getVideo(String id){
        return movieApi.getVideo
                        (id,
                        BuildConfig.THE_MOVIE_DB_API_TOKEN);
    }
    private void loadDetailedReviews(String id){
        Observable<Video> videoObservable = getVideo(id);
        videoObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(video -> {
                    videoAdapter.setData(video.getResultVideos());
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        return view;
    }
}
