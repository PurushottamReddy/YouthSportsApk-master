package com.example.youthsports.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDetailsInSharedPreferences {

    private static final String PREFERENCES_FILE = "appPreferences";
    private static SharedPreferences sharedPreferences;

    private UserDetailsInSharedPreferences() { }

    public static void initialize(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        }
    }

    public static void storeValue(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getValue(String key) {
        return sharedPreferences.getString(key, null);
    }

    public static void clearAllValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearValue(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
