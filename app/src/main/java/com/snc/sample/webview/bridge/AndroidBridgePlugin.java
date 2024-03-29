package com.snc.sample.webview.bridge;

import android.webkit.WebView;

import com.snc.sample.webview.bridge.plugin.PluginAPI;
import com.snc.sample.webview.bridge.plugin.PluginCamera;
import com.snc.sample.webview.bridge.plugin.interfaces.Plugin;
import com.snc.zero.dialog.DialogBuilder;
import com.snc.zero.json.JSONHelper;
import com.snc.zero.reflect.ReflectHelper;

import org.json.JSONObject;

import java.lang.reflect.Method;

import timber.log.Timber;

/**
 * WebView JavaScript Interface Process
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
public class AndroidBridgePlugin {

    /////////////////////////////////////////////////
    // Plugins

    private static final String PLUGIN_API = "api";
    private static final String PLUGIN_CAMERA = "camera";


    /////////////////////////////////////////////////
    // Method

    public static boolean execute(WebView webView, JSONObject jsonObject) {
        //final String hostCommand = JSONHelper.getString(jsonObject, "hostCommand", "");
        final String pluginName = JSONHelper.getString(jsonObject, "plugin", "");
        final String methodName = JSONHelper.getString(jsonObject, "method", "");
        final JSONObject args = JSONHelper.getJSONObject(jsonObject, "args", new JSONObject());
        final String callback = JSONHelper.getString(jsonObject, "callback", "");
        final String cbId = JSONHelper.getString(jsonObject, "cbId", "");

        Timber.i("[WEBVIEW] callNativeMethod: execute() :  plugin = " + pluginName + ", method = " + methodName + ",  args = " + args + ",  callback = " + callback);

        try {
            Plugin plugin = null;
            if (PLUGIN_API.equals(pluginName)) {
                plugin = (Plugin) PluginAPI.getInstance();
            }
            else if (PLUGIN_CAMERA.equals(pluginName)) {
                plugin = (Plugin) PluginCamera.getInstance();
            }

            if (null == plugin) {
                Timber.e("[WEBVIEW] plugin is null");
                throw new Exception("plugin is null");
            }

            Method method = ReflectHelper.getMethod(plugin, methodName);
            if (null == method) {
                Timber.e("[WEBVIEW] method is null");
                throw new Exception("method is null");
            }

            ReflectHelper.invoke(plugin, method, webView, args, cbId);
            return true;

        } catch (Exception e) {
            Timber.e(e);
            DialogBuilder.with(webView.getContext())
                    .setMessage(e.toString())
                    .show();
        }
        return false;
    }

}
