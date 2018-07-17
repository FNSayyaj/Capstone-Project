package com.aboelfer.knightrider.radicalnews.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aboelfer.knightrider.radicalnews.Model.Article;
import com.aboelfer.knightrider.radicalnews.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Created by KNIGHT RIDER on 7/9/2018.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private final List<Article> articles;
    private final Context context;
    private final ListArticleClickListener articleOnClickListener;

    public interface ListArticleClickListener {

        void onListItemClick(int clickedItemIndex);

    }

    public ArticlesAdapter(List<Article> articles, Context context, ArticlesAdapter.ListArticleClickListener listener ){

        this.articles = articles;
        this.context = context;
        this.articleOnClickListener = listener;

    }

    @NonNull
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycled_articles_items, parent, false);

        return new ArticlesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticlesAdapter.ViewHolder holder, final int position) {

        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    final ImageView articleIV;
    final TextView releaseDateTV, articleTitleTV, articleDescriptionTV;
    final RelativeLayout linearLayoutRV;
    String articleImageUrl, articleDescriptionText;

    ViewHolder(View itemView) {

        super(itemView);
        articleIV = itemView.findViewById(R.id.articleImage_iv);
        linearLayoutRV = itemView.findViewById(R.id.linearLayout_rv);
        releaseDateTV = itemView.findViewById(R.id.articleReleaseDate_tv);
        articleTitleTV = itemView.findViewById(R.id.articleTitle_tv);
        articleDescriptionTV = itemView.findViewById(R.id.articleDescription_tv);
        itemView.setOnClickListener(this);
    }

    void bind(int position) {

        final Article article = articles.get(position);

        articleImageUrl = String.valueOf(article.getImageUrl());
        articleDescriptionText = String.valueOf(article.getDescription());

        //determine if any of the inputs is null and take the appropriate action accordingly
        if (Objects.equals(articleImageUrl, context.getString(R.string.check_null_string)) && !Objects.equals(articleDescriptionText, context.getString(R.string.check_null_string))) {
            articleIV.setVisibility(View.GONE);
            articleDescriptionTV.setVisibility(View.VISIBLE);

        } else if (!Objects.equals(articleImageUrl, context.getString(R.string.check_null_string)) && !Objects.equals(articleDescriptionText, context.getString(R.string.check_null_string))) {
            Picasso.with(context).load(article.getImageUrl()).into(articleIV,
                    new Callback.EmptyCallback() {

                        @Override
                        public void onError() {
                            articleIV.setImageResource(R.drawable.ic_image_failed);
                        }
                    });
            articleDescriptionTV.setVisibility(View.GONE);

        } else if (!Objects.equals(articleImageUrl, context.getString(R.string.check_null_string)) && Objects.equals(articleDescriptionText, context.getString(R.string.check_null_string))) {
            Picasso.with(context).load(article.getImageUrl()).into(articleIV,
                    new Callback.EmptyCallback() {

                        @Override
                        public void onError() {
                            articleIV.setImageResource(R.drawable.ic_image_failed);
                        }
                    });
            articleDescriptionTV.setVisibility(View.GONE);

        } else if (Objects.equals(articleImageUrl, context.getString(R.string.check_null_string)) && Objects.equals(articleDescriptionText, context.getString(R.string.check_null_string))) {
            Picasso.with(context).load(article.getImageUrl()).into(articleIV,
                    new Callback.EmptyCallback() {

                        @Override
                        public void onError() {
                            articleIV.setImageResource(R.drawable.ic_image_failed);
                        }
                    });
            articleDescriptionTV.setVisibility(View.GONE);
        }
        String dateInString = String.valueOf(article.getPublishingDate());
        GetIntervalTime(dateInString);

        articleDescriptionTV.setText(article.getDescription());
        articleTitleTV.setText(article.getTitle());

    }

    @Override
    public void onClick(View v) {

        int clickedPosition = getAdapterPosition();
        articleOnClickListener.onListItemClick(clickedPosition);
    }

    private void GetIntervalTime(String dateInString){

        SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.time_pattern));


        try {

            Date articleDate = formatter.parse(dateInString.replaceAll(context.getResources().getString(R.string.time_replace_symbol), context.getResources().getString(R.string.time_replace_with)));
            Date deviceDate =  Calendar.getInstance().getTime();
            long diff =  deviceDate.getTime() - articleDate.getTime();

            int hours = (int) (diff / (1000 * 60 * 60));
            int minutes = (int) (diff / (1000 * 60)) - (hours * 60);

            if (minutes < 60){
                releaseDateTV.setText(String.valueOf(minutes) + context.getResources().getString(R.string.time_minutes));
            }

            if (hours < 24 && minutes > 60){
                releaseDateTV.setText(String.valueOf(hours) + context.getResources().getString(R.string.time_hours));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

    public void refreshEvents(List<Article> articles) {

        this.articles.clear();
        this.articles.addAll(articles);
        notifyDataSetChanged();
    }

}