package com.aboelfer.knightrider.radicalnews.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KNIGHT RIDER on 7/9/2018.
 */

public class Article implements Parcelable {

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    private String author;
    private String title;
    private String description;
    private String article_url;
    private String image_url;
    private String publishing_date;

    public Article(String author, String title, String description, String article_url, String image_url, String publishing_date) {

        this.author = author;
        this.title = title;
        this.description = description;
        this.article_url = article_url;
        this.image_url = image_url;
        this.publishing_date = publishing_date;
    }


    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author = author;}

    public String getTitle() {
        return title;
    }
    public void setTitle(String title){this.title = title;}

    public String getDescription() {
        return description;
    }
    public void setDescription(String description){this.description = description;}

    public String getArticleUrl() {
        return article_url;
    }
    public void setArticleUrl(String article_url){this.article_url = article_url;}

    public String getImageUrl() {
        return image_url;
    }
    public void setImageUrl(String image_url){this.image_url = image_url;}

    public String getPublishingDate() {
        return publishing_date;
    }
    public void setPuplishingDate(String publishing_date){this.publishing_date = publishing_date;}

    private Article(Parcel in) {
        this.author = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.article_url = in.readString();
        this.image_url = in.readString();
        this.publishing_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(article_url);
        dest.writeString(image_url);
        dest.writeString(publishing_date);
    }
}
