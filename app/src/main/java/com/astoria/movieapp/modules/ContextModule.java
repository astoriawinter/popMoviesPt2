package com.astoria.movieapp.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Astoria on 13/03/2018.
 */
@Module
public class ContextModule {
    private Context context;
    public ContextModule(Context context){
        this.context = context;
    }
    @Provides
    public Context getContext() {
        return context;
    }
}
