package com.aboelfer.knightrider.radicalnews.Activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aboelfer.knightrider.radicalnews.Adapter.ArticlesAdapter;
import com.aboelfer.knightrider.radicalnews.Database.BookmarksContract;
import com.aboelfer.knightrider.radicalnews.Model.Article;
import com.aboelfer.knightrider.radicalnews.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksActivity extends AppCompatActivity implements ArticlesAdapter.ListArticleClickListener, LoaderManager.LoaderCallbacks<Cursor>, Loader.OnLoadCompleteListener {

    @BindView(R.id.noBookmarks_tv)
    TextView noBookmarksTV;
    @BindView(R.id.noBookmarks_iv)
    ImageView noBookmarksIV;
    @BindView(R.id.bookmarks_rv)
    RecyclerView bookmarksRV;
    @BindView(R.id.bookmarks_pb)
    ProgressBar bookmarksPB;

    private List<Article> bookmarkedArticleList;
    private ArticlesAdapter bookmarksAdapter;
    boolean POPULATED_UI_SUCCESSFULLY = false;
    private static int LOADER_BOOKMARKS = 123;

    @Override
    protected void onResume() {
        super.onResume();
        if (POPULATED_UI_SUCCESSFULLY){
            getLoaderManager().restartLoader(LOADER_BOOKMARKS, null , this);
            bookmarksAdapter.refreshEvents(bookmarkedArticleList);
            getLoaderManager().initLoader(LOADER_BOOKMARKS, null, this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        ButterKnife.bind(this);

        //Populate the layout according to screen size and orientation
        GridLayoutManager layoutManager = null;
        if (findViewById(R.id.largeScreen) == null){
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
            } else {
                layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
            }
        } else {
            layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        }


        bookmarksRV.setLayoutManager(layoutManager);
        bookmarksRV.setHasFixedSize(true);

        bookmarkedArticleList = new ArrayList<>();

        getLoaderManager().initLoader(LOADER_BOOKMARKS, null, this);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Article article1 = bookmarkedArticleList.get(clickedItemIndex);

        Intent intent = new Intent(this, ArticleActivity.class);

        intent.putExtra(getResources().getString(R.string.KEY_AUTHOR), article1.getAuthor());
        intent.putExtra(getResources().getString(R.string.KEY_TITLE), article1.getTitle());
        intent.putExtra(getResources().getString(R.string.KEY_DESCRIPTION), article1.getDescription());
        intent.putExtra(getResources().getString(R.string.KEY_ARTICLE_URL), article1.getArticleUrl());
        intent.putExtra(getResources().getString(R.string.KEY_IMAGE_URL), article1.getImageUrl());
        intent.putExtra(getResources().getString(R.string.KEY_PUBLISHED_DATE), article1.getPublishingDate());
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        bookmarksPB.setVisibility(View.VISIBLE);
        if (id == LOADER_BOOKMARKS) {
            //Iterating through the database
            return new CursorLoader(this, BookmarksContract.BookmarkedArticlesEntry.CONTENT_URI, null, null, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookmarksPB.setVisibility(View.GONE);
        //Filling the Article object using the cursor
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            Article article = new Article(
                    data.getString(data.getColumnIndex(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_AUTHOR)),
                    data.getString(data.getColumnIndex(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_TITLE)),
                    data.getString(data.getColumnIndex(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_DESCRIPTION)),
                    data.getString(data.getColumnIndex(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_URL)),
                    data.getString(data.getColumnIndex(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_IMAGE)),
                    data.getString(data.getColumnIndex(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_DATE))
            );
            bookmarkedArticleList.add(article);
        }

        //Checking if there is no bookmarks saved by List
        //if there is bookmarks we show the recyclerView and hide other views else we do the opposite
        if (bookmarkedArticleList.isEmpty()){
            noBookmarksTV.setVisibility(View.VISIBLE);
            noBookmarksIV.setVisibility(View.VISIBLE);
            bookmarksRV.setVisibility(View.GONE);

        }else {

            noBookmarksTV.setVisibility(View.GONE);
            noBookmarksIV.setVisibility(View.GONE);
            bookmarksRV.setVisibility(View.VISIBLE);

            bookmarksAdapter = new ArticlesAdapter(bookmarkedArticleList, getApplicationContext(), this);
            bookmarksRV.setAdapter(bookmarksAdapter);

            POPULATED_UI_SUCCESSFULLY = true;
        }

    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }

    @Override
    public void onLoadComplete(Loader loader, Object data) {

    }
}
