package com.snc.zero.webview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.snc.sample.webview.R;
import com.snc.sample.webview.bridge.AndroidBridge;
import com.snc.sample.webview.requetcode.RequestCode;
import com.snc.zero.dialog.DialogHelper;
import com.snc.zero.log.Logger;
import com.snc.zero.util.BitmapUtil;
import com.snc.zero.util.DateTimeUtil;
import com.snc.zero.util.StringUtil;
import com.snc.zero.util.UriUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Custom WebChrome Client
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class CSWebChromeClient extends WebChromeClient {
    private static final String TAG = CSWebChromeClient.class.getSimpleName();

    private final Context context;

    // constructor
    public CSWebChromeClient(Context context) {
        this.context = context;
    }

    //++ [[START] File Chooser]
    private ValueCallback<Uri> filePathCallbackNormal;
    private ValueCallback<Uri[]> filePathCallbackLollipop;

    private final int IMAGE = 0;
    private final int AUDIO = 1;
    private final int VIDEO = 2;
    private Uri[] mediaURIs;

    // For Android 4.1+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        Logger.i(TAG, "[WEBVIEW] openFileChooser()  For Android 4.1+ \n:: uploadMsg[" + uploadMsg + "]  acceptType[" + acceptType + "]  capture[" + capture + "]");

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        TedPermission.with(this.context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Logger.i(TAG, "[WEBVIEW] onPermissionGranted()");
                        filePathCallbackNormal = uploadMsg;

                        openIntentChooser(acceptType);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Logger.e(TAG, "[WEBVIEW] onPermissionDenied()..." + deniedPermissions.toString());
                    }
                })
                .setPermissions(permissions.toArray(new String[] {}))
                .check();
    }

    // For Android 5.0+
    public boolean onShowFileChooser(final WebView webView, final ValueCallback<Uri[]> filePathCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
        Logger.i(TAG, "[WEBVIEW] openFileChooser()  For Android 5.0+ \n:: filePathCallback[" + filePathCallback + "]  fileChooserParams[" + fileChooserParams + "]");

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        TedPermission.with(this.context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Logger.i(TAG, "[WEBVIEW] onPermissionGranted()");
                        if (null != filePathCallbackLollipop) {
                            filePathCallbackLollipop.onReceiveValue(null);
                        }

                        filePathCallbackLollipop = filePathCallback;

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                openIntentChooser(fileChooserParams.getAcceptTypes());
                            } else  {
                                openIntentChooser("");
                            }
                        } catch (Exception e) {
                            DialogHelper.alert((Activity) context, e.getMessage());
                            filePathCallbackLollipop = null;
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Logger.e(TAG, "[WEBVIEW] onPermissionDenied()..." + deniedPermissions.toString());
                    }
                })
                .setPermissions(permissions.toArray(new String[] {}))
                .check();
        return true;
    }

    private void openIntentChooser(String[] acceptTypes) {
        String acceptType = "";

        for (String type : acceptTypes) {
            if (StringUtil.isEmpty(type)) {
                continue;
            }
            if (!type.startsWith("image/") && !type.startsWith("audio/") && !type.startsWith("video/") && !type.startsWith("application/")) {
                continue;
            }

            if (StringUtil.isEmpty(acceptType)) {
                acceptType += type;
            } else {
                acceptType += "," + type;
            }
        }

        openIntentChooser(acceptType);
    }

    private void openIntentChooser(String acceptType) {
        String type = acceptType;

        if (type.isEmpty() || "*/*".equalsIgnoreCase(acceptType)) {
            type = "image/*|audio/*|video/*";
        }

        try {
            mediaURIs = new Uri[3];

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                List<Intent> intentList = new ArrayList<>();

                String fileName = DateTimeUtil.formatDate(new Date(), "yyyyMMdd_HHmmss");

                if (type.contains("image/")) {
                    mediaURIs[IMAGE] = UriUtil.fromFile(context, new File(getExternalDir("image"), fileName + ".jpg"));

                    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaURIs[IMAGE]);
                    intentList.add(intent);
                }
                if (type.contains("audio/")) {
                    mediaURIs[AUDIO] = UriUtil.fromFile(context, new File(getExternalDir("audio"), fileName + ".m4a"));

                    final Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaURIs[AUDIO]);
                    intentList.add(intent);
                }
                if (type.contains("video/")) {
                    mediaURIs[VIDEO] =  UriUtil.fromFile(context, new File(getExternalDir("video"), fileName + ".mp4"));

                    final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaURIs[VIDEO]);
                    intentList.add(intent);
                }

                // Intent Chooser
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                Intent chooserIntent = Intent.createChooser(intent, "Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[] { }));
                ((Activity) context).startActivityForResult(chooserIntent, RequestCode.REQUEST_FILE_CHOOSER_LOLLIPOP);

            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                ((Activity) context).startActivityForResult(Intent.createChooser(intent, "File Chooser"), RequestCode.REQUEST_FILE_CHOOSER_NORMAL);
            }

        } catch (Exception e) {
            Logger.e(TAG, e);
            DialogHelper.alert((Activity) context, e.getMessage());
        }
    }

    private File getExternalDir(String type) {
        File dir = null;

        if ("image".equalsIgnoreCase(type) || "video".equalsIgnoreCase(type)) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        }
        else if ("audio".equalsIgnoreCase(type)) {
            dir = new File(Environment.getExternalStorageDirectory(), "Voice Recorder");
        }
        return dir;
    }

    public void onActivityResultFileChooserNormal(int requestCode, int resultCode, Intent data) {
        Logger.i(TAG, "[WEBVIEW] onActivityResultFileChooserNormal(): requestCode[" + requestCode + "]  resultCode[" + resultCode + "] data[" + data + "]");

        if (null == filePathCallbackNormal) {
            Logger.i(TAG, "[WEBVIEW] onActivityResultNormal(): filePathCallbackNormal is null !!!");
            return;
        }

        Uri result = (null == data || resultCode != Activity.RESULT_OK) ? null : data.getData();
        filePathCallbackNormal.onReceiveValue(result);
        filePathCallbackNormal = null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResultFileChooserLollipop(int requestCode, int resultCode, Intent data) {
        Logger.i(TAG, "[WEBVIEW] onActivityResultFileChooserLollipop(): requestCode[" + requestCode + "]  resultCode[" + resultCode + "] data[" + data + "]");

        if (null == filePathCallbackLollipop) {
            Logger.i(TAG, "[WEBVIEW] onActivityResultLollipop(): filePathCallbackLollipop is null !!!");
            return;
        }

        try {
            if (null != data) {
                filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            } else {
                List<Uri> results = new ArrayList<>();

                if (null != mediaURIs) {
                    if (null != mediaURIs[IMAGE] && new File(mediaURIs[IMAGE].getPath()).exists()) {
                        results.add(mediaURIs[IMAGE]);
                    }
                    if (null != mediaURIs[AUDIO] && new File(mediaURIs[AUDIO].getPath()).exists()) {
                        results.add(mediaURIs[AUDIO]);
                    }
                    if (null != mediaURIs[VIDEO] && new File(mediaURIs[VIDEO].getPath()).exists()) {
                        results.add(mediaURIs[VIDEO]);
                    }
                }

                filePathCallbackLollipop.onReceiveValue(results.toArray(new Uri[]{}));
            }

        } catch (Exception e) {
            Logger.e(TAG, e);
        } finally {
            filePathCallbackLollipop = null;
            mediaURIs = null;
        }
    }
    //-- [[E N D] File Chooser]


    //++ [[START] Take a picture]
    public void onActivityResultTakePicture(WebView webview, int requestCode, int resultCode, Intent data) {
        Logger.i(TAG, "[WEBVIEW] onActivityResultTakePicture(): requestCode[" + requestCode + "]  resultCode[" + resultCode + "] data[" + data + "]");
        if (Activity.RESULT_OK != resultCode) {
            return;
        }

        try {
            if (null != AndroidBridge.getExtraOutput(false)) {
                Logger.i(TAG, "[WEBVIEW] onActivityResultTakePicture(): REQUEST_CODE_TAKE_A_PICTURE (with ExtraOutput)");
                File file = AndroidBridge.getExtraOutput(true);
                Uri uri = UriUtil.fromFile(webview.getContext(), file);
                AndroidBridge.executeJSFunction(webview, requestCode, uri.toString());

                showTakePicture(context, file);
            } else if (null != data) {
                Logger.i(TAG, "[WEBVIEW] onActivityResultTakePicture(): REQUEST_CODE_TAKE_A_PICTURE (with Intent)");
                String params = null;
                Bitmap bitmap = null;
                if ("inline-data".equals(data.getAction())) {
                    Bundle extras = data.getExtras();
                    if (null != extras) {
                        bitmap = (Bitmap) extras.get("data");
                        if (null != bitmap) {
                            params = bitmap.toString();
                        }
                    }
                } else if (null != data.getData()) {
                    Uri uri = data.getData();
                    params = StringUtil.nvl(uri, "");
                }
                AndroidBridge.executeJSFunction(webview, requestCode, params);

                if (null != bitmap) {
                    showTakePicture(context, bitmap);
                }
            }

        } catch (Exception e) {
            DialogHelper.alert((Activity) context, e.toString());
        }
    }

    private void showTakePicture(Context context, File file) {
        showTakePicture(context, BitmapUtil.decodeBitmap(context, file));
    }

    private void showTakePicture(Context context, Bitmap bitmap) {
        ImageView iv = new ImageView(context);
        ViewGroup.LayoutParams params = iv.getLayoutParams();
        if (null == params) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        iv.setLayoutParams(params);
        iv.setImageBitmap(bitmap);

        DialogHelper.alert((Activity) context, iv);
    }
    //-- [[E N D] Take a picture]


    //++ [[START] Geolocation, Record Audio]
    @Override
    public void onPermissionRequest(final PermissionRequest request) {
        Logger.i(TAG, "[WEBVIEW] onPermissionRequest: request[" + request + "]");

        // RECORD AUDIO, RECORD VIDEO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Uri origin = request.getOrigin();

            for (String permission : request.getResources()) {
                switch (permission) {
                    case PermissionRequest.RESOURCE_AUDIO_CAPTURE: {
                        TedPermission.with(this.context)
                                .setPermissionListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted() {
                                        Logger.i(TAG, "[WEBVIEW] onPermissionGranted() : android.webkit.resource.AUDIO_CAPTURE :: origin[" + origin + "] request[" + request + "]");
                                        request.grant(request.getResources());
                                    }

                                    @Override
                                    public void onPermissionDenied(List<String> deniedPermissions) {
                                        Logger.e(TAG, "[WEBVIEW] onPermissionDenied() : android.webkit.resource.AUDIO_CAPTURE :: origin[" + origin + "] request[" + request + "]");
                                        request.deny();
                                    }
                                })
                                .setPermissions(new String[] {
                                        // Dangerous Permission
                                        Manifest.permission.RECORD_AUDIO
                                })
                                .check();
                        return;
                    }

                    case PermissionRequest.RESOURCE_VIDEO_CAPTURE: {
                        TedPermission.with(this.context)
                                .setPermissionListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted() {
                                        Logger.i(TAG, "[WEBVIEW] onPermissionGranted() : android.webkit.resource.VIDEO_CAPTURE :: origin[" + origin + "] request[" + request + "]");
                                        request.grant(request.getResources());
                                    }

                                    @Override
                                    public void onPermissionDenied(List<String> deniedPermissions) {
                                        Logger.e(TAG, "[WEBVIEW] onPermissionDenied() : android.webkit.resource.VIDEO_CAPTURE :: origin[" + origin + "] request[" + request + "]");
                                        request.deny();
                                    }
                                })
                                .setPermissions(new String[] {
                                        // Dangerous Permission
                                        Manifest.permission.CAMERA
                                })
                                .check();
                        return;
                    }
                }
            }
        }
        super.onPermissionRequest(request);
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        Logger.i(TAG, "[WEBVIEW] onPermissionRequestCanceled: request[" + request + "]");
        super.onPermissionRequestCanceled(request);
    }
    //-- [[E N D] Geolocation, Record Audio]


    //++ [[START] Geolocation]
    @Override
    public void onGeolocationPermissionsHidePrompt() {
        Logger.i(TAG, "[WEBVIEW] onGeolocationPermissionsHidePrompt");
        super.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
        Logger.i(TAG, "[WEBVIEW] onGeolocationPermissionsShowPrompt : origin[" + origin + "] callback[" + callback + "]");

        TedPermission.with(this.context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Logger.i(TAG, "[WEBVIEW] onGeolocationPermissionsShowPrompt : onPermissionGranted() : origin[" + origin + "] callback[" + callback + "]");
                        callback.invoke(origin, true, false);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Logger.e(TAG, "[WEBVIEW] onGeolocationPermissionsShowPrompt : onPermissionDenied() : origin[" + origin + "] callback[" + callback + "]");
                        callback.invoke(origin, false, false);
                    }
                })
                .setPermissions(new String[] {
                        // Dangerous Permission
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                })
                .check();

        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }
    //-- [[E N D] Geolocation]


    //++ [[START] Javascript Alert]
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        Logger.i(TAG, "[WEBVIEW] onJsAlert(): url[" + view.getUrl() + "], message[" + message + "], JsResult[" + result + "]");

        //++
        // custom dialog
        String title = StringUtil.nvl(Uri.parse(url).getLastPathSegment(), "");
        DialogHelper.alert((Activity) context, title, message, android.R.string.ok, (dialog, which) -> result.confirm());
        return true;
        //||
        // default dialog
        //return super.onJsAlert(view, url, message, result);
        //--
    }
    //-- [[E N D] Javascript Alert]


    //++ [[START] Web Console Log]
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Logger.i(TAG, "[WEBVIEW] " + consoleMessage.messageLevel() + ":CONSOLE] \"" + consoleMessage.message() + "\", source: " + consoleMessage.sourceId() + " (" + consoleMessage.lineNumber() + ")");
        return true;    // remove chromium log
        //return super.onConsoleMessage(consoleMessage);
    }
    //-- [[E N D] Web Console Log]


    //++ [[START] ProgressBar]
    @Override
    public void onProgressChanged(WebView view, int progress) {
        super.onProgressChanged(view, progress);
        Logger.i(TAG, "[WEBVIEW] onProgressChanged(): " + progress + "%,  url[" + view.getUrl() + "]");

        View v = findProgressBarInTopArea(view);
        if (progress >= 100) {
            if (null != v) {
                v.setVisibility(View.GONE);
            }
        } else {
            if (null != v) {
                ((ProgressBar) v).setProgress(progress);
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    // Find the progress bar widget in the top area.
    private View findProgressBarInTopArea(View view) {
        ViewParent parent = view.getParent();
        View v = null;
        while (null != parent) {
            v = ((ViewGroup) parent).findViewById(R.id.webViewProgressBar);
            if (null != v) {
                break;
            }
            parent = parent.getParent();
        }
        return v;
    }
    //-- [[E N D] ProgressBar]


    //++ [[START] Video Player (for fullscreen]
    private ViewGroup fullscreenContainer;
    private CustomViewCallback customViewCallback;

    public boolean isVideoPlayingInFullscreen() {
        return null != fullscreenContainer;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        Logger.i(TAG, "[WEBVIEW] onShowCustomView() - view[" + view + "], callback[" + callback + "]");

        // background
        if (null == fullscreenContainer) {
            fullscreenContainer = new FrameLayout(view.getContext());
            fullscreenContainer.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            fullscreenContainer.setBackgroundResource(android.R.color.black);
            fullscreenContainer.setVisibility(View.GONE);

            ViewGroup decor = ((Activity) this.context).getWindow().getDecorView().findViewById(android.R.id.content);
            decor.addView(fullscreenContainer, -1);
        }

        customViewCallback = callback;

        Logger.i(TAG, "[WEBVIEW] onShowCustomView() - view class name = " + view.getClass().getName());

        // add video view
        fullscreenContainer.addView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        fullscreenContainer.setVisibility(View.VISIBLE);

        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        Logger.i(TAG, "[WEBVIEW] onHideCustomView()");
        super.onHideCustomView();

        if (null != fullscreenContainer) {
            ViewGroup decor = ((Activity) this.context).getWindow().getDecorView().findViewById(android.R.id.content);
            decor.removeView(fullscreenContainer);
        }

        if (null != customViewCallback) {
            customViewCallback.onCustomViewHidden();
        }

        customViewCallback = null;
        fullscreenContainer = null;
    }
    //-- [[E N D] Video Player (for fullscreen]


    //++ [[START] Support Multiple Windows]
    private WebView newWebView;
    private int scrollX;
    private int scrollY;

    public WebView getNewWebView() {
        return newWebView;
    }

    public void closeNewWebView() {
        WebView webView = ((WebView) newWebView.getParent());
        webView.removeView(newWebView);
        newWebView.destroy();
        newWebView = null;

        webView.scrollTo(scrollX, scrollY);
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        Logger.i(TAG, "[WEBVIEW] onCreateWindow():  view[" + view + "]  isDialog[" + isDialog + "]  isUserGesture[" + isUserGesture + "]  resultMsg[" + resultMsg + "]");

        this.newWebView = WebViewHelper.addWebView(view.getContext(), view);
        view.bringChildToFront(this.newWebView);

        CSWebChromeClient webChromeClient = new CSWebChromeClient(view.getContext());
        this.newWebView.setWebChromeClient(webChromeClient);

        CSWebViewClient webviewClient = new CSWebViewClient();
        this.newWebView.setWebViewClient(webviewClient);

        scrollX = view.getScrollX();
        scrollY = view.getScrollY();
        view.scrollTo(0, 0);

        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(this.newWebView);
        resultMsg.sendToTarget();

        return true;
        //return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
        this.newWebView = null;
        Logger.i(TAG, "[WEBVIEW] onCloseWindow()");
    }
    //-- [[E N D] Support Multiple Windows]

}
