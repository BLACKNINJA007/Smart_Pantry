package com.example.smartpantry.logic;

import android.content.Context;
import android.content.SharedPreferences;

/** Small wrapper around SharedPreferences for the user's settings. */
public class AppSettings {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** True by default: expiring items are highlighted on the pantry list. */
    public static boolean isExpiryAlertOn(Context context) {
        return prefs(context).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlertOn(Context context, boolean on) {
        prefs(context).edit().putBoolean(KEY_EXPIRY_ALERTS, on).apply();
    }
}
