package com.snc.zero.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.snc.zero.dialog.DialogBuilder;
import com.snc.zero.requetcode.RequestCode;
import com.snc.zero.util.DateTimeFormat;
import com.snc.zero.util.EnvUtil;
import com.snc.zero.util.StringUtil;
import com.snc.zero.util.UriUtil;
import com.snc.zero.webview.listener.FileChooserListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * WebView FileChooser Listener
 *
 * @author mcharima5@gmail.com
 * @since 2020
 */
public class CSFileChooserListener implements FileChooserListener {
    private static final String ALL_TYPE = "image/*|audio/*|video/*";

    private final Context context;

    private ValueCallback<Uri> filePathCallbackNormal;

    private ValueCallback<Uri[]> filePathCallbackLollipop;
    private final int IMAGE = 0;
    private final int AUDIO = 1;
    private final int VIDEO = 2;
    private Uri[] mediaURIs;


    /////////////////////////////////////////////////
    // Constructor
    /////////////////////////////////////////////////

    public CSFileChooserListener(Context context) {
        this.context = context;
    }


    /////////////////////////////////////////////////
    // Open FileChooser
    /////////////////////////////////////////////////

    @SuppressWarnings({"unused", "RedundantSuppression"})
    @Override
    public void onOpenFileChooserNormal(WebView webView, ValueCallback<Uri> filePathCallback, String acceptType) {
        this.filePathCallbackNormal = filePathCallback;
        openIntentChooser(acceptType);
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    @Override
    public void onOpenFileChooserLollipop(WebView webView, ValueCallback<Uri[]> filePathCallback, String[] acceptTypes) {
        this.filePathCallbackLollipop = filePathCallback;
        openIntentChooser(acceptTypes);
    }

    private void openIntentChooser(String[] acceptTypes) {
        String acceptType = "";

        for (String type : acceptTypes) {
            if (StringUtil.isEmpty(type)) {
                continue;
            }
            if (!type.startsWith("image/")
                    && !type.startsWith("audio/")
                    && !type.startsWith("video/")
                    && !type.startsWith("application/")) {
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

        if (type.isEmpty() || "*/*".equalsIgnoreCase(type)) {
            type = ALL_TYPE;
        }

        try {
            mediaURIs = new Uri[3];

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                List<Intent> intentList = new ArrayList<>();

                String fileName = DateTimeFormat.format(new Date(), "yyyyMMdd_HHmmss");

                if (type.contains("image/")) {
                    mediaURIs[IMAGE] = UriUtil.fromFile(context, new File(EnvUtil.getMediaDir(context, "image"), fileName + ".jpg"));

                    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaURIs[IMAGE]);
                    intentList.add(intent);
                }
                if (type.contains("audio/")) {
                    mediaURIs[AUDIO] = UriUtil.fromFile(context, new File(EnvUtil.getMediaDir(context, "audio"), fileName + ".m4a"));

                    final Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaURIs[AUDIO]);
                    intentList.add(intent);
                }
                if (type.contains("video/")) {
                    mediaURIs[VIDEO] =  UriUtil.fromFile(context, new File(EnvUtil.getMediaDir(context, "video"), fileName + ".mp4"));

                    final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaURIs[VIDEO]);
                    intentList.add(intent);
                }

                // Intent Chooser
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (ALL_TYPE.equals(type)) {
                    intent.setType("*/*");
                } else {
                    intent.setType(type);
                }

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
            Timber.e(e);

            DialogBuilder.with(context)
                    .setMessage(e.toString())
                    .show();
        }
    }


    /////////////////////////////////////////////////
    // onActivityResult
    /////////////////////////////////////////////////

    public void onActivityResultFileChooserNormal(int requestCode, int resultCode, Intent data) {
        Timber.i("[WEBVIEW] onActivityResultFileChooserNormal(): requestCode[" + requestCode + "]  resultCode[" + resultCode + "] data[" + data + "]");

        if (null == filePathCallbackNormal) {
            Timber.i("[WEBVIEW] onActivityResultNormal(): filePathCallbackNormal is null !!!");
            return;
        }

        if (Activity.RESULT_OK != resultCode) {
            filePathCallbackNormal.onReceiveValue(null);
            filePathCallbackNormal = null;
            return;
        }

        Uri result = null == data ? null : data.getData();
        filePathCallbackNormal.onReceiveValue(result);
        filePathCallbackNormal = null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResultFileChooserLollipop(int requestCode, int resultCode, Intent data) {
        Timber.i("[WEBVIEW] onActivityResultFileChooserLollipop(): requestCode[" + requestCode + "]  resultCode[" + resultCode + "] data[" + data + "]");

        if (null == filePathCallbackLollipop) {
            Timber.i("[WEBVIEW] onActivityResultLollipop(): filePathCallbackLollipop is null !!!");
            return;
        }

        if (Activity.RESULT_OK != resultCode) {
            filePathCallbackLollipop.onReceiveValue(null);
            filePathCallbackLollipop = null;
            return;
        }

        try {
            if (null != data) {
                filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            } else {
                List<Uri> results = new ArrayList<>();

                if (null != mediaURIs) {
                    if (null != mediaURIs[IMAGE]) {
                        results.add(mediaURIs[IMAGE]);
                    }
                    if (null != mediaURIs[AUDIO]) {
                        results.add(mediaURIs[AUDIO]);
                    }
                    if (null != mediaURIs[VIDEO]) {
                        results.add(mediaURIs[VIDEO]);
                    }
                }

                filePathCallbackLollipop.onReceiveValue(results.toArray(new Uri[]{}));
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            filePathCallbackLollipop = null;
            mediaURIs = null;
        }
    }

}
