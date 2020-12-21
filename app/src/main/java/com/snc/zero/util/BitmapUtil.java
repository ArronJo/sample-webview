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

    public static Bitmap decodeBitmap(Context context, File file, int maxImageWidth) throws Exception {
        return decodeBitmap(context, UriUtil.fromFile(context, file), maxImageWidth);
    }
    public static Bitmap decodeBitmap(Context context, Uri uri, int maxImageWidth) throws Exception {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                return ImageDecoder.decodeBitmap(source, (decoder, info, source1) -> {
                    if (maxImageWidth <= 0) {
                        return;
                    }

                    int sampleSize = getSampleSize(info.getSize().getWidth(), info.getSize().getHeight(), maxImageWidth);
                    decoder.setTargetSampleSize(sampleSize);
                });
            } else {
                //noinspection deprecation
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            }
        } catch (Exception e) {
            Logger.e(TAG, e);
            throw e;
        }
    }

    public static int getSampleSize(int outWidth, int outHeight, int maxImageWidth) {
        int inSampleSize = 1;
        if (outHeight > maxImageWidth || outWidth > maxImageWidth) {
            inSampleSize = (int) Math.pow(2,
                    (int) Math.round(
                            (Math.log(maxImageWidth / (double) Math.max(outHeight, outWidth)))
                                    / Math.log(0.5)
                    )
            );
        }
        return inSampleSize;
    }

}
