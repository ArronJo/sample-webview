package com.snc.sample.webview.asynctask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
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
 * AsyncTask
 *
 * @author mcharima5@gmail.com
 * @since 2020
 */
public class DownloadAsyncTask extends AsyncTask<String, String, String> {
    private static final String TAG = DownloadAsyncTask.class.getSimpleName();

    private static final String CODE_SUCCESS = "SUCCESS";
    private static final String CODE_FAILED = null;

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private DownloadAsyncTask task;

    public DownloadAsyncTask(Activity activity) {
        this.activity = activity;
        this.task = this;
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "[WEBVIEW] onPreExecute");
        Toast.makeText(this.activity, "downloading...", Toast.LENGTH_SHORT).show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.i(TAG, "[WEBVIEW] doInBackground");
        // 권한 체크
        if (!checkPermission(this.activity, params)) {
            this.task.cancel(true);
            return null;
        }

        try {
            return downloadIt2(params);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(final String... progress) {
        Log.i(TAG, "[WEBVIEW] onProgressUpdate..." + progress[1]);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO::
            }
        });
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "[WEBVIEW] onPostExecute..." + result);

        if (CODE_SUCCESS.equals(result)) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "downloaded", Toast.LENGTH_SHORT).show();
                }
            });
        }

        super.onPostExecute(result);
    }

    private boolean checkPermission(final Activity activity, final String... params) {
        Log.i(TAG, "[WEBVIEW] checkPermission");
        // 권한 체크
        if (TedPermission.isGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.i(TAG, "[WEBVIEW] permission granted...");
            return true;
        }

        // 권한 요청
        TedPermission.with(activity)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Log.i(TAG, "[WEBVIEW] onPermissionGranted...");

                        // 기존 Task 중단
                        task.cancel(true);

                        // 새 다운로드 Task 실행
                        try {
                            DownloadAsyncTask task = new DownloadAsyncTask(activity);
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params[0], params[1], "1");
                        } catch (Exception e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Log.i(TAG, "[WEBVIEW] onPermissionDenied..." + deniedPermissions.toString());

                        // 기존 Task 중단
                        task.cancel(true);
                    }
                })
                .setPermissions(new String[]{
                        // Dangerous Permission
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                })
                .check();

        return false;
    }

    private String downloadIt2(String... params) throws IOException {
        String url = params[0];
        String fileName = params[1];

        Log.i(TAG, "[WEBVIEW] downloadIt2..." + url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your.api.url/")   // Base URL required.
                .build();

        DynamicUrlService service = retrofit.create(DynamicUrlService.class);
        Call<ResponseBody> call = service.get(url);
        Response<ResponseBody> response = call.execute();
        if (null == response) {
            Log.i(TAG, "onResponse() = response is null.");
            return null;
        }

        Log.i(TAG, "onResponse() = response code is " + response.code());

        ResponseBody body;
        if (response.code() < 400) {
            body = response.body();
        } else {
            body = response.errorBody();
        }
        Log.i(TAG, "onResponse() = " + response + " \n\n ");

        if (writeFile(body, fileName)) {
            return CODE_SUCCESS;
        }
        return CODE_FAILED;
    }

    private boolean writeFile(ResponseBody body, String fileName) {
        InputStream input = null;
        OutputStream output = null;
        try {
            long lengthOfFile = body.contentLength();
            Logger.d(TAG, "Length of file: " + lengthOfFile);

            input = body.byteStream();
            String targetFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File file = new File(targetFolder, fileName );
            output = new FileOutputStream(file);

            byte[] data = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                int p = (int) ((total * 100) / lengthOfFile);
                publishProgress("progress", Integer.toString(p));
                output.write(data, 0, count);
                Logger.e(TAG, "output write = " + total);
            }

            output.flush();

        } catch (Exception e) {
            Logger.e(TAG, e);
        } finally {
            IOUtil.closeQuietly(output);
            IOUtil.closeQuietly(input);
        }
        return false;
    }

}
