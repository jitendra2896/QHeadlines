package com.example.qheadlines.Databases;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {


    public static final String AUTHORITY = "com.example.qheadlines";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String NEWS_TABLE_PATH = "news_table";
    public static final String SAVED_NEWS_TABLE_PATH = "saved_table";


    public static class NewsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(NEWS_TABLE_PATH).build();

        public static final String TABLE_NAME = "news_table";
        public static final String HEADLINE_COLUMN = "headlines";
        public static final String DESCRIPTION_COLUMN = "description";
        public static final String URL_COLUMN = "url";
        public static final String IMAGE_URL_COLUMN = "Image_url";
        public static final String SOURCE_COLUMN = "source";
    }

    public static class SavedNewsEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SAVED_NEWS_TABLE_PATH).build();

        public static final String TABLE_NAME = "saved_table";
        public static final String HEADLINE_COLUMN = "headlines";
        public static final String DESCRIPTION_COLUMN = "description";
        public static final String URL_COLUMN = "url";
        public static final String DATE_COLUMN = "date";
        public static final String SOURCE_COLUMN = "source";
    }
}