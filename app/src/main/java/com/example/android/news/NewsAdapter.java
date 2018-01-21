package com.example.android.news;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Eugen on 16-Jan-18.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;
    private OnItemClicked onClick;

    public List<News> mNewsList;

    public NewsAdapter(Activity context, List<News> news) {
        mContext = context;
        mNewsList = news;
    }

    public void addAll(List<News> news){
        mNewsList.addAll(news);
    }

    //format the date
    private String formatDate(String dateObject) {
        Date date1 = new Date();
        try {
            date1 = new SimpleDateFormat("LLL dd, yyyy").parse(dateObject);
        } catch (ParseException e) {

        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yyyy");
        return dateFormat.format(date1).toString();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        News currentNews = mNewsList.get(position);
        holder.headlineTextView.setText(currentNews.getHeadline());
        holder.trailTextView.setText(currentNews.getHeadline());
        holder.dateTextView.setText(formatDate(currentNews.getDate()));
        //Setting the cardView onClickListener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });

        // loading thumbnail using Glide library
        Glide.with(mContext).load(currentNews.getThumbnailUrl()).into(holder.thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    // declare interface for onItemClick
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    //setting the clicked item on click
    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView headlineTextView;
        public TextView trailTextView;
        public ImageView thumbnailImageView;
        public TextView dateTextView;
        public View cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            headlineTextView = itemView.findViewById(R.id.list_item_headline);
            trailTextView = itemView.findViewById(R.id.list_item_trail_text);
            thumbnailImageView = itemView.findViewById(R.id.list_item_thumbnail);
            dateTextView = itemView.findViewById(R.id.list_item_date);
            cardView = itemView.findViewById(R.id.news_item);
        }

    }
}