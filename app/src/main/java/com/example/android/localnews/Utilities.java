package com.example.android.localnews;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//utility class providing several helper methods
public class Utilities {

    private static final int readTimeout = 10000;
    private static final int connectTimeout = 15000;
    private static final int successResponse = 200;

    private Utilities() {
    }

    //parse a JSON string into a news article list
    private static List<NewsArticle> extractFeatureFromJson(String newsArticleJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsArticleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news articles to
        List<NewsArticle> newsArticles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsArticleJSON);

            JSONObject resultsJsonObject = baseJsonResponse.getJSONObject("response");

            //Extract the JSONArray associated with the key called "results",
            //which represents a list of articles
            JSONArray newsArticleArray = resultsJsonObject.getJSONArray("results");

            //For each article in the article array, create an NewsArticle object
            for (int i = 0; i < newsArticleArray.length(); i++) {

                //Get a single article at position i within the list of articles
                JSONObject currentNewsArticle = newsArticleArray.getJSONObject(i);

                //Extract the value for the key called "webTitle"
                String title = currentNewsArticle.optString("webTitle");

                //Extract the value for the key called "sectionName"
                String section = currentNewsArticle.optString("sectionName");

                //Extract the value for the key called "webUrl"
                String url = currentNewsArticle.optString("webUrl");

                //Extract the tags array from the current news article and then get
                //the author name from the key call "webTitle"
                String author;

                if (currentNewsArticle.has("tags")) {
                    JSONArray tagsArray = currentNewsArticle.getJSONArray("tags");
                    JSONObject tagsObject = tagsArray.getJSONObject(0);
                    author = tagsObject.optString("webTitle");
                } else {
                    author = "No Author Provided";
                }

                Date parsedDate = formatDateString(currentNewsArticle.optString("webPublicationDate"));

                // Create a new {@link NewsArticle} object with the title, author, section,
                // url, and date from the JSON response.
                NewsArticle newsArticle = new NewsArticle(title, author, section, url, parsedDate);

                // Add the new article to the list of news articles.
                newsArticles.add(newsArticle);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Utilities: ", "Problem parsing the newsArticle JSON results", e);
        }

        // Return the list of newsArticles
        return newsArticles;
    }

    //create a new URL object from a given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Utilities: ", "Problem building the URL ", e);
        }
        return url;
    }


    //Make a HTTP request to a given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(readTimeout /* milliseconds */);
            urlConnection.setConnectTimeout(connectTimeout /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == successResponse) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("Utilities: ", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Utilities: ", "Problem retrieving the newsArticle JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    //Convert an InputString into a String which contains the
    //whole JSON response from the server.
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    //Query the Guardian dataset and return a list of news article objects.
    public static List<NewsArticle> fetchNewsArticleData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Utilities: ", "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of news articles
        List<NewsArticle> newsArticles = extractFeatureFromJson(jsonResponse);

        // Return the list of news articles
        return newsArticles;
    }

    //convert a string date into a Date object
    private static Date formatDateString(String stringDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        Date date;

        try {
            date = dateFormat.parse(stringDate.replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            date = null;
            Log.e("Utilities: ", "Problem formatting the date.", e);
        }

        return date;
    }

    //Return a formatted date string from a Date object.
    public static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

}
