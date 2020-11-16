package com.snc.sample.webview.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.snc.sample.webview.BuildConfig;
import com.snc.sample.webview.R;
import com.snc.sample.webview.bridge.AndroidBridge;
import com.snc.sample.webview.download.CSDownloadListener;
import com.snc.sample.webview.requetcode.RequestCode;
import com.snc.zero.activity.BaseActivity;
import com.snc.zero.dialog.DialogHelper;
import com.snc.zero.keyevent.BackKeyShutdown;
import com.snc.zero.log.Logger;
import com.snc.zero.util.PackageUtil;
import com.snc.zero.util.StringUtil;
import com.snc.zero.webview.CSWebChromeClient;
import com.snc.zero.webview.CSWebViewClient;
import com.snc.zero.webview.WebViewHelper;

/**
 * WebView Activity
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class WebViewActivity extends BaseActivity {
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private Activity activity;
    private WebView webview;
    private CSWebChromeClient webChromeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.activity = this;

        init();

        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    @SuppressLint("AddJavascriptInterface")
    private void init() {
        ViewGroup contentView = findViewById(R.id.contentView);
        if (null == contentView) {
            DialogHelper.alert(this, "The contentView does not exist.");
            finish();
            return;
        }

        // add webview
        this.webview = WebViewHelper.addWebView(getContext(), contentView);

        // options
        //this.webview.getSettings().setSupportMultipleWindows(true);

        // set user-agent
        try {
            String ua = this.webview.getSettings().getUserAgentString();
            if (!ua.endsWith(" ")) {
                ua += " ";
            }
            ua += PackageUtil.getApplicationName(this);
            ua += "/" + PackageUtil.getPackageVersionName(this);
            ua += "." + PackageUtil.getPackageVersionCode(this);
            this.webview.getSettings().setUserAgentString(ua);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG, e);
        }

        // set webViewClient
        CSWebViewClient webviewClient = new CSWebViewClient();
        this.webview.setWebViewClient(webviewClient);

        // set webChromeClient
        this.webChromeClient = new CSWebChromeClient(webview.getContext());
        this.webview.setWebChromeClient(webChromeClient);

        // add interface
        this.webview.addJavascriptInterface(new AndroidBridge(webview), "AndroidBridge");

        // add download listener
        this.webview.setDownloadListener(new CSDownloadListener(this.activity));

        // load url
        //WebViewHelper.loadUrl(this.webview, "file:///android_asset/www/docs/sample/sample.html");
        WebViewHelper.loadUrl(this.webview, "https://www.google.com");
    }

    @Override
    protected void onDestroy() {
        WebViewHelper.removeWebView(this.webview);
        this.webview = null;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            Logger.i(TAG, "[ACTIVITY] onActivityResult(): WebView isVideoPlayingInFullscreen = " + this.webChromeClient.isVideoPlayingInFullscreen());
            if (this.webChromeClient.isVideoPlayingInFullscreen()) {
                return false;
            }

            // multiple windows go back
            if (null != this.webChromeClient.getNewWebView()) {
                Logger.i(TAG, "[ACTIVITY] onActivityResult(): NewWebView canGoBack = " + this.webChromeClient.getNewWebView().canGoBack());
                if (this.webChromeClient.getNewWebView().canGoBack()) {
                    this.webChromeClient.getNewWebView().goBack();
                    return true;
                } else {
                    this.webChromeClient.closeNewWebView();
                }
                return true;
            }

            Logger.i(TAG, "[ACTIVITY] onActivityResult(): WebView canGoBack = " + this.webview.canGoBack());
            // go back
            if (this.webview.canGoBack()) {
                this.webview.goBack();
                return true;
            }

            if (BackKeyShutdown.isFirstBackKeyPress(keyCode, event)) {
                Toast.makeText(getContext(), getString(R.string.one_more_press_back_button), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) {
            Logger.i(TAG, "[ACTIVITY] onActivityResult(): requestCode[" + requestCode + "],  resultCode[" + resultCode + "],  data[null]");
        } else {
            Logger.i(TAG, "[ACTIVITY] onActivityResult(): requestCode[" + requestCode + "],  resultCode[" + resultCode + "],  data[" +
                    "\n  action = " + data.getAction() +
                    "\n  scheme = " + data.getScheme() +
                    "\n  data = " + data.getData() +
                    "\n  type = " + data.getType() +
                    "\n  extras = " + StringUtil.toString(data.getExtras()) +
                    "\n]");
        }

        //++ [[START] File Chooser]
        if (RequestCode.REQUEST_FILE_CHOOSER_NORMAL == requestCode) {
            Logger.i(TAG, "[ACTIVITY] onActivityResult(): REQUEST_FILE_CHOOSER_NORMAL");
            this.webChromeClient.onActivityResultFileChooserNormal(requestCode, resultCode, data);
        }
        else if (RequestCode.REQUEST_FILE_CHOOSER_LOLLIPOP == requestCode) {
            Logger.i(TAG, "[ACTIVITY] onActivityResult(): REQUEST_FILE_CHOOSER_LOLLIPOP");
            this.webChromeClient.onActivityResultFileChooserLollipop(requestCode, resultCode, data);
        }
        //-- [[E N D] File Chooser]

        //++ [[START] Take a picture]
        else if (RequestCode.REQUEST_TAKE_A_PICTURE == requestCode) {
            Logger.i(TAG, "[ACTIVITY] onActivityResult(): REQUEST_TAKE_A_PICTURE");
            this.webChromeClient.onActivityResultTakePicture(this.webview, requestCode, resultCode, data);
        }
        //++ [[E N D] Take a picture]

        super.onActivityResult(requestCode, resultCode, data);
    }

}
