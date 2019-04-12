package com.snc.zero.json;

import com.snc.zero.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSONObject Helper
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class JSONHelper {
    private static final String TAG = JSONHelper.class.getSimpleName();

    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        try {
            if (hasKey(jsonObject, key)) {
                return jsonObject.getString(key);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e);
        }
        return defaultValue;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key, JSONObject defaultValue) {
        try {
            if (hasKey(jsonObject, key)) {
                return jsonObject.getJSONObject(key);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e);
        }
        return defaultValue;
    }

    private static boolean hasKey(JSONObject jsonObject, String key) {
        if (null == jsonObject || null == key || key.length() <= 0) {
            return false;
        }
        return jsonObject.has(key);
    }

}
