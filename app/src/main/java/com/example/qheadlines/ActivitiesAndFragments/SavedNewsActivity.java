package com.example.qheadlines.ActivitiesAndFragments;

import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.qheadlines.Databases.DatabaseContract;
import com.example.qheadlines.R;
import com.example.qheadlines.Adapters.SavedNewsAdapter;

public class SavedNewsActivity extends AppCompatActivity {

    ActionBar actionBar;
    SavedNewsAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        adapter = new SavedNewsAdapter(getDataFromDatabase());
        recyclerView = (RecyclerView) findViewById(R.id.id_saved_news_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.saved_news_dummy_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        if(id == R.id.id_delete_saved_news){
            deleteData();
            adapter.swapCursor(getDataFromDatabase());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteData() {
        getContentResolver().delete(DatabaseContract.SavedNewsEntry.CONTENT_URI,null,null);
    }

    private Cursor getDataFromDatabase(){
        Cursor cursor = getContentResolver().query(DatabaseContract.SavedNewsEntry.CONTENT_URI,null,null,null,DatabaseContract.SavedNewsEntry.DATE_COLUMN+" DESC");
        return cursor;
    }
}
