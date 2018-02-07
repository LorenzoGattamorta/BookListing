package com.example.android.booklisting;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int BOOK_LOADER_ID = 1;

    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private ListView bookListView;

    private BookAdapter mAdapter;

    private SearchView search;

    private String mQuery;

    private TextView mEmptyStateTextView;

    private ProgressBar mProgressBar;

    public static final String LOG_TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookListView = (ListView) findViewById(R.id.list);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        search = (SearchView) findViewById(R.id.search);

        bookListView.setAdapter(mAdapter);


        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Book currentBook = mAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(webIntent);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        if (isConnected()) {
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.internet_off);
        }

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isConnected()) {
                    bookListView.setVisibility(View.INVISIBLE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mQuery = search.getQuery().toString();
                    mQuery = mQuery.replace(" ", "+");
                    Log.v(LOG_TAG, mQuery);
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                    search.clearFocus();
                } else {
                    bookListView.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.internet_off);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        String requestUrl = "";
        if (mQuery != null && mQuery != "") {
            requestUrl = BOOK_REQUEST_URL + mQuery;
        } else {
            String defaultQuery = "android";
            requestUrl = BOOK_REQUEST_URL + defaultQuery;
        }
        return new BookLoader(this, requestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        Log.d(LOG_TAG, String.valueOf(books.get(0)));
        mEmptyStateTextView.setText(R.string.no_book);
        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

}
