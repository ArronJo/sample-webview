package com.snc.sample.webview.bridge.plugin;

import android.webkit.WebView;

import com.snc.sample.webview.bridge.AndroidBridge;
import com.snc.sample.webview.bridge.plugin.interfaces.Plugin;
import com.snc.zero.json.JSONHelper;

import org.json.JSONObject;

import timber.log.Timber;

/**
 * Plugin
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
@SuppressWarnings({"InstantiationOfUtilityClass", "unused", "RedundantSuppression"})
public class PluginAPI implements Plugin {
    private static final PluginAPI mInstance = new PluginAPI();
    public static PluginAPI getInstance() {
        return mInstance;
    }

    /////////////////////////////////////////////////

    public static void recommended(final WebView webview, final JSONObject args, final String cbId) {
        Timber.i("[WEBVIEW] recommended : args[" + args + "], cbId[" + cbId + "]");

        new Thread(() -> {

            // test code...
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Timber.e(e);
            }
            //--

            JSONObject jsonObject = new JSONObject();
            JSONHelper.put(jsonObject, "result", "success");

            // send result
            AndroidBridge.callFromNative(webview, cbId, "00000", jsonObject.toString());
        }).start();
    }
}
