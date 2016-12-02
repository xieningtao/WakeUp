package com.sf.banbanle;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by NetEase on 2016/12/1 0001.
 */

public class SpHelper {
    private static SharedPreferences sharedPreferences;

    public static void init(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE);
    }

    public static void saveStr(String tag, String content) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, content);
        editor.commit();
    }

    public static String getStr(String tag, String defaultValue) {
        return sharedPreferences.getString(tag, defaultValue);
    }
}
