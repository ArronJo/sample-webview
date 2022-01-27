package com.snc.zero.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RPermission {
    //private static final String TAG = RPermission.class.getSimpleName();

    public static RPermission with(Context context) {
        return new RPermission(context);
    }

    private final Context context;
    private String[] permissions;
    private RPermissionListener listener;

    public RPermission(Context context) {
        this.context = context;
    }

    public RPermission setPermissionListener(RPermissionListener listener) {
        this.listener = listener;
        return this;
    }

    public RPermission setPermissions(List<String> permissions) {
        this.permissions = permissions.toArray(new String[] {});
        return this;
    }

    public RPermission setPermissions(String...permissions) {
        this.permissions = permissions;
        return this;
    }

    public void check() {
        TedPermission.create()
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (null != listener) {
                            listener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        if (null != listener) {
                            if (!shouldShowRequestPermissionRationale(deniedPermissions)) {
                                listener.onPermissionDenied(deniedPermissions);
                            } else {
                                listener.onPermissionDenied(deniedPermissions);
                            }
                        }
                    }
                })
                //.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(permissions)
                .check();
    }

    public static boolean isGranted(Context context, @NonNull List<String> permissions) {
        return isGranted(context, permissions.toArray(new String[] {}));
    }

    public static boolean isGranted(Context context, @NonNull String... permissions) {
        for (String permission : permissions) {
            if (isDenied(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isGranted(Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isDenied(Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public boolean shouldShowRequestPermissionRationale(List<String> needPermissions) {
        if (null == needPermissions) {
            return false;
        }

        for (String permission : needPermissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) this.context, permission)) {
                return false;
            }
        }
        return true;
    }
}
