package com.example.qheadlines.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qheadlines.Databases.DatabaseContract;
import com.example.qheadlines.R;


public class NewsHeadlineAdapter extends RecyclerView.Adapter<NewsHeadlineAdapter.NewsHeadlineViewHolder>{

    Cursor cursor;
    Context context;
    SharedPreferences sharedPreferences;
    String sharedPreferenceValue;

    public NewsHeadlineAdapter(Cursor cursor1, Context context, SharedPreferences sharedPreferences){
        cursor = cursor1;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public NewsHeadlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        sharedPreferenceValue = sharedPreferences.getString("default_view_layouts","card_view");
        View view;
        if(sharedPreferenceValue.equals("card_view"))
            view = inflater.inflate(R.layout.card_layout,parent,false);
        else
            view = inflater.inflate(R.layout.list_view,parent,false);

        return new NewsHeadlineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsHeadlineViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.headLineTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsEntry.HEADLINE_COLUMN)));
        holder.additionalInformationTextView.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsEntry.DESCRIPTION_COLUMN)));
        Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsEntry.IMAGE_URL_COLUMN)));
        Glide.with(context).load(uri).into(holder.newsImageView);
        holder.itemView.setTag(position);
        holder.popupMenuButton.setTag(position);
}

    @Override
    public int getItemCount() {
        if(cursor == null){
            return 0;
        }
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(cursor != null) cursor.close();
        cursor = newCursor;

        if(newCursor != null){
            notifyDataSetChanged();
        }
    }

    public class NewsHeadlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        TextView headLineTextView;
        TextView additionalInformationTextView;
        ImageView newsImageView;
        LinearLayout linearLayout;
        ImageButton popupMenuButton;

        public NewsHeadlineViewHolder(View view){
            super(view);

            linearLayout = (LinearLayout) view.findViewById(R.id.inner_linear_layout);

            if(sharedPreferences.equals("card_view")) {
                cardView = (CardView) view.findViewById(R.id.card_view);
                cardView.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
            }
            headLineTextView = (TextView) view.findViewById(R.id.headline_text_view);
            additionalInformationTextView = (TextView) view.findViewById(R.id.additional_information_text_view);
            newsImageView = (ImageView) view.findViewById(R.id.news_image_view);
            popupMenuButton = (ImageButton) view.findViewById(R.id.id_popup_menu_button);
            popupMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu menu = new PopupMenu(view.getContext(),view);
                    menu.setOnMenuItemClickListener(new PopUpMenuListener((int)view.getTag(),view.getContext()));
                    MenuInflater inflater = menu.getMenuInflater();
                    inflater.inflate(R.menu.popup_menu,menu.getMenu());
                    menu.show();
                }
            });
            view.setOnClickListener(this);
        }

        public void onClick(View view){

            int id = (int) view.getTag();
            cursor.moveToPosition(id);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsEntry.URL_COLUMN))));
            view.getContext().startActivity(intent);
        }
    }

    class PopUpMenuListener implements PopupMenu.OnMenuItemClickListener{

        int id;
        Context context;
        public PopUpMenuListener(int id,Context context){
            this.id = id;
            this.context = context;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch(item.getItemId()){
                case R.id.id_popup_menu_save:
                    saveNews();
                    return true;
                case R.id.id_popup_menu_share:
                    return false;
                default:
                    return false;
            }

        }

        private void saveNews(){
            String headLine;
            String description;
            String url;
            String source;
            cursor.moveToPosition(id);
            headLine = cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsEntry.HEADLINE_COLUMN));
            description = cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsEntry.DESCRIPTION_COLUMN));
            url = cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsEntry.URL_COLUMN));
            source = cursor.getString(cursor.getColumnIndex(DatabaseContract.NewsEntry.SOURCE_COLUMN));

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.SavedNewsEntry.HEADLINE_COLUMN,headLine);
            contentValues.put(DatabaseContract.SavedNewsEntry.DESCRIPTION_COLUMN,description);
            contentValues.put(DatabaseContract.SavedNewsEntry.URL_COLUMN,url);
            contentValues.put(DatabaseContract.SavedNewsEntry.DATE_COLUMN, System.currentTimeMillis());
            contentValues.put(DatabaseContract.SavedNewsEntry.SOURCE_COLUMN,source);
            context.getContentResolver().insert(DatabaseContract.SavedNewsEntry.CONTENT_URI,contentValues);
        }
    }
}