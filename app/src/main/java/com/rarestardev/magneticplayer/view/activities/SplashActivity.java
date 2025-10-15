package com.rarestardev.magneticplayer.view.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.settings.ThemeSettings;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.utilities.NavigationBarUtils;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1000;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ThemeSettings themeSettings = new ThemeSettings(this);
        themeSettings.doInitialization();

        NavigationBarUtils.setNavigationBarColor(this, getColor(R.color.top_panel_background_color_end));
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkAndRequestPermissions();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(Constants.appLog, "API 33 ABOVE (android 13)");
            // For Android 13 (API 33) and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // Request READ_MEDIA_AUDIO permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                        PERMISSION_REQUEST_CODE);

            } else startApp();

        } else {
            Log.d(Constants.appLog, "API 32 BELOW (android 12)");
            // For Android 12 (API 32) and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request READ_EXTERNAL_STORAGE permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                startApp();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Constants.appLog, "Read Audio file granted");
                startApp();
            } else {
                Log.e(Constants.appLog, "Read Audio file denied");
                showPermissionAlertDialog();
            }
        } else {
            Log.e(Constants.appLog, "PERMISSION_REQUEST_CODE Failed");
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showPermissionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this)
                .setTitle("Attention")
                .setMessage("Sorry, if you don't grant access, the app won't run!")
                .setPositiveButton("Request", (dialog, which) -> {
                    checkAndRequestPermissions();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setIcon(getDrawable(R.drawable.ic_warning))
                .setCancelable(false);

        builder.show();
    }

    private void startApp() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 3000);
    }
}