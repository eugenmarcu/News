package com.example.android.news;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Eugen on 16-Jan-18.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Activity context, List<News> news) {
        super(context, 0, news);
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        News currentNews = getItem(position);

        //Set the headline
        String title = currentNews.getHeadline();
        TextView titleTextView = listItemView.findViewById(R.id.list_item_headline);
        titleTextView.setText(title);

        //Set the textTrails
        String textTrails = currentNews.getTrailText();
        TextView textTrailsView = listItemView.findViewById(R.id.list_item_trail_text);
        textTrailsView.setText(textTrails);

        //Set the thumbnail
        String url = currentNews.getThumbnailUrl();
        ImageView thumbnailView = listItemView.findViewById(R.id.list_item_thumbnail);
        final AsyncTask<String, Void, Bitmap>  execute = new DownloadImageTask(thumbnailView)
                .execute(url);


        //Set the date
        TextView newsDateTextView = listItemView.findViewById(R.id.list_item_date);

        //Date date = toDate(currentNews.getDate());
        String dateToDisplay = formatDate(currentNews.getDate());
        newsDateTextView.setText(dateToDisplay);

        return listItemView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}