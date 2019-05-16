package com.snc.zero.util;

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

    private static boolean mkdirs(File dir) {
        if (null != dir) {
            if (dir.exists()) {
                return true;
            }
            return dir.mkdirs();
        }
        return false;
    }

    public static boolean delete(File file) {
        if (null != file && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static File createCameraFile(String extension) throws IOException {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/Camera/");
        if (!mkdirs(storageDir)) {
            throw new IOException("mkdirs failed...!!!!! " + storageDir);
        }

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File newFile = new File(storageDir, fileName + "." + extension);
        if (newFile.createNewFile()) {
            return newFile;
        }
        return null;
    }

}
