package com.aboelfer.knightrider.radicalnews.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.aboelfer.knightrider.radicalnews.Adapter.ArticlesAdapter;
import com.aboelfer.knightrider.radicalnews.Model.Article;
import com.aboelfer.knightrider.radicalnews.R;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import static com.android.volley.Request.Method.GET;

public class SearchActivity extends AppCompatActivity implements ArticlesAdapter.ListArticleClickListener{

    @BindView(R.id.search_cl)
    CoordinatorLayout searchCL;
    @BindView(R.id.search_rv)
    RecyclerView searchRV;

    private boolean POPULATED_UI_SUCCESSFULLY = false;
    private static String searchUrl;
    private ArticlesAdapter searchAdapter;
    private List<Article> searchedArticleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchRV = findViewById(R.id.search_rv);

        GridLayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        }

        searchRV.setLayoutManager(layoutManager);
        searchRV.setHasFixedSize(true);
        searchedArticleList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view_menu_item, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewItem.expandActionView();
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (POPULATED_UI_SUCCESSFULLY){searchAdapter.refreshEvents(searchedArticleList);}

                searchViewAndroidActionBar.clearFocus();
                searchUrl = getResources().getString(R.string.URL_Search_Every_article_1st_part) + query + getResources().getString(R.string.URL_Search_Every_article_2nd_part);
                SearchTask searchTask = new SearchTask();
                searchTask.execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchViewAndroidActionBar.setOnQueryTextFocusChangeListener((v, newViewFocus) -> {
            if (!newViewFocus)
            {
                //Collapse the action item.
                searchViewItem.collapseActionView();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void loadNewsData(String url) {

        StringRequest stringRequest = new StringRequest(GET, url, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.optJSONArray(getResources().getString(R.string.KEY_ARTICLES));
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jo = results.optJSONObject(i);
                    Article article = new Article(
                            jo.optString(getResources().getString(R.string.KEY_AUTHOR)),
                            jo.optString(getResources().getString(R.string.KEY_TITLE)),
                            jo.optString(getResources().getString(R.string.KEY_DESCRIPTION)),
                            jo.optString(getResources().getString(R.string.KEY_ARTICLE_URL)),
                            jo.optString(getResources().getString(R.string.KEY_IMAGE_URL)),
                            jo.optString(getResources().getString(R.string.KEY_PUBLISHED_DATE))
                    );
                    searchedArticleList.add(article);
                }
                searchAdapter = new ArticlesAdapter(searchedArticleList, this, this);
                searchRV.setAdapter(searchAdapter);
                POPULATED_UI_SUCCESSFULLY = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error ->
        {
            Snackbar snackbar = Snackbar
                    .make(searchCL, getString(R.string.no_connectivity_error), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry_to_connect), view -> {

                        loadNewsData(url);
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
            POPULATED_UI_SUCCESSFULLY = false;
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Article article1 = searchedArticleList.get(clickedItemIndex);


        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(getResources().getString(R.string.KEY_ARTICLE_URL), article1.getArticleUrl());
        startActivity(intent);
    }

    public class SearchTask extends AsyncTask<Void, Void, Void> {
        ProgressBar searchPB = findViewById(R.id.search_pb);



        @Override
        protected Void doInBackground(Void... voids) {
            loadNewsData(searchUrl);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            POPULATED_UI_SUCCESSFULLY = false;
            searchPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            searchPB.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

