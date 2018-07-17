package com.aboelfer.knightrider.radicalnews.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import com.aboelfer.knightrider.radicalnews.Activities.MainActivity;
import com.aboelfer.knightrider.radicalnews.Model.Article;
import com.aboelfer.knightrider.radicalnews.R;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static com.android.volley.Request.Method.GET;

/**
 * Implementation of App Widget functionality.
 */
public class NewsWidgetProvider extends AppWidgetProvider implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = NewsWidgetProvider.class.getSimpleName();
    private String name1TV, name2TV, name3TV, title1TV, title2TV, title3TV, time1TV, time2TV, time3TV, country;
    private List<Article> articles;
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent widgetIntent) {
        final String action = widgetIntent.getAction();

        Log.d(TAG, context.getResources().getString(R.string.received_widget_action) + action);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(this);
        super.onReceive(context, widgetIntent);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, context.getResources().getString(R.string.updating_widget));

        for (final int appWidgetId : appWidgetIds) {

            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.news_widget_provider);

            country = preferences.getString(context.getString(R.string.pref_newsOrigin_key),
                    context.getString(R.string.pref_newsOrigin_default));
            StringRequest stringRequest = new StringRequest(GET,
                    context.getString(R.string.URL_Top_Headlines_1st_part) +
                    country +
                    context.getString(R.string.URL_Top_Headlines_2nd_part), response -> {

                articles = new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.optJSONArray(context.getString(R.string.KEY_ARTICLES));
                    for (int k = 0; k < results.length(); k++) {
                        JSONObject jo = results.optJSONObject(k);
                        Article article = new Article(
                                jo.optString(context.getString(R.string.KEY_AUTHOR)),
                                jo.optString(context.getString(R.string.KEY_TITLE)),
                                jo.optString(context.getString(R.string.KEY_DESCRIPTION)),
                                jo.optString(context.getString(R.string.KEY_ARTICLE_URL)),
                                jo.optString(context.getString(R.string.KEY_IMAGE_URL)),
                                jo.optString(context.getString(R.string.KEY_PUBLISHED_DATE))
                        );
                        articles.add(article);
                    }
                    name1TV = articles.get(0).getAuthor();
                    name2TV = articles.get(1).getAuthor();
                    name3TV = articles.get(2).getAuthor();
                    title1TV = articles.get(0).getTitle();
                    title2TV = articles.get(1).getTitle();
                    title3TV = articles.get(2).getTitle();
                    time1TV = articles.get(0).getPublishingDate();
                    time2TV = articles.get(1).getPublishingDate();
                    time3TV = articles.get(2).getPublishingDate();


                    Intent intent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                    views.setTextViewText(R.id.authorArticle1_tv, checkIfNull(name1TV, context));
                    views.setTextViewText(R.id.authorArticle2_tv, checkIfNull(name2TV, context));
                    views.setTextViewText(R.id.authorArticle3_tv, checkIfNull(name3TV, context));

                    views.setTextViewText(R.id.titleArticle1_tv, checkIfNull(title1TV, context));
                    views.setTextViewText(R.id.titleArticle2_tv, checkIfNull(title2TV, context));
                    views.setTextViewText(R.id.titleArticle3_tv, checkIfNull(title3TV, context));

                    views.setTextViewText(R.id.timeArticle1_tv, GetIntervalTime(checkIfNull(time1TV, context), context));
                    views.setTextViewText(R.id.timeArticle2_tv, GetIntervalTime(checkIfNull(time2TV, context), context));
                    views.setTextViewText(R.id.timeArticle3_tv, GetIntervalTime(checkIfNull(time3TV, context), context));

                    views.setOnClickPendingIntent(R.id.openApp_btn, pendingIntent);

                    appWidgetManager.updateAppWidget(appWidgetId, views);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Log.e(TAG, String.valueOf(getResultCode())));

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }
}


    private String checkIfNull(String data, Context context) {
        if (data.equals(context.getResources().getString(R.string.check_null_string))) {
            return  context.getResources().getString(R.string.set_null_default_value);
        } else {
            return data;
        }
    }

    private String GetIntervalTime(String dateInString, Context context){

        SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.time_pattern));
        int minutes = 0;
        int hours = 0;

        try {

            Date articleDate = formatter.parse(dateInString.replaceAll(context.getResources().getString(R.string.time_replace_symbol), context.getResources().getString(R.string.time_replace_with)));
            Date deviceDate =  Calendar.getInstance().getTime();

            long dateDiff =  deviceDate.getTime() - articleDate.getTime();

            hours = (int) (dateDiff / (1000 * 60 * 60));
            minutes = (int) (dateDiff / (1000 * 60)) - (hours * 60);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (minutes < 60){
            return String.valueOf(minutes) + context.getResources().getString(R.string.time_minutes);
        }

        if (hours < 24 && minutes > 60){
            return String.valueOf(hours) + context.getResources().getString(R.string.time_hours);
        }

        return dateInString;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("newsOrigin")){
            country = preferences.getString("newsOrigin", "us" );
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this);
    }
}

