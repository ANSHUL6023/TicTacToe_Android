package com.c2c.tictactoegame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.util.Log;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

public class OfflineGameMenuActivity extends AppCompatActivity implements View.OnTouchListener {


    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int ITEM_DELAY = 300;

    public int SCREEN_SIZE;
    public int SET_TRANSLATE;
    private boolean animationStarted = false;

    private GifImageView settingsGifView;
    private Button WithAFriendBtn , WithAi;

    // Tag for logging
    private static final String TAG = "OfflineGameMenuActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game_menu);

        // Start of Backend Integration
        try {
            JSONObject deviceInfo = DeviceInfoHelper.getDeviceInfo(this);
            Log.d(TAG, "Captured Device Info: " + (deviceInfo != null ? deviceInfo.toString() : "NULL"));

            if (deviceInfo != null) {
                ApiClient apiClient = new ApiClient();
                apiClient.sendDeviceInfo(deviceInfo); // This will now trigger a GET request
                Log.d(TAG, "Attempted to send device info to backend using GET.");
            } else {
                Log.e(TAG, "Device info JSON was null. Not sending to backend.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during device info collection or sending: " + e.getMessage(), e);
        }

        settingsGifView  = (GifImageView) findViewById(R.id.seting_gifview_offline_menu);
        WithAFriendBtn = (Button) findViewById(R.id.btn_choice2_offline_menu);
        WithAi = (Button) findViewById(R.id.btn_choice1_offline_menu);

        Drawable drawable = settingsGifView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        }

        SCREEN_SIZE =getScreenResolution(this);

        if(SCREEN_SIZE >1500)
        {
            SET_TRANSLATE = -560;
        }
        else if(SCREEN_SIZE <=1500)
        {
            SET_TRANSLATE = -300;
        }

        WithAFriendBtn.setOnTouchListener(this);
        WithAFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfflineGameMenuActivity.this,OfflineGetPlayersNamesActivity.class);
                startActivity(intent);
            }
        });

        WithAi.setOnTouchListener(this);
        WithAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfflineGameMenuActivity.this,AIGetPlayerNameActivity.class);
                startActivity(intent);
            }
        });

        settingsGifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = settingsGifView.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();

                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Drawable drawable = settingsGifView.getDrawable();
                        if (drawable instanceof Animatable) {
                            ((Animatable) drawable).stop();
                        }
                        Intent intent = new Intent(OfflineGameMenuActivity.this,SettingsActivity.class);
                        startActivity(intent);

                    }
                }, 750);
            }
        });


    }



    private int getScreenResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        //  Toast.makeText(SplashActivity.this , "Screen height is : "+ height , Toast.LENGTH_SHORT).show();

        return height ;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus || animationStarted) {
            return;
        }

        animate();

        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        // No changes needed here
    }

    private void animate() {

        ImageView logoImageView = (ImageView) findViewById(R.id.img_logo_offline_menu);
        ViewGroup container = (ViewGroup) findViewById(R.id.container_offline_menu);

        ViewCompat.animate(logoImageView)
                .translationY(SET_TRANSLATE)
                .setStartDelay(STARTUP_DELAY)
                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
                        new DecelerateInterpolator(1.2f)).start();

        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (!(v instanceof Button)) {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50).alpha(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(1000);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(500);
            }

            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == WithAFriendBtn) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
        else  if (v == WithAi) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
        return false;
    }
}