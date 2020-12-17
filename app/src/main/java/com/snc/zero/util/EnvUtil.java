package com.snc.zero.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

/**
 * Environment Utilities
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class EnvUtil {

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    private static File getExternalStorageDir() {
        return Environment.getExternalStorageDirectory();	// Deprecated in API 29
    }

    //@Deprecated
    //private static File getExternalStoragePublicDir(String type) {
    //    return Environment.getExternalStoragePublicDirectory(type);	// Deprecated in API 29
    //}

    public static File getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(null);
    }

    public static File getExternalDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return EnvUtil.getExternalFilesDir(context);
        } else {
            //noinspection deprecation
            return EnvUtil.getExternalStorageDir();
        }
    }

    public static File getMediaDir(Context context, String type) {
        File storageDir = getExternalDir(context);

        if ("image".equalsIgnoreCase(type) || "video".equalsIgnoreCase(type)) {
            return new File(new File(storageDir, Environment.DIRECTORY_DCIM), "Camera");
        }
        else if ("audio".equalsIgnoreCase(type)) {
            return new File(storageDir, "Voice Recorder");
        }
        return null;
    }

    //public static File getDownloadDir(Context context) {
    //    return new File(new File(getExternalDir(context), Environment.DIRECTORY_DOWNLOADS), "Camera");
    //}
}
