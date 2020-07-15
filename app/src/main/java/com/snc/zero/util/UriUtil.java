package com.snc.zero.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Uri Utilities
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class UriUtil {

    public static Uri fromFile(Context context, File file) throws PackageManager.NameNotFoundException {
        final Uri newUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newUri = FileProvider.getUriForFile(context, PackageUtil.getPackageName(context) + ".fileprovider", file);
        } else {
            newUri = Uri.fromFile(file);
        }
        return newUri;
    }

}
