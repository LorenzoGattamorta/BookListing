package com.example.android.booklisting;

/**
 * Created by Lorenzo on 26/06/17.
 */

public class Book {

    //Create a variable for the writer of the book
    private String mWriter;

    //Create a variable for the title of the book
    private String mTitle;

    private String mUrl;

    public Book(String writer, String title, String url) {
        mWriter = writer;
        mTitle = title;
        mUrl = url;
    }

    //Return writer
    public String getWriter() {
        return mWriter;
    }

    //Return title
    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }
}
