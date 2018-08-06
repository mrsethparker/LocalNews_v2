package com.example.android.localnews;

import java.util.Date;

//contains a news article and basic details of the article
public class NewsArticle {

    //the title of an article
    String mTitle;

    //the author of an article
    String mAuthor;

    //the section name of an article
    String mSection;

    //the URL of an article
    String mUrl;

    //the publication date of an article
    Date mDate;


    //construct a new news article object
    public NewsArticle(String title, String author, String section, String url, Date date) {
        this.mTitle = title;
        this.mAuthor = author;
        this.mSection = section;
        this.mUrl = url;
        this.mDate = date;
    }

    //get the title of the article
    public String getTitle() {
        return mTitle;
    }

    //get the author of the article
    public String getAuthor() {
        return mAuthor;
    }

    //get the section name of the article
    public String getSection() {
        return mSection;
    }

    //get the url of the article
    public String getUrl() {
        return mUrl;
    }

    //get the publication date of the article
    public Date getDate() {
        return mDate;
    }

}
