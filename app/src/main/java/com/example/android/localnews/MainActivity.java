package com.example.android.localnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsArticle>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //our news article request URL
    private static final String REQUEST_URL = "https://content.guardianapis.com/search";

    //the ID of our loader
    private static final int LOADER_ID = 1;

    //our news article adapter
    private NewsArticleAdapter mAdapter;

    //TextView to display if our list is empty
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyStateTextView = (TextView) findViewById(R.id.empty_list);

        // Find a reference to the ListView in the layout
        ListView newsArticleListView = (ListView) findViewById(R.id.list);
        newsArticleListView.setEmptyView(emptyStateTextView);

        //check for an internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Create a new adapter that takes an empty list of news articles as input
        mAdapter = new NewsArticleAdapter(this, new ArrayList<NewsArticle>());

        // Set the adapter on the ListView
        // so the list can be populated in the user interface
        newsArticleListView.setAdapter(mAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected newsarticle.
        newsArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current newsarticle that was clicked on
                NewsArticle currentNewsArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsarticleUri = Uri.parse(currentNewsArticle.getUrl());

                // Create a new intent to view the news article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsarticleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //only try to load content if there is an internet connection
        // otherwise, warn the user there is no connection
        if (isConnected) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
            progressBar.setVisibility(View.GONE);

            TextView emptyView = (TextView) findViewById(R.id.empty_list);
            emptyView.setText(R.string.no_connection);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_search_term_key))) {
            //clear our ListView as a new query will be started
            mAdapter.clear();

            //hide our empty state text view because a loading indicator will be displayed
            emptyStateTextView.setVisibility(View.GONE);

            //show the loading indicator while we fetch new data
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.VISIBLE);

            //restart the loader to requery the server since the query settings have been changed
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    //no loader exists so create a new one
    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int i, Bundle bundle) {

        //get the default preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String searchTerm = sharedPrefs.getString(
                getString(R.string.settings_search_term_key),
                getString(R.string.settings_search_term_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        //parse our base URI string
        Uri baseUri = Uri.parse(REQUEST_URL);

        //prepare our baseURI so that we can add parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //grab our API key
        String apiKey = BuildConfig.THE_GUARDIAN_API_KEY;

        //append our query parameters to our baseURI
        uriBuilder.appendQueryParameter("q", searchTerm);
        uriBuilder.appendQueryParameter("api-key", apiKey);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", "50");
        uriBuilder.appendQueryParameter("order-by", orderBy);

        return new NewsArticleLoader(MainActivity.this, uriBuilder.toString());
    }

    //loader has finished fetching content so go ahead and update the UI with the news articles
    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> newsArticles) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.GONE);

        TextView emptyView = (TextView) findViewById(R.id.empty_list);
        emptyView.setText(R.string.no_content);

        // Clear the adapter of previous news article data
        mAdapter.clear();

        // If there is a valid list of ews articles, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsArticles != null && !newsArticles.isEmpty()) {
            mAdapter.addAll(newsArticles);
        }

    }

    //reset the loader
    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        mAdapter.clear();
    }

    @Override
    //initialize the contents of our activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate our options menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    //check the id of the selected menu item and start the appropriate activity
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
