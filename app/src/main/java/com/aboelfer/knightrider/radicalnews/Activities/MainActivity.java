package com.aboelfer.knightrider.radicalnews.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.aboelfer.knightrider.radicalnews.Adapter.ArticlesAdapter;
import com.aboelfer.knightrider.radicalnews.Model.Article;
import com.aboelfer.knightrider.radicalnews.R;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.android.volley.Request.Method.GET;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    @BindView(R.id.mainArticles_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.mainArticles_container)
    ViewPager mainViewPager;
    @BindView(R.id.mainArticles_tabs)
    TabLayout mainTabLayout;
    @BindView(R.id.mainArticles_navView)
    NavigationView mainNavigationView;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mainSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    DrawerLayout mainDrawerLayout;
    static boolean DRAWER_OPEN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mainToolbar);

        mainDrawerLayout = findViewById(R.id.mainArticles_drawer);
        mainDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                DRAWER_OPEN = true;
                mainDrawerLayout.requestFocus();
                mainToolbar.clearFocus();
            }
        });
        ActionBarDrawerToggle mainToggle = new ActionBarDrawerToggle(
                this, mainDrawerLayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainDrawerLayout.addDrawerListener(mainToggle);
        mainToggle.syncState();

        mainNavigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mainSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.;
        mainViewPager.setAdapter(mainSectionsPagerAdapter);

        mainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTabLayout));
        mainTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mainViewPager));
        mainTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @Override
    public void onBackPressed() {
        mainDrawerLayout = findViewById(R.id.mainArticles_drawer);
        if (mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerOpened(View drawerView) {
                    DRAWER_OPEN = true;
                    mainDrawerLayout.clearFocus();
                    mainToolbar.setFocusable(true);
                }
            });
            mainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            DRAWER_OPEN = false;
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_refresh_news) {
            recreate();
        } else if (id == R.id.nav_bookmarks) {
            Intent intent = new Intent(this, BookmarksActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        mainDrawerLayout = findViewById(R.id.mainArticles_drawer);
        mainDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                DRAWER_OPEN = true;
                mainDrawerLayout.requestFocus();
                mainToolbar.clearFocus();
            }
        });
        mainDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements ArticlesAdapter.ListArticleClickListener, SharedPreferences.OnSharedPreferenceChangeListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        @BindView(R.id.fragment_rv)
        RecyclerView mainArticlesRV;
        @BindView(R.id.adView)
        AdView mAdView;

        private static final String ARG_SECTION_NUMBER = "section_number";
        private ArticlesAdapter mainArticlesAdapter;
        private List<Article> mainArticlesList;
        private SharedPreferences preferences;
        private String country;

        public PlaceholderFragment() {
        }
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, rootView);

            // Display adds on device
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);

            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            country = preferences.getString(getString(R.string.pref_newsOrigin_key), getResources().getString(R.string.pref_newsOrigin_default) );
            preferences.registerOnSharedPreferenceChangeListener(this);

            mainArticlesRV.setNextFocusUpId(R.id.mainArticles_toolbar);

            //Populate the layout according to screen size and orientation
            GridLayoutManager layoutManager;
            if (rootView.findViewById(R.id.constraintLayout_large) == null){
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
                } else {
                    layoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
                }
            } else {
                    layoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
            }

            mainArticlesRV.setLayoutManager(layoutManager);
            mainArticlesRV.setHasFixedSize(true);
            mainArticlesList = new ArrayList<>();

            PullNewsTask pullNewsTask = new PullNewsTask();
            pullNewsTask.execute();

            if (!DRAWER_OPEN){
                mainArticlesRV.clearFocus();
            }

            return rootView;
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
                        mainArticlesList.add(article);
                    }
                    mainArticlesAdapter = new ArticlesAdapter(mainArticlesList, getContext(), this);
                    mainArticlesRV.setAdapter(mainArticlesAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error ->
                {
                    Snackbar snackbar = Snackbar
                            .make(getView(), getActivity().getString(R.string.no_connectivity_error), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getActivity().getString(R.string.retry_to_connect), view -> {

                                loadNewsData(url);
                            });
                    // Changing message text color
                    snackbar.setActionTextColor(Color.RED);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);

                    snackbar.show();
                });
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);

        }

        @Override
        public void onListItemClick(int clickedItemIndex) {
            Article article1 = mainArticlesList.get(clickedItemIndex);

            Intent intent = new Intent(getContext(), ArticleActivity.class);

            intent.putExtra(getResources().getString(R.string.KEY_AUTHOR), article1.getAuthor());
            intent.putExtra(getResources().getString(R.string.KEY_TITLE), article1.getTitle());
            intent.putExtra(getResources().getString(R.string.KEY_DESCRIPTION), article1.getDescription());
            intent.putExtra(getResources().getString(R.string.KEY_ARTICLE_URL), article1.getArticleUrl());
            intent.putExtra(getResources().getString(R.string.KEY_IMAGE_URL), article1.getImageUrl());
            intent.putExtra(getResources().getString(R.string.KEY_PUBLISHED_DATE), article1.getPublishingDate());
            startActivity(intent);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_newsOrigin_key))){
                country = preferences.getString(getString(R.string.pref_newsOrigin_key), getResources().getString(R.string.pref_newsOrigin_default) );
            }
        }

        public class PullNewsTask extends AsyncTask<Void, Void, Void>{
            ProgressBar mainArticlesPB = getActivity().findViewById(R.id.mainArticles_pb);



            @Override
            protected Void doInBackground(Void... voids) {
                switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                    case 1: loadNewsData(getResources().getString(R.string.URL_Top_Headlines_1st_part) + country + getResources().getString(R.string.URL_Top_Headlines_2nd_part));
                        break;
                    case 2: loadNewsData(getResources().getString(R.string.URL_Entertainment_1st_part) + country + getResources().getString(R.string.URL_Entertainment_2nd_part));
                        break;
                    case 3: loadNewsData(getResources().getString(R.string.URL_Technology_1st_part) + country + getResources().getString(R.string.URL_Technology_2nd_part));
                        break;
                    case 4: loadNewsData(getResources().getString(R.string.URL_Health_1st_part) + country + getResources().getString(R.string.URL_Health_2nd_part));
                        break;
                    case 5: loadNewsData(getResources().getString(R.string.URL_Science_1st_part) + country + getResources().getString(R.string.URL_Science_2nd_part));
                        break;
                    case 6: loadNewsData(getResources().getString(R.string.URL_Sports_1st_part) + country + getResources().getString(R.string.URL_Sports_2nd_part));
                        break;
                    case 7: loadNewsData(getResources().getString(R.string.URL_Business_1st_part) + country + getResources().getString(R.string.URL_Business_2nd_part));
                        break;
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mainArticlesPB.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mainArticlesPB.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 7 total pages.
            return 7;
        }
    }


}
