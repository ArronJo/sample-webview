package com.snc.zero.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Build;
import android.provider.MediaStore;

import com.snc.zero.log.Logger;

import java.io.File;
import java.io.IOException;

public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    public static Bitmap decodeBitmap(Context context, File file) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), UriUtil.fromFile(context, file));
                return ImageDecoder.decodeBitmap(source);
            } else {
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), UriUtil.fromFile(context, file));
            }
        } catch (PackageManager.NameNotFoundException | IOException e) {
            Logger.e(TAG, e);
        }
        return null;
    }
}
