package com.snc.sample.webview.bridge.process;

import android.Manifest;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.snc.sample.webview.bridge.AndroidBridge;
import com.snc.sample.webview.requetcode.RequestCode;
import com.snc.zero.dialog.DialogHelper;
import com.snc.zero.log.Logger;
import com.snc.zero.util.FileUtil;
import com.snc.zero.util.IntentUtil;
import com.snc.zero.util.UriUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * WebView JavaScript Interface Process
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
@SuppressWarnings("unused")
public class AndroidBridgeProcess {
    private static final String TAG = AndroidBridgeProcess.class.getSimpleName();

    public static void apiRecommended(final WebView webview, final JSONObject args, final String callback) {
        Logger.d(TAG, "apiRecommended(): args[" + args + "], callback[" + callback + "]");

        // async
        new Thread(new Runnable() {
            @Override
            public void run() {
                // test code...
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Logger.e(TAG, e);
                }
                //--

                // send result
                AndroidBridge.callJSFunction(webview, callback, "success");
            }
        }).start();
    }

    public static void apiNotRecommended(final WebView webview, final JSONObject args, final String callback) {
        Logger.d(TAG, "apiNotRecommended(): args[" + args + "], callback[" + callback + "]");

        // sync
        // test code...
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        //--

        // send result
        AndroidBridge.callJSFunction(webview, callback, "success");
    }

    public static void apiTakePicture(final WebView webview, final JSONObject args, final String callback) {
        Logger.d(TAG, "apiTakePicture(): args[" + args + "], callback[" + callback + "]");

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // check permission
        TedPermission.with(webview.getContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        try {
                            AndroidBridge.setCallbackJSFunctionName(RequestCode.REQUEST_CODE_TAKE_A_PICTURE, callback);
                            File file = FileUtil.createCameraFile("jpg");
                            Uri output = UriUtil.fromFile(webview.getContext(), file);
                            if (!FileUtil.delete(file)) {
                                Logger.e(TAG, "delete failed...");
                            }
                            AndroidBridge.setExtraOutput(output);
                            IntentUtil.imageCaptureWithExtraOutput(webview.getContext(), RequestCode.REQUEST_CODE_TAKE_A_PICTURE, output);
                        } catch (Exception e) {
                            if (webview.getContext() instanceof Activity) {
                                DialogHelper.alert((Activity) webview.getContext(), e.getMessage());
                            }
                            Logger.e(TAG, e);
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Logger.e(TAG, "onPermissionDenied..." + deniedPermissions.toString());
                    }
                })
                .setPermissions(permissions.toArray(new String[] {}))
                .check();
    }

}
