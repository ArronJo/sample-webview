package com.snc.zero.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * File Utilities
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class FileUtil {
    //private static final String TAG = FileUtil.class.getSimpleName();

    private static boolean mkdirs(File dir) {
        if (null == dir) {
            return false;
        }
        if (dir.exists()) {
            return true;
        }
        return dir.mkdirs();
    }

    public static boolean delete(File file) {
        if (null == file || !file.exists()) {
            return false;
        }
        return file.delete();
    }

    public static String newFilename(String extension) {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return name + "."  + extension;
    }

    public static File createCameraFile(String extension) throws IOException {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/Camera/");
        if (!mkdirs(storageDir)) {
            throw new IOException("mkdirs failed...!!!!! " + storageDir);
        }

        String fileName = newFilename(extension);
        File newFile = new File(storageDir, fileName);
        if (newFile.createNewFile()) {
            return newFile;
        }
        return null;
    }

    public static File createCameraFileInExternalFiles(Context context, String extension) throws IOException {
        File storageDir = new File(context.getExternalFilesDir(null), "/Camera/");
        if (!mkdirs(storageDir)) {
            throw new IOException("mkdirs failed...!!!!! " + storageDir);
        }

        String fileName = newFilename(extension);
        File newFile = new File(storageDir, fileName);
        if (newFile.createNewFile()) {
            return newFile;
        }
        return null;
    }

}
