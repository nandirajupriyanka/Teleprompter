package com.priyankanandiraju.teleprompter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.priyankanandiraju.teleprompter.data.TeleprompterFileContract.TeleprompterFileEvent;

/**
 * Created by priyankanandiraju on 8/2/17.
 */

public class TeleprompterFilesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Teleprompter.db";
    public static final int DATABASE_VERSION = 1;
    private static TeleprompterFilesDbHelper mDbHelper;

    public TeleprompterFilesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TeleprompterFilesDbHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (mDbHelper == null) {
            mDbHelper = new TeleprompterFilesDbHelper(context.getApplicationContext());
        }
        return mDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + TeleprompterFileEvent.TABLE_NAME + " (" +
                TeleprompterFileEvent._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TeleprompterFileEvent.COLUMN_FILE_IMAGE + " TEXT, " +
                TeleprompterFileEvent.COLUMN_FILE_TITLE + " TEXT NOT NULL, " +
                TeleprompterFileEvent.COLUMN_FILE_CONTENT + " TEXT NOT NULL, " +
                TeleprompterFileEvent.COLUMN_FILE_IS_FAV + " INTEGER DEFAULT 0);";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
