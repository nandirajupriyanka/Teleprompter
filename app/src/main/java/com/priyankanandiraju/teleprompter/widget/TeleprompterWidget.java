package com.priyankanandiraju.teleprompter.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.priyankanandiraju.teleprompter.R;
import com.priyankanandiraju.teleprompter.utils.TeleprompterFile;

import static com.priyankanandiraju.teleprompter.utils.Constants.IMAGE_DATA;
import static com.priyankanandiraju.teleprompter.utils.Constants.SHARED_PREF_FILE;

/**
 * Implementation of App Widget functionality.
 */
public class TeleprompterWidget extends AppWidgetProvider {

    private static final String TAG = TeleprompterWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.app_name);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.teleprompter_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPref.contains(SHARED_PREF_FILE)) {
            views.setViewVisibility(R.id.ll_content, View.VISIBLE);
            views.setViewVisibility(R.id.tv_empty, View.GONE);

            String jsonPrefFile = sharedPref.getString(SHARED_PREF_FILE, null);
            Gson gson = new Gson();
            TeleprompterFile teleprompterFile = gson.fromJson(jsonPrefFile, TeleprompterFile.class);
            views.setTextViewText(R.id.file_title, teleprompterFile.getTitle());
            views.setTextViewText(R.id.file_content, teleprompterFile.getContent());

            String imageFileName = IMAGE_DATA + teleprompterFile.getTitle();
            if (sharedPref.contains(imageFileName)) {
                String previouslyEncodedImage = sharedPref.getString(imageFileName, "");
                if (!previouslyEncodedImage.equalsIgnoreCase("")) {
                    byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    views.setImageViewBitmap(R.id.file_icon, bitmap);
                } else {
                    views.setImageViewBitmap(R.id.file_icon, null);
                }
            }
        } else {
            Log.v(TAG, "No data");
            views.setViewVisibility(R.id.ll_content, View.GONE);
            views.setViewVisibility(R.id.tv_empty, View.VISIBLE);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

