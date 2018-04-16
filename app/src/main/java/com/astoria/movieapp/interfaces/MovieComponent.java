package com.astoria.movieapp.interfaces;
import android.content.Context;

import com.astoria.movieapp.modules.ContextModule;
import com.astoria.movieapp.modules.MovieModule;
import com.astoria.movieapp.modules.PicassoModule;
import com.squareup.picasso.Picasso;
import javax.inject.Singleton;

import dagger.Component;
@Singleton
@Component(modules = {MovieModule.class, PicassoModule.class, ContextModule.class})
public interface MovieComponent {
    MovieApi getMoveiApi();
    Picasso getPicasso();
    Context getContext();
}
