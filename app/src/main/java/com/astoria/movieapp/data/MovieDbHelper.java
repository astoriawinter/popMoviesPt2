package com.astoria.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.astoria.movieapp.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "moviesDb.db";
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE "
                + MovieEntry.TABLE_NAME + " ("
                + MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY, "
                + MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_DATE + " DATE NOT NULL, "
                + MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_IMAGE_URI + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_RATING + " INTEGER NOT NULL, "
                + MovieEntry.COLUMN_STATE + " INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
