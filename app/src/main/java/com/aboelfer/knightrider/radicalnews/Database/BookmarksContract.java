package com.aboelfer.knightrider.radicalnews.Database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by KNIGHT RIDER on 7/12/2018.
 */

public class BookmarksContract {

    public static final String AUTHORITY = "com.aboelfer.knightrider.radicalnews.Database.BookmarksContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_BOOKMARKED_ARTICLES = "bookmarkedarticles";

    public static final class BookmarkedArticlesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKMARKED_ARTICLES).build();

        public static final String TABLE_NAME = "bookmarkedarticles";
        public static final String COLUMN_ARTICLE_AUTHOR = "articleAuthor";
        public static final String COLUMN_ARTICLE_TITLE = "articleTitle";
        public static final String COLUMN_ARTICLE_DESCRIPTION = "articleDescription";
        public static final String COLUMN_ARTICLE_URL = "articleURL";
        public static final String COLUMN_ARTICLE_IMAGE = "articleImage";
        public static final String COLUMN_ARTICLE_DATE = "articleDate";
    }
}
