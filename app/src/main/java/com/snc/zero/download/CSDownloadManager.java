package com.snc.zero.download;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.snc.zero.dialog.DialogHelper;
import com.snc.zero.log.Logger;
import com.snc.zero.mimetype.MimeType;
import com.snc.zero.permission.PermissionListener;
import com.snc.zero.permission.RPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * Download Manager
 *
 * @author mcharima5@gmail.com
 * @since 2020
 */
public class CSDownloadManager {
    private static final String TAG = CSDownloadManager.class.getSimpleName();

    private long mDownloadId;

    public void download(Context context, String urlString) {
        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);    // android:maxSdkVersion="28"
        }

        if (RPermission.isGranted(context, permissions.toArray(new String[] {}))) {
            downloadIt(context, urlString);
            return;
        }

        // check permission
        RPermission.with(context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Logger.i(TAG, "[CSDownloadManager] onPermissionGranted()");
                        downloadIt(context, urlString);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Logger.e(TAG, "[CSDownloadManager] onPermissionDenied()..." + deniedPermissions.toString());

                        DialogHelper.toast(context, "Permission denied !!!");
                    }
                })
                .setPermissions(permissions.toArray(new String[] {}))
                .check();
    }

    private void downloadIt(Context context, String urlString) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlString));
            //request.allowScanningByMediaScanner();    // Deprecated
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            int lastIdx = urlString.lastIndexOf("/");
            String fileName = urlString.substring(lastIdx);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setMimeType(MimeType.getMimeFromFileName(fileName));

            registerDownloadReceiver(context);

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            mDownloadId = manager.enqueue(request);

        } catch (Exception e) {
            Logger.e(TAG, e);
            DialogHelper.alert((Activity) context, e.toString());
        }
    }

    private void registerDownloadReceiver(Context context) {
        try {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (mDownloadId == id) {
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(id);
                            Cursor cursor = manager.query(query);
                            if (!cursor.moveToFirst()) {
                                return;
                            }

                            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            int status = cursor.getInt(columnIndex);
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                DialogHelper.toast(context, "download success");
                            } else if (status == DownloadManager.STATUS_FAILED) {
                                DialogHelper.toast(context, "download failed");
                            }

                            unregisterDownloadReceiver(context, this);
                        }
                    }
                }
            }, intentFilter);

        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    private void unregisterDownloadReceiver(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

}