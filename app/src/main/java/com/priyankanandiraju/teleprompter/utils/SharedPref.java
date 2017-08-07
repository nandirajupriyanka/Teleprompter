package com.priyankanandiraju.teleprompter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.priyankanandiraju.teleprompter.R;

/**
 * Created by priyankanandiraju on 8/3/17.
 */

public class SharedPref {

    private static final String PREF_NAME = "1000000";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context mContext;

    public SharedPref(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public void setSpeed(int speed) {
        editor.putInt(mContext.getString(R.string.KEY_SPEED), speed);
        editor.commit();
    }

    public String getSpeed() {
        return sharedPreferences.getString(mContext.getString(R.string.KEY_SPEED), mContext.getString(R.string.speed_10_value));
    }

    public void setTextColor(String textColor) {
        editor.putString(mContext.getString(R.string.KEY_TEXT_COLOR), textColor);
        editor.commit();
    }

    public String getTextColor() {
        return sharedPreferences.getString(mContext.getString(R.string.KEY_TEXT_COLOR), mContext.getString(R.string.text_color_black_value));
    }

    public void setTextSize(int size) {
        editor.putInt(mContext.getString(R.string.KEY_TEXT_SIZE), size);
        editor.commit();
    }

    public String getTextSize() {
        return sharedPreferences.getString(mContext.getString(R.string.KEY_TEXT_SIZE), mContext.getString(R.string.text_size_small_value));
    }




}
