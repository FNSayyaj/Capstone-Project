package com.aboelfer.knightrider.radicalnews.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.aboelfer.knightrider.radicalnews.Database.BookmarksContract;
import com.aboelfer.knightrider.radicalnews.R;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @BindView(R.id.article_wv)
    WebView articleWV;
    @BindView(R.id.article_pb)
    ProgressBar articlePB;
    @BindView(R.id.article_cl)
    CoordinatorLayout articleCL;

    private String articleAuthor, articleTitle, articleDescription, articleURL, articleImage, articleDate;
    private List<String> articleUrlList = new ArrayList<>();
    private SharedPreferences preferences;
    private int fontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        fontSize = Integer.valueOf(preferences.getString(getString(R.string.pref_articleFontSize_key), getResources().getString(R.string.pref_articleFontSize_default)));
        preferences.registerOnSharedPreferenceChangeListener(this);

        Intent intent = getIntent();
        if (intent != null){
            articleAuthor = intent.getStringExtra(getResources().getString(R.string.KEY_AUTHOR));
            articleTitle = intent.getStringExtra(getResources().getString(R.string.KEY_TITLE));
            articleDescription = intent.getStringExtra(getResources().getString(R.string.KEY_DESCRIPTION));
            articleURL = intent.getStringExtra(getResources().getString(R.string.KEY_ARTICLE_URL));
            articleImage = intent.getStringExtra(getResources().getString(R.string.KEY_IMAGE_URL));
            articleDate = intent.getStringExtra(getResources().getString(R.string.KEY_PUBLISHED_DATE));
            initWebView();
            articleWV.loadUrl(articleURL);
        }
    }

    private void initWebView() {
        articleWV.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                articlePB.setVisibility(View.VISIBLE);
                articleURL = url;
                invalidateOptionsMenu();
            }

            //force the use of webView and without asking the user to open a browser
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                articleWV.loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    articleWV.loadUrl(request.getUrl().toString());
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                articlePB.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                articlePB.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        });

        articleWV.getSettings().setLoadWithOverviewMode(true);
        articleWV.getSettings().setUseWideViewPort(true);
        articleWV.clearCache(true);
        articleWV.clearHistory();
        articleWV.getSettings().setJavaScriptEnabled(true);
        articleWV.setHorizontalScrollBarEnabled(true);
        articleWV.getSettings().setTextZoom(fontSize);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_bookmark:
                if (!CheckIfArticleIsBookmarked()) {
                    AddArticleToBookmarks();
                } else {
                    DeleteArticleFromBookmarks();
                }

                break;
            case R.id.action_share:
                ShareCompat.IntentBuilder
                        .from(this)
                        .setType("text/plain")
                        .setChooserTitle("Share article via:")
                        .setText("Hey, I recommend reading this article: " + articleURL)
                        .startChooser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void AddArticleToBookmarks() {

        ContentValues cv = new ContentValues();
        cv.put(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_AUTHOR, articleAuthor);
        cv.put(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_TITLE, articleTitle);
        cv.put(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_DESCRIPTION, articleDescription);
        cv.put(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_URL, articleURL);
        cv.put(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_IMAGE, articleImage);
        cv.put(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_DATE, articleDate);
        getContentResolver().insert(BookmarksContract.BookmarkedArticlesEntry.CONTENT_URI, cv);
    }

    private boolean CheckIfArticleIsBookmarked() {

        Cursor cursor = getContentResolver().query(BookmarksContract.BookmarkedArticlesEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                null
        );
        assert cursor != null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String url = cursor.getString(cursor.getColumnIndex(BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_URL));
            articleUrlList.add(url);
        }
        if (cursor.moveToFirst()) {
            if (articleUrlList.contains(articleURL)) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    private void DeleteArticleFromBookmarks() {

        String selection = BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_URL + "=?";
        String[] selectionArgs = {String.valueOf(articleURL)};
        getContentResolver().delete(BookmarksContract.BookmarkedArticlesEntry.CONTENT_URI, selection, selectionArgs);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_articleFontSize_key))){
            fontSize = Integer.valueOf(preferences.getString(getString(R.string.pref_articleFontSize_key), getResources().getString(R.string.pref_articleFontSize_default)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

    }
}
