package com.example.android.news;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Eugen on 19-Jan-18.
 */

public abstract class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    private int maxVisibleItemCount = 0;
    private boolean onLoading = false;
    private int NEWS_PER_PAGE = 5;
    RecyclerView.LayoutManager mLayoutManager;
    Context mContext;

    public RecyclerViewOnScrollListener(LinearLayoutManager layoutManager, Context context) {
        this.mLayoutManager = layoutManager;
        mContext = context;
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int firstVisibleItem, int visibleItemCount) {
        int lastVisibleItemPosition;
        int totalItemCount = mLayoutManager.getItemCount();
        lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        if (maxVisibleItemCount < lastVisibleItemPosition) {
            maxVisibleItemCount = lastVisibleItemPosition;
            if (lastVisibleItemPosition == totalItemCount - 1)
                onLoading = true;
        } else onLoading = false;
        if (onLoading) {
            //load next page
            onLoadMore(totalItemCount / NEWS_PER_PAGE + 1);
            Log.v("Scroll", "Load more................");
        }
    }

    // Setting the page for loader
    public abstract void onLoadMore(int page);

}
