package com.priyankanandiraju.teleprompter;

import android.app.Application;

import com.priyankanandiraju.teleprompter.helper.DeviceConfig;

/**
 * Created by priyankanandiraju on 8/20/17.
 */

public class TeleprompterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Device Config utility at startup
        final DeviceConfig deviceConfig = DeviceConfig.getInstance();
        deviceConfig.initContext(this);
    }
}
