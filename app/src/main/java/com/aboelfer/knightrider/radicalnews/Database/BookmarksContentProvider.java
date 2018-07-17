package com.aboelfer.knightrider.radicalnews.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import static com.aboelfer.knightrider.radicalnews.Database.BookmarksContract.BookmarkedArticlesEntry.TABLE_NAME;

/**
 * Created by KNIGHT RIDER on 7/12/2018.
 */

public class BookmarksContentProvider extends ContentProvider {

    public static final int BOOKMARKED_ARTICLES = 100;
    public static final int BOOKMARKED_ARTICLES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BookmarksContract.AUTHORITY, BookmarksContract.PATH_BOOKMARKED_ARTICLES, BOOKMARKED_ARTICLES);
        uriMatcher.addURI(BookmarksContract.AUTHORITY, BookmarksContract.PATH_BOOKMARKED_ARTICLES + "/#", BOOKMARKED_ARTICLES_WITH_ID);

        return uriMatcher;
    }

    BookmarksDBHelper bookmarksDBHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        bookmarksDBHelper = new BookmarksDBHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = bookmarksDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match){

            case BOOKMARKED_ARTICLES:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case BOOKMARKED_ARTICLES_WITH_ID:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

            default:
                throw new android.database.SQLException("Unknown uri query: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = bookmarksDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case BOOKMARKED_ARTICLES:

                long id = db.insert(TABLE_NAME, null, values);

                if (id > 0){
                    returnUri = ContentUris.withAppendedId(BookmarksContract.BookmarkedArticlesEntry.CONTENT_URI, id);
                    Toast.makeText(getContext(), "Added to Bookmarks!", Toast.LENGTH_SHORT).show();
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new android.database.SQLException("Unknown uri insert: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = bookmarksDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int articleRemoved;

        switch (match) {

            case BOOKMARKED_ARTICLES:
                articleRemoved = db.delete(TABLE_NAME, selection, selectionArgs);
                Toast.makeText(getContext(), "Removed from Bookmarks!", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri delete: " + uri);
        }
        if (articleRemoved != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return articleRemoved;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
