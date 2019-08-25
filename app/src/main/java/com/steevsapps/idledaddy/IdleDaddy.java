package com.steevsapps.idledaddy;

import android.content.Context;
import android.content.res.Configuration;
import androidx.multidex.MultiDexApplication;

import com.steevsapps.idledaddy.preferences.PrefsManager;
import com.steevsapps.idledaddy.utils.LocaleManager;

public class IdleDaddy extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        // Init SharedPreferences manager
        PrefsManager.init(base);
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }
}
