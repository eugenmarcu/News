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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String REQUEST_URL = "http://content.guardianapis.com/search?show-fields=thumbnail,trailText&api-key=test";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private String mCategory;

    private boolean isInternetConnection(){
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
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
                    mCategory = getString(R.string.title_sport);;
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

        mEmptyStateTextView =  findViewById(R.id.empty_view);
        ListView newsListView = findViewById(R.id.list);
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setEmptyView(mEmptyStateTextView);
        //Set the initial category
        mCategory = getString(R.string.title_movies);

        //Check internet connection
        if (!isInternetConnection()){
            mEmptyStateTextView.setText("No internet connection.");
            // Hide progress bar
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    private void loaderManager(){
        //destroy existing loader
        getLoaderManager().destroyLoader(NEWS_LOADER_ID);
        // Show progress bar
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(getString(R.string.category_key), mCategory);
        uriBuilder.appendQueryParameter("orderBy", "newest");

        Log.v(LOG_TAG, uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Clear the adapter of previous news data
        if (mAdapter != null) mAdapter.clear();

        // If there is a valid list of {@link News, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
            updateUi();
        }
        else
            mEmptyStateTextView.setText("No news found.");

        // Hide progress bar
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    private void updateUi(){
        // Find a reference to the {@link ListView} in the layout
        final ListView newsListView = (ListView) findViewById(R.id.list);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                News news = (News) newsListView.getItemAtPosition(position);
                intent.setData(Uri.parse(news.getUrl()));
                startActivity(intent);
            }
        });
    }
}
