package com.example.qheadlines.Networking;


import android.net.Uri;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class GetResponse {

    private static final String BASE_URL = "http://newsapi.org/v1/articles";
    private static final String API_KEY = "9449c02eecd44879a14acf889867fe35";
    private static String source = "cnn";

    private static Uri buildUri(){
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("source",source)
                .appendQueryParameter("apiKey",API_KEY)
                .build();

        return uri;
    }

    public static String getHttpResponse(){

        URL url;

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String response = null;
        try {
            url = new URL(buildUri().toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode() == 200){
                inputStream = httpURLConnection.getInputStream();
                response = readStream(inputStream);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            httpURLConnection.disconnect();
        }

        return response;
    }

    private static String readStream(InputStream inputStream){
        if(inputStream == null)
            return null;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            line = bufferedReader.readLine();
            if(line != null){
                builder.append(line);
            }
        }catch (Exception e){

        }
        return builder.toString();
    }

    public static void setSource(String newSource){
        source = newSource;
    }
}
