package com.c2c.tictactoegame;



import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfoHelper {

    public static JSONObject getDeviceInfo(Context context) {
        JSONObject deviceInfo = new JSONObject();
        try {
            deviceInfo.put("model", Build.MODEL);
            deviceInfo.put("manufacturer", Build.MANUFACTURER);
            deviceInfo.put("android_sdk_int", Build.VERSION.SDK_INT);
            deviceInfo.put("android_version_release", Build.VERSION.RELEASE);
            deviceInfo.put("android_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

            // Screen Resolution
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
            deviceInfo.put("screen_width_pixels", displayMetrics.widthPixels);
            deviceInfo.put("screen_height_pixels", displayMetrics.heightPixels);
            deviceInfo.put("density_dpi", displayMetrics.densityDpi);

            // App Version
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                deviceInfo.put("app_version_name", pInfo.versionName);
                deviceInfo.put("app_version_code", pInfo.versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return deviceInfo;
    }
}
