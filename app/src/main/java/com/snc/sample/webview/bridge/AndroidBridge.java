package com.snc.sample.webview.bridge;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.snc.sample.webview.bridge.process.AndroidBridgeProcess;
import com.snc.zero.dialog.DialogHelper;
import com.snc.zero.json.JSONHelper;
import com.snc.zero.log.Logger;
import com.snc.zero.reflect.ReflectHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * WebView JavaScript Interface Bridge
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class AndroidBridge {
    private static final String TAG = AndroidBridge.class.getSimpleName();

    private static final String SCHEME_BRIDGE = "native";

    private static final String HOST_COMMAND = "callNative";

    private static final String SCHEME_JAVASCRIPT = "javascript:";

    private final WebView webView;

    private static final Map<String, String> callbackFunctionNames = new HashMap<>();
    private static Uri extraOutput;

    // constructor
    public AndroidBridge(WebView webView) {
        this.webView = webView;
    }


    //++ [START] call Web --> Native

    // ex) "native://callNative?" + btoa(encodeURIComponent(JSON.stringify({ command:\"apiSample\", args{max:1,min:1}, callback:\"callbackNativeResponse\" })))
    @JavascriptInterface
    public boolean callNativeMethod(String urlString) {
        //Logger.i(TAG, "[WEBVIEW] callNativeMethod: " + urlString);
        try {
            return executeProcess(this.webView, parse(urlString));
        } catch (Exception e) {
            Logger.e(TAG, e);
            if (webView.getContext() instanceof Activity) {
                DialogHelper.alert((Activity) webView.getContext(), e.toString());
            }
        }
        return false;
    }

    private JSONObject parse(String urlString) throws IOException {
        Uri uri = Uri.parse(urlString);
        Logger.i(TAG, "[WEBVIEW] callNativeMethod: parse() : uri = " + uri);

        if (!SCHEME_BRIDGE.equals(uri.getScheme())) {
            throw new IOException("\"" + uri.getScheme() + "\" scheme is not supported.");
        }
        if (!HOST_COMMAND.equals(uri.getHost())) {
            throw new IOException("\"" + uri.getHost() + "\" host is not supported.");
        }

        String query = uri.getEncodedQuery();
        try {
            query = new String(Base64.decode(query, Base64.DEFAULT));
            query = URLDecoder.decode(query, "utf-8");
            return new JSONObject(query);
        } catch (Exception e) {
            throw new IOException("\"" + query + "\" is not JSONObject.");
        }
    }

    private boolean executeProcess(final WebView webview, final JSONObject jsonObject) {
        final String command = JSONHelper.getString(jsonObject, "command", "");
        final JSONObject args = JSONHelper.getJSONObject(jsonObject, "args", new JSONObject());
        final String callback = JSONHelper.getString(jsonObject, "callback", "");

        Logger.i(TAG, "[WEBVIEW] callNativeMethod: executeProcess() :  command = " + command + ",  args = " + args + ",  callback = " + callback);

        final AndroidBridgeProcess process = AndroidBridgeProcess.getInstance();
        final Method method = ReflectHelper.getMethod(process, command);
        if (null == method) {
            Logger.e(TAG, "[WEBVIEW] method is null");
            return false;
        }

        try {
            ReflectHelper.invoke(process, method, webview, args, callback);
            return true;
        } catch (Exception e) {
            DialogHelper.alert((Activity) webview.getContext(), e.getMessage());
            Logger.e(TAG, e);
        }
        return false;
    }

    //-- [E N D] call Web --> Native


    //++ [START] call Native --> Web

    public static void callJSFunction(final WebView webView, String functionName, String... params) {
        final StringBuilder buff = new StringBuilder();
        buff.append("try { ");
        // function name
        buff.append("  ").append(functionName).append("(");
        // parameters
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (null != param) {
                buff.append("'").append(param).append("'");
            } else {
                buff.append("''");
            }
            if (i < params.length - 1) {
                buff.append(", ");
            }
        }
        buff.append("); ");
        buff.append("} catch(e) { ");
        buff.append("  console.error(e.message); ");
        buff.append("}");

        // Run On UIThread
        webView.post(() -> evaluateJavascript(webView, buff.toString()));
    }

    private static void evaluateJavascript(final WebView webView, final String javascriptString) {
        String jsString = javascriptString;

        if (jsString.startsWith(SCHEME_JAVASCRIPT)) {
            jsString = jsString.substring(SCHEME_JAVASCRIPT.length());
        }

        jsString = jsString.replaceAll("\t", "    ");

        // Android 4.4 (KitKat, 19) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(jsString, value -> Logger.i(TAG, "[WEBVIEW] onReceiveValue: " + value));
        }
        // Android 4.3 or lower (Jelly Bean, 18)
        else {
            webView.loadUrl(SCHEME_JAVASCRIPT + jsString);
        }
    }

    //-- [E N D] call Native --> Web


    //++ [[START] for Native Interface]

    public static void executeJSFunction(WebView webView, int requestCode, String data) {
        String callbackJSFunction = getCallbackJSFunctionName(requestCode);
        if (null == callbackJSFunction || callbackJSFunction.isEmpty()) {
            Logger.e(TAG, "[WEBVIEW] Error: The executeJSFunction information is unknown.");
            return;
        }
        AndroidBridge.callJSFunction(webView, callbackJSFunction, data);
    }

    public static void setCallbackJSFunctionName(int requestCode, String functionName) {
        callbackFunctionNames.put(String.valueOf(requestCode), functionName);
    }

    private static String getCallbackJSFunctionName(int requestCode) {
        return callbackFunctionNames.remove(String.valueOf(requestCode));
    }

    public static Uri getExtraOutput(boolean pop) {
        if (pop) {
            Uri uri = extraOutput;
            extraOutput = null;
            return uri;
        }
        return extraOutput;
    }

    public static void setExtraOutput(Uri uri) {
        extraOutput = uri;
    }

    //-- [[E N D] for Native Interface]

}
