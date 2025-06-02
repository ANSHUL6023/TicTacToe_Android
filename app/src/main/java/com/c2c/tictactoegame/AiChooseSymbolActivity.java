package com.c2c.tictactoegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AiChooseSymbolActivity extends AppCompatActivity implements View.OnTouchListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ImageView BackBtn, CrossImg, CrossRadioImg, CircleImg, CircleRadioImg;
    private Button ContinueBtn;

    int PICK_SIDE;
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ai_choose_symbol);

        playerName = getIntent().getStringExtra("p1");

        BackBtn = findViewById(R.id.ai_pick_side_back_btn);
        CrossImg = findViewById(R.id.ai_pick_side_cross_img);
        CircleImg = findViewById(R.id.ai_pick_side_circle_img);
        CrossRadioImg = findViewById(R.id.ai_pick_side_cross_radio);
        CircleRadioImg = findViewById(R.id.ai_pick_side_circle_radio);
        ContinueBtn = findViewById(R.id.ai_pick_side_continue_btn);

        CrossRadioImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICK_SIDE = 0;
                CrossRadioImg.setImageResource(R.drawable.radio_button_checked);
                CircleRadioImg.setImageResource(R.drawable.radio_button_unchecked);
                CircleImg.setAlpha(0.3f);
                CrossImg.setAlpha(1.0f);

                // Firebase Analytics event for Cross
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Cross");
                mFirebaseAnalytics.logEvent("symbol_selected", bundle);
            }
        });

        CircleRadioImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICK_SIDE = 1;
                CircleRadioImg.setImageResource(R.drawable.radio_button_checked);
                CrossRadioImg.setImageResource(R.drawable.radio_button_unchecked);
                CrossImg.setAlpha(0.3f);
                CircleImg.setAlpha(1.0f);

                // Firebase Analytics event for Circle
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Circle");
                mFirebaseAnalytics.logEvent("symbol_selected", bundle);
            }
        });

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ContinueBtn.setOnTouchListener(this);
        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AiChooseSymbolActivity.this, AiGameActivity.class);
                intent.putExtra("p1", playerName);
                intent.putExtra("ps", PICK_SIDE);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == ContinueBtn) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
