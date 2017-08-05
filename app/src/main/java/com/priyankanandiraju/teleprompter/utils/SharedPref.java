package com.priyankanandiraju.teleprompter.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by priyankanandiraju on 8/3/17.
 */

public class SharedPref {

    private static final String PREF_NAME = "1000000";
    private static final String KEY_SPEED = "KEY_SPEED";
    private static final String KEY_TEXT_COLOR = "KEY_TEXT_COLOR";
    private static final String KEY_TEXT_SIZE = "KEY_TEXT_SIZE";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        sharedPreferences  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setSpeed(int speed) {
        editor.putInt(KEY_SPEED, speed);
        editor.commit();
    }

    public int getSpeed(int defaultValue) {
        return sharedPreferences.getInt(KEY_SPEED, defaultValue);
    }

    public void setTextColor(String textColor) {
        editor.putString(KEY_TEXT_COLOR, textColor);
        editor.commit();
    }

    public String getTextColor(String defaultTextColor) {
        return sharedPreferences.getString(KEY_TEXT_COLOR, defaultTextColor);
    }

    public void setTextSize(int size) {
        editor.putInt(KEY_TEXT_COLOR, size);
        editor.commit();
    }

    public int getTextSize(int defaultSize) {
        return sharedPreferences.getInt(KEY_TEXT_SIZE, defaultSize);
    }




}
