package com.snc.sample.webview.asynctask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.snc.sample.webview.network.DynamicUrlService;
import com.snc.zero.log.Logger;
import com.snc.zero.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Download AsyncTask
 *
 * @author mcharima5@gmail.com
 * @since 2020
 */
public class DownloadAsyncTask extends ThreadTask<String, String, String> {
    private static final String TAG = DownloadAsyncTask.class.getSimpleName();

    private static final String CODE_SUCCESS = "SUCCESS";
    private static final String CODE_FAILED = null;

    @SuppressLint("StaticFieldLeak")
    private final Activity activity;
    private final DownloadAsyncTask task;

    public DownloadAsyncTask(Activity activity) {
        this.activity = activity;
        this.task = this;
    }

    @Override
    protected void onPreExecute() {
        Logger.i(TAG, "[WEBVIEW] onPreExecute");
        Toast.makeText(this.activity, "downloading...", Toast.LENGTH_SHORT).show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        Logger.i(TAG, "[WEBVIEW] doInBackground");

        if (!checkPermission(this.activity, params)) {
            this.task.cancel();
            return null;
        }

        try {
            return downloadIt(params);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(final String... progress) {
        Logger.i(TAG, "[WEBVIEW] onProgressUpdate..." + progress[1]);
        activity.runOnUiThread(() -> {
            //TODO::
        });
    }

    @Override
    protected void onPostExecute(String result) {
        Logger.i(TAG, "[WEBVIEW] onPostExecute..." + result);

        if (CODE_SUCCESS.equals(result)) {
            activity.runOnUiThread(() -> Toast.makeText(activity, "downloaded", Toast.LENGTH_SHORT).show());
        }

        super.onPostExecute(result);
    }

    private boolean checkPermission(final Activity activity, final String... params) {
        Logger.i(TAG, "[WEBVIEW] checkPermission");

        if (TedPermission.isGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Logger.i(TAG, "[WEBVIEW] permission granted");
            return true;
        }

        TedPermission.with(activity)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Logger.i(TAG, "[WEBVIEW] onPermissionGranted()");

                        task.cancel();

                        try {
                            DownloadAsyncTask task = new DownloadAsyncTask(activity);
                            task.execute(params[0], params[1], "1");
                        } catch (Exception e) {
                            Logger.e(TAG, e);
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Logger.e(TAG, "[WEBVIEW] onPermissionDenied()..." + deniedPermissions.toString());

                        task.cancel();
                    }
                })
                .setPermissions(new String[]{
                        // Dangerous Permission
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                })
                .check();

        return false;
    }

    private String downloadIt(String... params) throws IOException {
        String url = params[0];
        String fileName = params[1];

        Logger.i(TAG, "[WEBVIEW] downloadIt..." + url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your.api.url/")   // Base URL required.
                .build();

        DynamicUrlService service = retrofit.create(DynamicUrlService.class);
        Call<ResponseBody> call = service.get(url);
        Response<ResponseBody> response = call.execute();

        Logger.i(TAG, "[WEBVIEW] onResponse() = response code is " + response.code());

        ResponseBody body;
        if (response.code() < 400) {
            body = response.body();
        } else {
            body = response.errorBody();
        }
        Logger.i(TAG, "[WEBVIEW] onResponse() = " + response + " \n\n ");

        if (writeFile(body, fileName)) {
            return CODE_SUCCESS;
        }
        return CODE_FAILED;
    }

    private boolean writeFile(ResponseBody body, String fileName) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = body.byteStream();
            String targetFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File file = new File(targetFolder, fileName);
            output = new FileOutputStream(file);

            long lengthOfFile = body.contentLength();
            byte[] data = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                int p = (int) ((total * 100) / lengthOfFile);
                publishProgress("progress", Integer.toString(p));
                output.write(data, 0, count);
            }

            output.flush();
            return true;

        } catch (Exception e) {
            Logger.e(TAG, e);
        } finally {
            IOUtil.closeQuietly(output);
            IOUtil.closeQuietly(input);
        }
        return false;
    }

}
