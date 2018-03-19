package com.example.qheadlines.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 7;

    public DbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQLString1 = "CREATE TABLE "+DatabaseContract.NewsEntry.TABLE_NAME+
                "("+DatabaseContract.NewsEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                DatabaseContract.NewsEntry.HEADLINE_COLUMN+" TEXT NOT NULL, "+
                DatabaseContract.NewsEntry.DESCRIPTION_COLUMN+" TEXT NOT NULL, "+
                DatabaseContract.NewsEntry.URL_COLUMN+" TEXT NOT NULL, "+
                DatabaseContract.NewsEntry.IMAGE_URL_COLUMN+" TEXT NOT NULL, "+
                DatabaseContract.NewsEntry.SOURCE_COLUMN+" TEXT NOT NULL);";

        final String SQLString2 = "CREATE TABLE "+DatabaseContract.SavedNewsEntry.TABLE_NAME+
                "("+DatabaseContract.SavedNewsEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                DatabaseContract.SavedNewsEntry.HEADLINE_COLUMN+" TEXT NOT NULL, "+
                DatabaseContract.SavedNewsEntry.DESCRIPTION_COLUMN+" TEXT NOT NULL, "+
                DatabaseContract.SavedNewsEntry.URL_COLUMN+" TEXT NOT NULL, "+
                DatabaseContract.SavedNewsEntry.DATE_COLUMN+" BIGINT NOT NULL, "+
                DatabaseContract.SavedNewsEntry.SOURCE_COLUMN+" TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQLString1);
        sqLiteDatabase.execSQL(SQLString2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DatabaseContract.NewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DatabaseContract.SavedNewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
