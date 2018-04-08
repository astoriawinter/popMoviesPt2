package com.astoria.movieapp.modules;

import android.content.Context;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module(includes = ContextModule.class)
public class PicassoModule {
    @Provides
    @Singleton
    public Picasso picasso(Context context, OkHttp3Downloader okHttp3Downloader){
        return new Picasso.Builder(context).downloader(okHttp3Downloader).build();
    }
    @Provides
    public OkHttp3Downloader okHttp3Downloader(OkHttpClient okHttpClient){
        return new OkHttp3Downloader(okHttpClient);
    }
    @Provides
    public OkHttpClient okHttpClient(){
        return new OkHttpClient()
                .newBuilder()
                .build();
    }
}
