package com.example.android.localnews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

//Adapter view for a news article object
public class NewsArticleAdapter extends ArrayAdapter<NewsArticle> {

    //constructor for creating a new NewsArticleAdapter
    public NewsArticleAdapter(Activity context, ArrayList<NewsArticle> articles) {
        super(context, 0, articles);
    }

    //provide a View for an AdaptorView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        //get the current article in our list
        NewsArticle currentArticle = getItem(position);

        //find the current article title view in our layout and set its value
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentArticle.getTitle());

        //find the current article author view in our layout and set its value
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        authorView.setText(currentArticle.getAuthor());

        //find the current article date view in our layout and set its value
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        dateView.setText(Utilities.formatDate(currentArticle.getDate()));

        //find the current article section view in our layout and set its value
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        sectionView.setText(currentArticle.getSection());

        return listItemView;
    }
}
