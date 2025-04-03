package com.example.retrofit2.utils;

import android.Manifest;
import android.os.Build;
import androidx.annotation.RequiresApi;

public class PermissionUtils {
    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    /**
     * Trả về danh sách quyền phù hợp với phiên bản Android hiện tại.
     */
    public static String[] getStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return storage_permissions_33;
        } else {
            return storage_permissions;
        }
    }
}
