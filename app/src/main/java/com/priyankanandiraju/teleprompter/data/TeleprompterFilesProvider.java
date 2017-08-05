package com.priyankanandiraju.teleprompter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.priyankanandiraju.teleprompter.data.TeleprompterFileContract.TeleprompterFileEvent;

/**
 * Created by priyankanandiraju on 8/2/17.
 */

public class TeleprompterFilesProvider extends ContentProvider {
    public static final String LOG_TAG = TeleprompterFilesProvider.class.getSimpleName();
    private static final int TELEPROMPTER_FILES = 100;
    private static final int TELEPROMPTER_FILE_ITEM = 200;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TeleprompterFileContract.CONTENT_AUTHORITY, TeleprompterFileContract.PATH_TELEPROMPTER_FILES, TELEPROMPTER_FILES);
        sUriMatcher.addURI(TeleprompterFileContract.CONTENT_AUTHORITY, TeleprompterFileContract.PATH_TELEPROMPTER_FILES + "/#", TELEPROMPTER_FILE_ITEM);
    }

    private TeleprompterFilesDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = TeleprompterFilesDbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TELEPROMPTER_FILES:
                cursor = database.query(TeleprompterFileEvent.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TELEPROMPTER_FILE_ITEM:
                selection = selection + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TeleprompterFileEvent.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TELEPROMPTER_FILES:
                return TeleprompterFileEvent.CONTENT_LIST_TYPE;
            case TELEPROMPTER_FILE_ITEM:
                return TeleprompterFileEvent.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TELEPROMPTER_FILES:
                return insertTeleprompterFile(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTeleprompterFile(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TeleprompterFileEvent.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TELEPROMPTER_FILES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TeleprompterFileEvent.TABLE_NAME, selection, selectionArgs);
                break;
            case TELEPROMPTER_FILE_ITEM:
                // Delete a single row given by the ID in the URI
                selection = TeleprompterFileEvent._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TeleprompterFileEvent.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
