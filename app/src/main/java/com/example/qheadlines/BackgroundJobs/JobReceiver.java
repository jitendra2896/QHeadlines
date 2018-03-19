package com.example.qheadlines.BackgroundJobs;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;

import com.example.qheadlines.Networking.GetResponse;
import com.example.qheadlines.Networking.JSONParser;
import com.example.qheadlines.News;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

public class JobReceiver extends JobService{

    private AsyncTask asyncTask;
    String defaultNewsSource;
    SharedPreferences preferences;
    String headline;
    String summary;
    String imageUrl;
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        defaultNewsSource = preferences.getString("default_news","cnn");
        GetResponse.setSource(defaultNewsSource);
        asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String response;
                response = GetResponse.getHttpResponse();
                ArrayList<News> arrayList = JSONParser.parseJson(response);
                headline = arrayList.get(0).headLine;
                summary = arrayList.get(0).additionalInformation;
                imageUrl = arrayList.get(0).imageUrl;
                return null;
            }

            @Override
            protected void onPostExecute(Object object){
                Notifications.notifyUserCustom(JobReceiver.this,headline, summary,imageUrl);
                jobFinished(jobParameters,true);
            }
        };
        asyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}