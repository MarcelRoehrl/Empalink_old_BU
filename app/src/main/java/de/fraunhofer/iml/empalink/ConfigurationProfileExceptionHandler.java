package de.fraunhofer.iml.empalink;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.empatica.empalink.ConfigurationProfileException;

import de.fraunhofer.iml.empalink.Activities.NoSingleInternetErrorActivity;

public class ConfigurationProfileExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CPExceptionHandler";

    private Context context;
    private Class<?> nextActivity;

    public ConfigurationProfileExceptionHandler(Context context, Class<?> nextActivity) {
        this.context = context;
        this.nextActivity = nextActivity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "uncaughtException: " + ex);

        // Check if exception is of type ConfigurationProfileException
        if (ex instanceof ConfigurationProfileException) {
            // Set API_KEY_INVALID flag and restart activity
            Intent restart = new Intent(context, NoSingleInternetErrorActivity.class);
            context.startActivity(restart);
        }

        // Kill app and exit in any case
        System.exit(0);
    }
}