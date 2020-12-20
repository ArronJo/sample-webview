package com.snc.zero.download;

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
import android.widget.Toast;

import com.snc.zero.dialog.DialogHelper;
import com.snc.zero.log.Logger;
import com.snc.zero.mimetype.MimeType;

/**
 * Download Manager
 *
 * @author mcharima5@gmail.com
 * @since 2020
 */
public class CSDownloadManager {
    private static final String TAG = CSDownloadManager.class.getSimpleName();

    private long mDownloadId;

    public void download(Context context, String url) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }

            int lastIdx = url.lastIndexOf("/");
            String fileName = url.substring(lastIdx);
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
                            Toast.makeText(context, "download success", Toast.LENGTH_SHORT).show();
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            Toast.makeText(context, "download failed", Toast.LENGTH_SHORT).show();
                        }

                        unregisterDownloadReceiver(context, this);
                    }
                }
            }
        }, intentFilter);
    }

    private void unregisterDownloadReceiver(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

}