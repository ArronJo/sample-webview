package com.snc.zero.json;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * JSONObject Helper
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class JSONHelper {

    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        if (isNull(jsonObject, key) || !has(jsonObject, key)) {
            return defaultValue;
        }
        return jsonObject.optString(key, defaultValue);
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key, JSONObject defaultValue) {
        if (isNull(jsonObject, key) || !has(jsonObject, key)) {
            return defaultValue;
        }

        return jsonObject.optJSONObject(key);
    }

    public static void put(JSONObject jsonObject, String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private static boolean has(JSONObject jsonObject, String key) {
        if (null == jsonObject || null == key || key.length() <= 0) {
            return true;
        }
        return jsonObject.has(key);
    }

    private static boolean isNull(JSONObject jsonObject, String key) {
        if (null == jsonObject || TextUtils.isEmpty(key)) {
            return false;
        }
        return jsonObject.isNull(key);
    }
}
