package com.example.qheadlines;

import android.content.ContentValues;
import android.content.Context;

import com.example.qheadlines.Databases.DatabaseContract;
import com.example.qheadlines.Networking.GetResponse;
import com.example.qheadlines.Networking.JSONParser;

import java.util.ArrayList;

public class NewsDownloadLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<News>> {

    Context context;
    public NewsDownloadLoader(Context context){
        super(context);
        this.context = context;
    }

    private void saveToDatabase(ArrayList<News> newsList){
        context.getContentResolver().delete(DatabaseContract.NewsEntry.CONTENT_URI,null,null);
        for(int i = 0;i<newsList.size();i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.NewsEntry.HEADLINE_COLUMN,newsList.get(i).headLine);
            contentValues.put(DatabaseContract.NewsEntry.DESCRIPTION_COLUMN,newsList.get(i).additionalInformation);
            contentValues.put(DatabaseContract.NewsEntry.URL_COLUMN,newsList.get(i).url);
            contentValues.put(DatabaseContract.NewsEntry.IMAGE_URL_COLUMN,newsList.get(i).imageUrl);
            contentValues.put(DatabaseContract.NewsEntry.SOURCE_COLUMN,newsList.get(i).source);
            getContext().getContentResolver().insert(DatabaseContract.NewsEntry.CONTENT_URI,contentValues);
        }
    }

    @Override
    public void onStartLoading(){
        forceLoad();
    }

    @Override
    public ArrayList<News> loadInBackground() {
        String jsonResponse = GetResponse.getHttpResponse();
        ArrayList<News> newsList = JSONParser.parseJson(jsonResponse);
        saveToDatabase(newsList);
        return newsList;
    }
}
