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

    private WebView webview;
    private CSWebChromeClient webChromeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // add user-agent
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
            e.printStackTrace();
        }

        // set webViewClient
        CSWebViewClient webviewClient = new CSWebViewClient();
        this.webview.setWebViewClient(webviewClient);

        // set webChromeClient
        this.webChromeClient = new CSWebChromeClient(webview.getContext());
        this.webview.setWebChromeClient(webChromeClient);

        // add interface
        this.webview.addJavascriptInterface(new AndroidBridge(webview), "AndroidBridge");

        // load url
        WebViewHelper.loadUrl(this.webview, "file:///android_asset/www/docs/sample/sample.html");
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
            Logger.i(TAG, "onActivityResult(): requestCode[" + requestCode + "],  resultCode[" + resultCode + "],  data[null]");
        } else {
            Logger.i(TAG, "onActivityResult(): requestCode[" + requestCode + "],  resultCode[" + resultCode + "],  data[" +
                    "\n  action = " + data.getAction() +
                    "\n  scheme = " + data.getScheme() +
                    "\n  data = " + data.getData() +
                    "\n  type = " + data.getType() +
                    "\n  extras = " + StringUtil.toString(data.getExtras()) +
                    "\n]");
        }

        if (RequestCode.REQUEST_CODE_FILE_CHOOSER_NORMAL == requestCode) {
            Logger.i(TAG, "onActivityResult(): REQUEST_CODE_FILE_CHOOSER_NORMAL");
            if (Activity.RESULT_OK == resultCode) {
                this.webChromeClient.onActivityResultFileChooserNormal(requestCode, resultCode, data);
            }
        } else if (RequestCode.REQUEST_CODE_FILE_CHOOSER_LOLLIPOP == requestCode) {
            Logger.i(TAG, "onActivityResult(): REQUEST_CODE_FILE_CHOOSER_LOLLIPOP");
            if (Activity.RESULT_OK == resultCode) {
                this.webChromeClient.onActivityResultFileChooserLollipop(requestCode, resultCode, data);
            }
        } else if (RequestCode.REQUEST_CODE_TAKE_A_PICTURE == requestCode) {
            Logger.i(TAG, "onActivityResult(): REQUEST_CODE_TAKE_A_PICTURE");
            if (Activity.RESULT_OK == resultCode) {
                this.webChromeClient.onActivityResultTakePicture(this.webview, requestCode, resultCode, data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}