package com.astoria.movieapp.interfaces;
import com.astoria.movieapp.modules.MovieModule;
import com.astoria.movieapp.modules.PicassoModule;
import com.squareup.picasso.Picasso;
import javax.inject.Singleton;

import dagger.Component;
@Singleton
@Component(modules = {MovieModule.class, PicassoModule.class})
public interface MovieComponent {
    MovieApi getMoveiApi();
    Picasso getPicasso();
}
