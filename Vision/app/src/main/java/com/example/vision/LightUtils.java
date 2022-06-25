package com.example.vision;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class LightUtils {
    public static String FIRST_INSTALL = "first-install";

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }

    public static Intent emailIntent(String emailAddress, String subject, String text, String title) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("text/email");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, text);
        return Intent.createChooser(email, title);
    }

    public static Intent webIntent(String url) {
        Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        return link;
    }

    public static Intent navIntent(Context context, Class activity) {
        Intent navigate = new Intent(context, activity);
        return navigate;
    }

//    public static String getAppVersion() {
//        return BuildConfig.VERSION_NAME;
//    }

    //basically show a toast message on android
    public static void showToast(String msg, int time, Context context) {
        CharSequence text = msg;
        int duration = time;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static boolean permissionsGranted(Context context)
    {
        return ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) &&
            ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED));
    }

    public static void openPermissionSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + context.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}