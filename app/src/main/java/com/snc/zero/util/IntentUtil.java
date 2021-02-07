package com.snc.zero.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.net.URISyntaxException;

import retrofit2.http.DELETE;

/**
 * Android Intent Utilities
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class IntentUtil {

    public static void imageCaptureWithExtraOutput(Context context, int requestCode, Uri output) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (null != output) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        }
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Chooser"), requestCode);
    }

    public static void view(Context context, Uri uri) throws ActivityNotFoundException {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void intentScheme(Context context, String urlString) throws URISyntaxException {
        Intent intent = Intent.parseUri(urlString, Intent.URI_INTENT_SCHEME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
            marketLaunch.setData(Uri.parse("market://details?id=" + intent.getPackage()));
            context.startActivity(marketLaunch);
        }
    }

    private static MediaScannerConnection scanner;

    public static void addGalleryPic(Context context, File file) {
        MediaScannerConnection.MediaScannerConnectionClient client = new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
                if (null == scanner) {
                    return;
                }
                scanner.scanFile(file.getAbsolutePath(), null);
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {

            }
        };

        if (null == scanner) {
            scanner = new MediaScannerConnection(context, client);
        }
        scanner.connect();

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.parse("file://" + file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
