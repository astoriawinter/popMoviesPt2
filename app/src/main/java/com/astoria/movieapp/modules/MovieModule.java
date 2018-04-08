package com.astoria.movieapp.modules;

import android.bluetooth.le.ScanSettings;

import com.astoria.movieapp.interfaces.MovieApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class MovieModule {
    @Provides
    @Singleton
    public MovieApi movieApi(Retrofit retrofit) {
        return retrofit.create(MovieApi.class);
    }
    @Provides
    @Singleton
    public Retrofit retrofit(GsonConverterFactory converterFactory, RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org")
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .addConverterFactory(converterFactory)
                .build();
    }
    @Provides
    public RxJavaCallAdapterFactory getRxAdapterFactory(){
        return RxJavaCallAdapterFactory.create();
    }
    @Provides
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }
    @Provides
    public GsonConverterFactory gsonConverterFactory(Gson gson){
        return GsonConverterFactory.create(gson);
    }
}
