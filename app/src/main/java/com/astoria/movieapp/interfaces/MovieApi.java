package com.astoria.movieapp.interfaces;

import com.astoria.movieapp.model.Movie;
import com.astoria.movieapp.model.Review;
import com.astoria.movieapp.model.Video;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface MovieApi {
    @GET("/3/movie/{sort}")
    Observable<Movie> getMovie(@Path("sort") String sort,
                               @Query("api_key") String key,
                               @Query("language") String language,
                               @Query("page") int page);

    @GET("/3/movie/{id}/videos")
    Observable<Video> getVideo(@Path("id") String id,
                         @Query("api_key") String key);

    @GET("/3/movie/{id}/reviews")
    Observable<Review> getReviews(@Path("id") String id,
                                  @Query("api_key") String key,
                                  @Query("language") String language,
                                  @Query("page") int page);
}
