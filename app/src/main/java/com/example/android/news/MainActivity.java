package com.example.android.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, NewsAdapter.OnItemClicked {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String REQUEST_URL = "http://content.guardianapis.com/search?show-fields=thumbnail,trailText&api-key=test";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter mAdapter;
    private List<News> mNewsList;
    private TextView mEmptyStateTextView;
    private String mCategory;
    private RecyclerViewOnScrollListener mOnScrollListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoaderManager mLoaderManager;
    private int currentLoadPage = 1;
    private boolean onLoadingMore = false;

    private boolean isInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_movies:
                    mCategory = getString(R.string.title_movies);
                    loaderManager();
                    return true;
                case R.id.navigation_sport:
                    mCategory = getString(R.string.title_sport);
                    loaderManager();
                    return true;
                case R.id.navigation_education:
                    mCategory = getString(R.string.title_education);
                    loaderManager();
                    return true;
                case R.id.navigation_technology:
                    mCategory = getString(R.string.title_technology);
                    loaderManager();
                    return true;
                case R.id.navigation_economy:
                    mCategory = getString(R.string.title_economy);
                    loaderManager();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        final RecyclerView newsListView = findViewById(R.id.list);
        mNewsList = new ArrayList<News>();
        mAdapter = new NewsAdapter(this, mNewsList);
        newsListView.setLayoutManager(new LinearLayoutManager(this));
        //setting the on click
        mAdapter.setOnClick(this);


        //Set the initial category
        mCategory = getString(R.string.title_movies);

        //Check internet connection
        if (!isInternetConnection()) {
            mEmptyStateTextView.setText("No internet connection.");
            // Hide progress bar
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Get a reference to the LoaderManager, in order to interact with loaders.
        mLoaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        mLoaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    private void loaderManager() {
        //initialize the loading page
        currentLoadPage = 1;
        //reset on loading
        onLoadingMore = false;
        //destroy existing loader
        mLoaderManager.destroyLoader(NEWS_LOADER_ID);
        // Show progress bar
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        //LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        mLoaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(getString(R.string.category_key), mCategory);
        uriBuilder.appendQueryParameter("orderBy", "newest");
        uriBuilder.appendQueryParameter("page-size","5");
        uriBuilder.appendQueryParameter("page", String.valueOf(currentLoadPage));

        Log.v(LOG_TAG, uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        //mNewsList = news;
        mAdapter.addAll(news);
        mAdapter.notifyDataSetChanged();
        if (mAdapter.mNewsList.size() == 0) {
            mEmptyStateTextView.setText("No news found.");
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
            if(!onLoadingMore)
                updateUi();
        }


        // Hide progress bar
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        //close swipe refreshing
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.mNewsList.clear();
    }

    private void updateUi() {
        // Find a reference to the {@link ListView} in the layout
        final RecyclerView newsListView = findViewById(R.id.list);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        //Adding the on scroll listener
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        newsListView.setLayoutManager(linearLayoutManager);
        mOnScrollListener = new RecyclerViewOnScrollListener(linearLayoutManager, this) {
            @Override
            public void onLoadMore(int page) {
                //On loading more news modify current load page and reset loader
                currentLoadPage = page;
                onLoadingMore = true;
                mLoaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
            }
        };

        newsListView.addOnScrollListener(mOnScrollListener);

        //on swipe to refresh
        mSwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoaderManager.restartLoader(NEWS_LOADER_ID,null, MainActivity.this);
            }
        });
    }

    //On news click open browser
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        News news = mNewsList.get(position);
        intent.setData(Uri.parse(news.getUrl()));
        startActivity(intent);
    }
}
