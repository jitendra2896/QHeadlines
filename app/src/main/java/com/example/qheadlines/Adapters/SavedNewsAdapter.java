package com.example.qheadlines.Adapters;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.qheadlines.Databases.DatabaseContract;
import com.example.qheadlines.DateAndTime;
import com.example.qheadlines.R;

public class SavedNewsAdapter extends RecyclerView.Adapter<SavedNewsAdapter.SavedNewsViewHolder>{

    Cursor cursor;

    public SavedNewsAdapter(Cursor cursor){
        this.cursor = cursor;
    }

    @Override
    public SavedNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.saved_news_layout,parent,false);

        return new SavedNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SavedNewsViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.savedNewsHeadline.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.SavedNewsEntry.HEADLINE_COLUMN)));
        holder.savedNewsAdditional.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.SavedNewsEntry.DESCRIPTION_COLUMN)));
        holder.itemView.setTag(position);
        holder.savedNewsDate.setText(DateAndTime.getDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SavedNewsEntry.DATE_COLUMN))));
        holder.savedNewsSource.setText("SOURCE: "+cursor.getString(cursor.getColumnIndex(DatabaseContract.SavedNewsEntry.SOURCE_COLUMN)));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        if(this.cursor != null) this.cursor.close();
        this.cursor = cursor;
        if(cursor != null)
            notifyDataSetChanged();
    }

    class SavedNewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView savedNewsHeadline;
        TextView savedNewsAdditional;
        TextView savedNewsDate;
        TextView savedNewsSource;

        public SavedNewsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            savedNewsHeadline = (TextView) itemView.findViewById(R.id.id_saved_news_headline_text_view);
            savedNewsAdditional = (TextView) itemView.findViewById(R.id.id_saved_news_additional_description_text_view);
            savedNewsDate = (TextView) itemView.findViewById(R.id.id_saved_news_date_text_view);
            savedNewsSource = (TextView) itemView.findViewById(R.id.id_saved_news_source_text_view);
        }

        @Override
        public void onClick(View view) {
            int pos = (int)view.getTag();
            cursor.moveToPosition(pos);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseContract.SavedNewsEntry.URL_COLUMN))));
            view.getContext().startActivity(intent);
        }
    }
}
