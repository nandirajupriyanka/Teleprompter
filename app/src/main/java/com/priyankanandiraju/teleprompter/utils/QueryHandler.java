package com.priyankanandiraju.teleprompter.utils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by priyankanandiraju on 8/16/17.
 */

public class QueryHandler extends AsyncQueryHandler {

    @Nullable
    private onQueryHandlerInsertComplete mOnQueryHandlerInsertComplete;

    public interface onQueryHandlerInsertComplete {
        void onInsertComplete(int token, Object cookie, Uri uri);
        void onUpdateComplete(int token, Object cookie, int result);
    }
    public QueryHandler(ContentResolver cr) {
        super(cr);
    }

    public QueryHandler(ContentResolver cr, @Nullable onQueryHandlerInsertComplete onQueryHandlerInsertComplete) {
        super(cr);
        mOnQueryHandlerInsertComplete = onQueryHandlerInsertComplete;
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        if (mOnQueryHandlerInsertComplete != null) {
            mOnQueryHandlerInsertComplete.onInsertComplete(token, cookie, uri);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        if (mOnQueryHandlerInsertComplete != null) {
            mOnQueryHandlerInsertComplete.onUpdateComplete(token, cookie, result);
        }
    }
}
