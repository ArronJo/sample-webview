package com.snc.zero.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Environment Utilities
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class EnvUtil {

    public static File getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(null);
    }

    public static File getMediaDir(Context context, String type) {
        File storageDir = getExternalFilesDir(context);

        if ("image".equalsIgnoreCase(type) || "video".equalsIgnoreCase(type)) {
            return new File(new File(storageDir, Environment.DIRECTORY_DCIM), "Camera");
        }
        else if ("audio".equalsIgnoreCase(type)) {
            return new File(storageDir, "Voice Recorder");
        }
        return null;
    }

}
