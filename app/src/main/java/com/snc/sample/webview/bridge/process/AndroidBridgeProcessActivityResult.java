package com.snc.sample.webview.bridge.process;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import com.snc.sample.webview.bridge.AndroidBridge;
import com.snc.zero.dialog.DialogBuilder;
import com.snc.zero.log.Logger;
import com.snc.zero.media.ImageProvider;
import com.snc.zero.util.BitmapUtil;
import com.snc.zero.util.EnvUtil;
import com.snc.zero.util.StringUtil;
import com.snc.zero.util.UriUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * WebView JavaScript Interface onActivityResult
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class AndroidBridgeProcessActivityResult {
    private static final String TAG = AndroidBridgeProcessActivityResult.class.getSimpleName();

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

                AndroidBridge.callJSFunction(webview, AndroidBridge.getCallbackJSFunctionName(requestCode), StringUtil.nvl(uri, ""));
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
