package com.example.android.news;

/**
 * Created by Eugen on 16-Jan-18.
 */

public class News {
    private String mHeadline;
    private String mUrl;
    private String mDate;
    private String mCategory;
    private String mTrailText;
    private String mThumbnailUrl;

    public News(String headline,String url, String date,String category, String trailText, String thumbnailUrl){
        mHeadline = headline;
        mUrl = url;
        mDate = date;
        mCategory = category;
        mTrailText = trailText;
        mThumbnailUrl = thumbnailUrl;
    }

    public String getHeadline(){
        return mHeadline;
    }
    public String getTrailText() { return mTrailText;}
    public String getUrl(){
        return mUrl;
    }
    public String getDate(){
        return mDate;
    }
    public String getCategory(){
        return mCategory;
    }
    public String getThumbnailUrl(){ return mThumbnailUrl;}
}
