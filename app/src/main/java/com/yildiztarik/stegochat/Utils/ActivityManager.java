package com.yildiztarik.stegochat.Utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityManager {
    private static ActivityManager instance;
    private Context context;

    private ActivityManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized ActivityManager getInstance(Context context) {
        if (instance == null) {
            instance = new ActivityManager(context);
        }
        return instance;
    }

    public void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void startActivityWithFinishOther(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void startActivityWithMessage(Class<? extends Activity> activityClass, String extraKey, String extraValue) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (extraKey != null && extraValue != null) {
            intent.putExtra(extraKey, extraValue);
        }

        context.startActivity(intent);
    }

    public void startActivityWithMessageWithFinishOther(Class<? extends Activity> activityClass, String extraKey, String extraValue) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (extraKey != null && extraValue != null) {
            intent.putExtra(extraKey, extraValue);
        }

        context.startActivity(intent);
    }
}

