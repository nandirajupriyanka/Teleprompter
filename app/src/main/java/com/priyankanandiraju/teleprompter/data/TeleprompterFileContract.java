package com.priyankanandiraju.teleprompter.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by priyankanandiraju on 8/2/17.
 */

public class TeleprompterFileContract {
    public static final String CONTENT_AUTHORITY = "com.priyankanandiraju.teleprompter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TELEPROMPTER_FILES = "teleprompterFiles";

    private TeleprompterFileContract() {
    }

    public static final class TeleprompterFileEvent implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TELEPROMPTER_FILES);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TELEPROMPTER_FILES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TELEPROMPTER_FILES;

        public static final String TABLE_NAME = "fileEntry";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_FILE_IMAGE = "image";
        public static final String COLUMN_FILE_TITLE = "title";
        public static final String COLUMN_FILE_CONTENT = "content";
        public static final String COLUMN_FILE_IS_FAV = "isFavourite";
    }
}
