package com.snc.zero.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.snc.zero.log.Logger;

import java.io.File;

public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    public static Bitmap decodeBitmap(Context context, File file) throws Exception {
        return decodeBitmap(context, UriUtil.fromFile(context, file));
    }
    public static Bitmap decodeBitmap(Context context, Uri uri) throws Exception {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                return ImageDecoder.decodeBitmap(source);
            } else {
                //noinspection deprecation
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            }
        } catch (Exception e) {
            Logger.e(TAG, e);
            throw e;
        }
    }
}
