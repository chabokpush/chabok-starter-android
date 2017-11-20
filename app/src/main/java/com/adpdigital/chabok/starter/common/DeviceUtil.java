package com.adpdigital.chabok.starter.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.adpdigital.chabok.starter.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class DeviceUtil {

    public static String getDeviceManufacturer(){
        String deviceMan = android.os.Build.MANUFACTURER;
        Log.d("deviceMan", "" + deviceMan);
        return deviceMan;
    }

    public static void ifHuaweiAlert(Activity ctx, String deviceManufacturer) {
        ifHuaweiAlert(
                ctx,
                "برنامه‌های محافظت شده",
                String.format("برنامه %s برای کارکرد درست می‌بایست در لیست برنامه‌های محافظت شده فعال شود.%n",
                        ctx.getString(R.string.app_name)),
                deviceManufacturer
        );
    }

    public static void ifHuaweiAlert(final Activity ctx, String title, String message, final String deviceManufacturer) {
        final SharedPreferences settings = ctx
                .getSharedPreferences("ProtectedApps", ctx.MODE_PRIVATE);

        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            boolean result;
            final SharedPreferences.Editor editor = settings.edit();
            if (deviceManufacturer.contains("huawei")) {
                Intent intent = new Intent();
                intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
                result = isCallable(ctx, intent);
            } else {
                result = true;
            }
            if (result) {
                LayoutInflater inflator = LayoutInflater.from(ctx);
                View view = inflator.inflate(R.layout.protected_apps_dialog, null);
                TextView messageTextView = (TextView) view.findViewById(R.id.message);
                TextView titleTextView = (TextView) view.findViewById(R.id.title);
                messageTextView.setText(message);
                titleTextView.setText(title);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setCancelable(false)
                        .setView(view);
                if (deviceManufacturer.contains("huawei")) {
                    dialog.setPositiveButton(R.string.goto_protected, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (deviceManufacturer.contains("huawei")) {
                                editor.putBoolean(saveIfSkip, true);
                                editor.apply();
                                huaweiProtectedApps(ctx);
                            }
                        }
                    })
                            .setNegativeButton(R.string.remindme, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editor.putBoolean(saveIfSkip, false);
                                    editor.apply();
                                }
                            })
                            .show();
                } else {
                    dialog.setPositiveButton(R.string.menu_item_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putBoolean(saveIfSkip, true);
                            editor.apply();
                            dialog.dismiss();
                        }
                    })
                            .show();
                }
            }
        }
    }

    private static boolean isCallable(Context ctx, Intent intent) {
        List<ResolveInfo> list = ctx
                .getPackageManager()
                .queryIntentActivities(
                        intent,
                        PackageManager.MATCH_DEFAULT_ONLY
                );
        return list.size() > 0;
    }

    private static void huaweiProtectedApps(Context ctx) {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial(ctx);
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private static String getUserSerial(Context ctx) {
        //noinspection ResourceType
        Object userManager = ctx.getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }

    public static float getScreenWidth(Activity context){

        WindowManager windowManager = context.getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth_in_pixel = displayMetrics.widthPixels;
        return screenWidth_in_pixel;
    }
}
