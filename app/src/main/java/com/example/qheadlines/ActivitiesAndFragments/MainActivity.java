package com.example.qheadlines.ActivitiesAndFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.qheadlines.BackgroundJobs.JobScheduler;
import com.example.qheadlines.BackgroundJobs.Notifications;
import com.example.qheadlines.Databases.DatabaseContract;
import com.example.qheadlines.Networking.GetResponse;
import com.example.qheadlines.News;
import com.example.qheadlines.NewsDownloadLoader;
import com.example.qheadlines.Adapters.NewsHeadlineAdapter;
import com.example.qheadlines.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<News>>,SharedPreferences.OnSharedPreferenceChangeListener{

    RecyclerView recyclerView;
    NewsHeadlineAdapter adapter;
    ProgressBar progressBar;
    DrawerLayout drawerLayout;
    ListView drawerListView;
    ActionBarDrawerToggle drawerToggle;
    private String[] newsProvider;
    SharedPreferences preference;
    boolean isTabletLargeLandscape;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout relativeLayout;
    ActionBar actionBar;

    public static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actionBar = getSupportActionBar();

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);
        newsProvider = getResources().getStringArray(R.array.news_providers);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);
        drawerListView.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_view_layout,newsProvider));
        recyclerView = (RecyclerView) findViewById(R.id.news_headline_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setInterpolator(new AccelerateDecelerateInterpolator());
        preference =  PreferenceManager.getDefaultSharedPreferences(this);
        adapter = new NewsHeadlineAdapter(null,this,preference);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
        View tabletCheckView = findViewById(R.id.in_large_tablet_mode_landscape_view);
        isTabletLargeLandscape = tabletCheckView != null && tabletCheckView.getVisibility() == View.VISIBLE;

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                actionBar.setHomeAsUpIndicator(R.drawable.hamburger);
            }
            @Override
            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                actionBar.setHomeAsUpIndicator(R.drawable.hamburger_open);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        if(actionBar != null){
            actionBar.setHomeAsUpIndicator(R.drawable.hamburger);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        if(isTabletLargeLandscape){
            layoutManager = new GridLayoutManager(this,3);
        }
        else {
            if(findViewById(R.id.in_large_tablet_mode_portrait_view) != null&&!preference.getString("default_view_layouts","card_view").equals("list_view")) {
                layoutManager = new GridLayoutManager(this, 2);
            }
            else {
                layoutManager = new LinearLayoutManager(this);

            }
        }

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        preference.registerOnSharedPreferenceChangeListener(this);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
        setDataUsingPreferences();

    }

    @Override
    public void onResume(){
        super.onResume();
        JobScheduler.scheduleJob(this);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    //Start the Loader for getting the data from the internet
    public void startAsyncTask(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            getSupportLoaderManager().destroyLoader(LOADER_ID);
            getSupportLoaderManager().initLoader(LOADER_ID,null,this);
        }
        else{
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    //Get the news headlines stored in the database
    public Cursor getData(){

        return getContentResolver().query(DatabaseContract.NewsEntry.CONTENT_URI,null,null,null,null);
    }


    //TODO fix menu problem
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.news_provider_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        if(id == R.id.settings_menu_id){
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        if(drawerToggle.onOptionsItemSelected(menuItem)){
            return true;
        }

        if(id == R.id.saved_news_menu_id){
            Intent intent = new Intent(this,SavedNewsActivity.class);
            startActivity(intent);
        }
        if(id == R.id.dummy_notification_menu_id){
            Notifications.notifyUserCustom(this,"how","Nothing","http://www.guides.global/images/guides/global/dummy_web_page.jpg");
        }

        return super.onOptionsItemSelected(menuItem);
    }


    //Set the correct destination to get news from after user select a news source from drawer
    public boolean onItemClicked(int position){
        switch (position){
            case 0:
                GetResponse.setSource("bbc-sport");
                startAsyncTask();
                return true;
            case 1:
                GetResponse.setSource("espn-cric-info");
                startAsyncTask();
                return true;
            case 2:
                GetResponse.setSource("business-insider");
                startAsyncTask();
                return true;

            case 3:
                GetResponse.setSource("fortune");
                startAsyncTask();
                return true;

            case 4:
                GetResponse.setSource("cnn");
                startAsyncTask();
                return true;
            case 5:
                GetResponse.setSource("google-news");
                startAsyncTask();
                return true;
            case 6:
                GetResponse.setSource("polygon");
                startAsyncTask();
                return true;
            case 7:
                GetResponse.setSource("mtv-news");
                startAsyncTask();
                return true;
            case 8:
                GetResponse.setSource("espn");
                startAsyncTask();
                return true;
            case 9:
                GetResponse.setSource("techcrunch");
                startAsyncTask();
                return true;
            case 10:
                GetResponse.setSource("the-new-york-times");
                startAsyncTask();
                return true;
            case 11:
                GetResponse.setSource("the-times-of-india");
                startAsyncTask();
                return true;
            default:
                return false;

        }
    }


    //Loader Callback methods
    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, Bundle args) {

        progressBar.setVisibility(View.VISIBLE);
        return new NewsDownloadLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> data) {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.swapCursor(getData());
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {

    }

    private void setDataUsingPreferences(){
        changeDefaultSource(preference);
        recyclerView.getRecycledViewPool().clear();
        if(preference.getString("default_view_layouts","card_view").equals("list_view")){
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.default_state));
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)recyclerView.getLayoutParams();
            marginLayoutParams.setMargins(0,0,0,0);
            recyclerView.setLayoutParams(marginLayoutParams);
            adapter.notifyDataSetChanged();
        }
        startAsyncTask();
    }

    private void changeDefaultSource(SharedPreferences preferences){
        GetResponse.setSource(preferences.getString("default_news","cnn"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals("default_view_layouts")){

            Log.e("**********I should ****","Letss asdjla");
            recreate();
        }

    }


    //Check clicks in drawer
    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            onItemClicked(i);
            drawerListView.setItemChecked((int)l,true);
            drawerLayout.closeDrawer(drawerListView);
        }
    }
}
