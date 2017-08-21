package com.priyankanandiraju.teleprompter.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by priyankanandiraju on 8/20/17.
 */

public class DeviceConfig {
    private static final String TAG = DeviceConfig.class.getSimpleName();

    private static final Lock singletonLock = new ReentrantLock();

    @SuppressLint("StaticFieldLeak") //we use app context. No leak.
    private static DeviceConfig ourInstance;

    private Context mContext = null;

    private boolean mIsPhone = true;
    private boolean mIsTablet = false;

    private DeviceConfig() {
    }

    public static DeviceConfig getInstance() {
        if (ourInstance == null) {
            singletonLock.lock();
            try {
                if (ourInstance == null) {
                    ourInstance = new DeviceConfig();
                }
            } finally {
                singletonLock.unlock();
            }
        }
        return ourInstance;
    }

    public synchronized void initContext(@NonNull final Context context) {
        Log.d(TAG, "initContext() called from " + context.getClass().getSimpleName());

        if (mContext == null) {
            ourInstance.setContext(context);

            // Determine if device is Handset or Tablet
            Configuration config = context.getResources().getConfiguration();
            if (config.smallestScreenWidthDp >= 600) {
                // Reference http://android-developers.blogspot.com/2011/07/new-tools-for-managing-screen-sizes.html
                setDeviceType(true);
            } else {
                setDeviceType(false);
            }
        } else {
            Log.d(TAG, "initContext() Already initialized");
        }
    }

    private void setContext(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private void setDeviceType(boolean isTablet) {
        mIsTablet = isTablet;
        mIsPhone = !mIsTablet;
        Log.v(TAG, "setDeviceType() phone:" + mIsPhone + " tablet:" + mIsTablet);
    }

    public boolean isPhone() {
        return mIsPhone;
    }

    public boolean isTablet() {
        return mIsTablet;
    }

}
