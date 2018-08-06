package com.example.android.localnews;

import android.content.Context;
import android.content.AsyncTaskLoader;
import java.util.List;

//loader for a news article list
public class NewsArticleLoader extends AsyncTaskLoader<List<NewsArticle>> {

    //holds the url that will be loaded from the internet
    private String mUrl = "";

    //construct a new loader with a given url
    public NewsArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    //load a new data set
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //start a new thread to load our data from the internet
    @Override
    public List<NewsArticle> loadInBackground() {
        if(mUrl == ""){
            return null;
        }

        List<NewsArticle> result = Utilities.fetchNewsArticleData(mUrl);
        return result;

    }
}