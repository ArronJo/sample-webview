package com.snc.zero.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetUtil {
    private static final String TAG = AssetUtil.class.getSimpleName();

    public static void copyAssetToFile(Context context, String srcAssetsFilePath, File destFolderFile) {
        InputStream is = null;
        OutputStream os = null;
        try {
            AssetManager am = context.getResources().getAssets();
            is = am.open(srcAssetsFilePath);

            if (!destFolderFile.exists()) {
                if (!destFolderFile.mkdirs()) {
                    Log.e(TAG, "[ERROR]] mkdir faiIed !!!!!  path = " + destFolderFile);
                    return;
                }
            }

            File srcAssetsFile = new File(srcAssetsFilePath);
            File destFile = new File(destFolderFile, srcAssetsFile.getName());
            if (destFile.exists()) {
                return;
            }
            os = new FileOutputStream(destFile);

            FileUtil.copyFile(is, os);
        }
        catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
        finally {
            IOUtil.closeQuietly(is);
            IOUtil.closeQuietly(os);
        }
    }

}
