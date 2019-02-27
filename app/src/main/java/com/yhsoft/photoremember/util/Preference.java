package com.yhsoft.photoremember.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static android.content.SharedPreferences.Editor;
import static com.yhsoft.photoremember.util.DebugLog.d;
import static com.yhsoft.photoremember.util.DebugLog.e;
import static java.lang.Float.NaN;
import static java.lang.Integer.MIN_VALUE;

public final class Preference {
    private static String mPrefName = "PhoTrace";

    /**
     * Time range of All photos in the device
     */
    public static final String KEY_PHOTO_RANGE_BOTTOM = "key_photo_range_bottom";
    public static final String KEY_PHOTO_RANGE_TOP = "key_photo_range_top";
    public static final String KEY_PHOTO_RANGE_BOTTOM_STATIC = "key_photo_range_bottom";
    public static final String KEY_PHOTO_RANGE_TOP_STATIC = "key_photo_range_top";
    /**
     * Filtered Timebar Range by user operation
     */
    public static final String KEY_TIMEBAR_RANGE_BOTTOM = "key_timebar_range_bottom";
    public static final String KEY_TIMEBAR_RANGE_TOP = "key_timebar_range_top";

    /**
     * Photo bucket display mode : year / month / day
     */
    public static final String KEY_PHOTO_BUCKET_START_MODE = "key_photo_bucket_mode";

    /**
     * Specific date picker mode
     */
    public static final String KEY_PHOTO_DATE_PICK_MODE = "key_photo_date_pick_mode";

    /**
     * Date Value in Picker mode
     */
    public static final String KEY_YEAR_MODE_PICK_DATE = "key_year_mode_pick_date";
    public static final String KEY_OTHER_MODE_PICK_DATE = "key_other_mode_pick_date";

    public static final String KEY_PHOTO_DATE_PICK_BOTTOM = "key_photo_date_pick_bottom";
    public static final String KEY_PHOTO_DATE_PICK_TOP = "key_photo_date_pick_top";

    // Special preference : Date String with dot seperator
    // - must use with String.split method
    public static final String KEY_PICK_MODE_BUCKET_BOTTOM = "key_pick_mode_bucket_bottom";
    public static final String KEY_PICK_MODE_BUCKET_TOP = "key_pick_mode_bucket_top";


    public static final String KEY_MAP_VALUE_TOP = "key_map_value_top";
    public static final String KEY_MAP_VALUE_BOTTOM = "key_map_value_bottom";
    public static final String KEY_MAP_VALUE_LEFT = "key_map_value_left";
    public static final String KEY_MAP_VALUE_RIGHT = "key_map_value_right";

    public static final String KEY_APP_FIRST_START = "key_app_first_start";


    private Preference() {
    }

    public static void putBoolean(final Context context, final String key, final boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(key, value);

        editor.apply();
        d("putBoolean() success - k:" + key + ", v:" + value);
    }

    public static void putFloat(final Context context, final String key, final float value) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putFloat(key, value);

        editor.apply();

        d("putFloat() success - k:" + key + ", v:" + value);
    }

    public static void putInt(final Context context, final String key, final int value) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(key, value);

        editor.apply();
        d("putInt() success - k:" + key + ", v:" + value);
    }

    public static void putLong(final Context context, final String key, final long value) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong(key, value);

        editor.apply();
        d("putLong() success - k:" + key + ", v:" + value);
    }

    public static void putString(final Context context, final String key, final String value) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key, value);

        editor.apply();
        d("putString() success - k:" + key + ", v:" + value);
    }

    public static boolean getBoolean(final Context context, final String key) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);

        try {
            boolean v = prefs.getBoolean(key, false);
            d("getBoolean() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            e("getBoolean() failed - k:" + key + ", v: false");
            return false;
        }
    }

    public static float getFloat(final Context context, final String key) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);

        try {
            float v = prefs.getFloat(key, NaN);
            d("getFloat() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            e("getFloat() failed - k:" + key + ", v: NaN");
            return NaN;
        }
    }

    public static int getInt(final Context context, final String key) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);

        try {
            int v = prefs.getInt(key, MIN_VALUE);
            d("getInt() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            e("getInt() failed - k:" + key + ", v: MIN_VALUE");
            return MIN_VALUE;
        }
    }

    public static long getLong(final Context context, final String key) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);

        try {
            long v = prefs.getLong(key, Long.MIN_VALUE);
            d("getLong() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            e("getLong() failed - k:" + key + ", v: MIN_VALUE");
            return Long.MIN_VALUE;
        }
    }

    public static String getString(final Context context, final String key) {
        SharedPreferences prefs = context.getSharedPreferences(mPrefName, MODE_PRIVATE);

        try {
            String v = prefs.getString(key, "");
            d("getString() success - k:" + key + ", v:" + v);
            return v;
        } catch (ClassCastException e) {
            e("getString() failed - k:" + key + ", v: ");
            return "";
        }
    }
}
