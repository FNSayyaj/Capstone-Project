package com.aboelfer.knightrider.radicalnews.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by KNIGHT RIDER on 7/12/2018.
 */

public class BookmarksDBHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "bookmarkedarticles.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public BookmarksDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a table to hold bookmarked articles
        final String SQL_CREATE_BOOKMARKEDARTICLES_TABLE =
                " CREATE TABLE " +
                        BookmarksContract.BookmarkedArticlesEntry.TABLE_NAME + "(" +
                        BookmarksContract.BookmarkedArticlesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_AUTHOR + " TEXT, " +
                        BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_TITLE + " TEXT, " +
                        BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_DESCRIPTION + " TEXT, " +
                        BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_URL + " TEXT NOT NULL UNIQUE, " +
                        BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_IMAGE + " TEXT, " +
                        BookmarksContract.BookmarkedArticlesEntry.COLUMN_ARTICLE_DATE + " TEXT " +
                        " );";


        db.execSQL(SQL_CREATE_BOOKMARKEDARTICLES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + BookmarksContract.BookmarkedArticlesEntry.TABLE_NAME);
        onCreate(db);
    }
}
