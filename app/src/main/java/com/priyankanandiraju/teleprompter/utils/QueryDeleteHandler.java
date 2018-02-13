package com.priyankanandiraju.teleprompter.utils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by priyankanandiraju on 2/12/18.
 */

public class QueryDeleteHandler extends AsyncQueryHandler {
    @Nullable
    private onQueryHandlerDeleteComplete mOnQueryHandlerDeleteComplete;

    public interface onQueryHandlerDeleteComplete {
        void onDeleteComplete(int token, Object cookie, int result);
    }
    public QueryDeleteHandler(ContentResolver cr) {
        super(cr);
    }

    public QueryDeleteHandler(ContentResolver cr, @Nullable onQueryHandlerDeleteComplete onQueryHandlerDeleteComplete) {
        super(cr);
        mOnQueryHandlerDeleteComplete = onQueryHandlerDeleteComplete;
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        if (mOnQueryHandlerDeleteComplete != null) {
            mOnQueryHandlerDeleteComplete.onDeleteComplete(token, cookie, result);
        }
    }
}
