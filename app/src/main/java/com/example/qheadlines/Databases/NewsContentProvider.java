package com.example.qheadlines.Databases;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

public class NewsContentProvider extends ContentProvider{

    DbHelper dbHelper;
    private static final int NEWS = 100;
    private static final int NEWS_WITH_ID = 101;

    private static final int SAVED_NEWS = 102;
    private static final int SAVED_NEWS_ID = 103;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Uri matcher for current news table
        uriMatcher.addURI(DatabaseContract.AUTHORITY,DatabaseContract.NEWS_TABLE_PATH,NEWS);
        uriMatcher.addURI(DatabaseContract.AUTHORITY,DatabaseContract.NEWS_TABLE_PATH+"/#",NEWS_WITH_ID);

        //Uri matcher for saved_news table
        uriMatcher.addURI(DatabaseContract.AUTHORITY,DatabaseContract.SAVED_NEWS_TABLE_PATH,SAVED_NEWS);
        uriMatcher.addURI(ContactsContract.AUTHORITY,DatabaseContract.SAVED_NEWS_TABLE_PATH+"/#",SAVED_NEWS_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        int match = uriMatcher.match(uri);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor returnCursor;
        switch (match){
            case NEWS:
                returnCursor = database.query(DatabaseContract.NewsEntry.TABLE_NAME,strings,s,strings1,null,null,s1);

                if(returnCursor == null){
                    throw new SQLException("Cannot Query Data at uri "+uri);
                }
                break;
            case SAVED_NEWS:
                returnCursor = database.query(DatabaseContract.SavedNewsEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                if(returnCursor == null){
                    throw new SQLException("Cannot Query Data at uri "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        Uri returnUri;
        switch (match){
            case NEWS:
                long id = database.insert(DatabaseContract.NewsEntry.TABLE_NAME,null,contentValues);
                if(id>0){
                    returnUri = ContentUris.withAppendedId(DatabaseContract.NewsEntry.CONTENT_URI,id);
                }
                else{
                    throw new SQLException("Failed to insert row at "+uri);
                }
                break;
            case SAVED_NEWS:
                id = database.insert(DatabaseContract.SavedNewsEntry.TABLE_NAME,null,contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(DatabaseContract.SavedNewsEntry.CONTENT_URI,id);
                }
                else{
                    throw new SQLException("Failed to insert row at "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;

    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {

        int match = uriMatcher.match(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int taskDeleted;
        switch (match){
            case NEWS:
               taskDeleted = database.delete(DatabaseContract.NewsEntry.TABLE_NAME,s,strings);
                break;
            case SAVED_NEWS:
                taskDeleted = database.delete(DatabaseContract.SavedNewsEntry.TABLE_NAME,s,strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }

        if(taskDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return taskDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
