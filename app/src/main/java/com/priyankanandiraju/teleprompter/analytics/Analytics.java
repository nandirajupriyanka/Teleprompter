package com.priyankanandiraju.teleprompter.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import static com.priyankanandiraju.teleprompter.analytics.AnalyticsConstant.*;

/**
 * Created by priyankanandiraju on 8/22/17.
 */

public class Analytics {

    public static void logEventAddFileToDb(Context context, String status) {
        Bundle bundle = new Bundle();
        bundle.putString(ADD_TO_DB_STATUS, status);
        FirebaseAnalytics.getInstance(context).logEvent(EVENT_ADD_FILE_TO_DB, bundle);
    }

    public static void logEventGetImageFromDevice(Context context, String status) {
        Bundle bundle = new Bundle();
        bundle.putString(PICK_IMAGE_STATUS, status);
        FirebaseAnalytics.getInstance(context).logEvent(EVENT_PICK_IMAGE, bundle);
    }
}
