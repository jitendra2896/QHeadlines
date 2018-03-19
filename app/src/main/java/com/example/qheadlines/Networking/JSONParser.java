package com.example.qheadlines.Networking;

import android.util.Log;

import com.example.qheadlines.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {
    public static ArrayList<News> parseJson(String source){
        ArrayList<News> newsList = null;
        if(source == null){
            return null;
        }

        try{
            newsList = new ArrayList<>();
            JSONObject rootObject = new JSONObject(source);
            if(!rootObject.getString("status").equals("ok"))
                return null;
            String newsSource = rootObject.getString("source");
            JSONArray articleArray = rootObject.getJSONArray("articles");
            for(int i = 0;i<articleArray.length();i++){
                JSONObject jsonObject = articleArray.getJSONObject(i);
                String headLine = jsonObject.getString("title");
                String description = jsonObject.getString("description");
                String url = jsonObject.getString("url");
                String imageUrl = jsonObject.getString("urlToImage");
                newsList.add(new News(headLine,description,url,imageUrl,newsSource));
            }

        }catch (JSONException e){
            Log.e("JSON RESOPONSE","*****SOMETHING IS WRONG****");
        }
        return newsList;
    }
}
