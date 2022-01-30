package com.snc.sample.webview.bridge.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import com.snc.sample.webview.bridge.AndroidBridge;
import com.snc.zero.dialog.DialogBuilder;
import com.snc.zero.imageprocess.ImageProcess;
import com.snc.zero.log.Logger;
import com.snc.zero.media.ImageProvider;
import com.snc.zero.permission.RPermission;
import com.snc.zero.permission.RPermissionListener;
import com.snc.zero.requetcode.RequestCode;
import com.snc.zero.util.BitmapUtil;
import com.snc.zero.util.EnvUtil;
import com.snc.zero.util.FileUtil;
import com.snc.zero.util.IntentUtil;
import com.snc.zero.util.StringUtil;
import com.snc.zero.util.UriUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"InstantiationOfUtilityClass", "unused", "RedundantSuppression"})
public class PluginCamera implements Plugin {
    private static final String TAG = PluginCamera.class.getSimpleName();

    private static final PluginCamera mInstance = new PluginCamera();
    public static PluginCamera getInstance() {
        return mInstance;
    }

    /////////////////////////////////////////////////
    // Method

    public static void takePicture(WebView webview, JSONObject args, String cbId) {
        Logger.i(TAG, "[WEBVIEW] takePicture : args[" + args + "], cbId[" + cbId + "]");
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // check permission
        RPermission.with(webview.getContext())
                .setPermissions(permissions)
                .setPermissionListener(new RPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Logger.i(TAG, "[WEBVIEW] onPermissionGranted()");
                        try {
                            AndroidBridge.setCallbackJSFunctionName(RequestCode.REQUEST_TAKE_A_PICTURE, cbId);

                            File storageDir = EnvUtil.getMediaDir(webview.getContext(), "image");
                            String filename = FileUtil.newFilename("jpg");
                            File file = FileUtil.createFile(storageDir, filename);
                            if (null == file) {
                                throw new NullPointerException();
                            }

                            Uri output = UriUtil.fromFile(webview.getContext(), file);

                            if (!FileUtil.delete(file)) {
                                Logger.e(TAG, "[WEBVIEW] delete failed...");
                            }

                            AndroidBridge.setExtraOutput(file);
                            IntentUtil.imageCaptureWithExtraOutput(webview.getContext(), RequestCode.REQUEST_TAKE_A_PICTURE, output);

                        } catch (Exception e) {
                            Logger.e(TAG, e);
                            DialogBuilder.with(webview.getContext())
                                    .setMessage(e.toString())
                                    .show();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Logger.e(TAG, "[WEBVIEW] onPermissionDenied()..." + deniedPermissions.toString());
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<String> deniedPermissions) {
                        Logger.e(TAG, "[WEBVIEW] onPermissionRationaleShouldBeShown()..." + deniedPermissions.toString());
                    }
                })
                .check();
    }


    /////////////////////////////////////////////////
    // Callback

    //++ [[START] Take a picture]
    public static void onActivityResultTakePicture(WebView webview, int requestCode, int resultCode, Intent data) {
        Logger.i(TAG, "[WEBVIEW] onActivityResultTakePicture(): requestCode[" + requestCode + "]  resultCode[" + resultCode + "] data[" + data + "]");
        if (Activity.RESULT_OK != resultCode) {
            return;
        }

        try {
            if (null != AndroidBridge.getExtraOutput(false)) {
                Logger.i(TAG, "[WEBVIEW] onActivityResultTakePicture(): REQUEST_CODE_TAKE_A_PICTURE (with ExtraOutput)");
                File documentFile = AndroidBridge.getExtraOutput(true);
                Uri uri = UriUtil.fromFile(webview.getContext(), documentFile);

                Bitmap bitmap = BitmapUtil.decodeBitmap(webview.getContext(), documentFile, 2048);
                DialogBuilder.with(webview.getContext())
                        .setBitmap(bitmap)
                        .show();

                String base64String = ImageProcess.toBase64String(documentFile);
                try {
                    uri = ImageProvider.insert(webview.getContext(), documentFile);
                    if (EnvUtil.isFilesDir(webview.getContext(), documentFile)) {
                        if (null != uri) {
                            if (!documentFile.delete()) {
                                Logger.e(TAG, "delete failed...");
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    Logger.e(TAG, e);
                }

                AndroidBridge.callJSFunction(webview, AndroidBridge.getCallbackJSFunctionName(requestCode), StringUtil.nvl(uri, ""), base64String);
            }
            else if (null != data) {
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

                AndroidBridge.callJSFunction(webview, AndroidBridge.getCallbackJSFunctionName(requestCode), params);

                if (null != bitmap) {
                    DialogBuilder.with(webview.getContext())
                            .setBitmap(bitmap)
                            .show();
                }
            }

        } catch (Exception e) {
            DialogBuilder.with(webview.getContext())
                    .setMessage(e.toString())
                    .show();
        }
    }
    //-- [[E N D] Take a picture]

}
