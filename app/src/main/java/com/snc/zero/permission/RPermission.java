package com.snc.zero.permission;

import android.content.Context;

import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import androidx.annotation.NonNull;

public class RPermission {

    public static RPermission with(Context context) {
        return new RPermission(context);
    }

    private Context context;
    private String[] permissions;
    private PermissionListener listener;

    public RPermission(Context context) {
        this.context = context;
    }

    public RPermission setPermissionListener(PermissionListener listener) {
        this.listener = listener;
        return this;
    }

    public RPermission setPermissions(List<String> permissions) {
        this.permissions = permissions.toArray(new String[] {});
        return this;
    }

    public RPermission setPermissions(String[] permissions) {
        this.permissions = permissions;
        return this;
    }

    public void check() {
        TedPermission.with(context)
                .setPermissionListener(new com.gun0912.tedpermission.PermissionListener() {

                    @Override
                    public void onPermissionGranted() {
                        if (null != listener) {
                            listener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        if (null != listener) {
                            listener.onPermissionDenied(deniedPermissions);
                        }
                    }
                })
                .setPermissions(permissions)
                .check();
    }

    public static boolean isGranted(Context context, @NonNull String... permissions) {
        return TedPermission.isGranted(context, permissions);
    }

}
