package com.example.lab4_inclass_health;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private View layoutMeasurement, layoutNotAvailable, layoutRecents;
    private TextView tvTimer, tvHeartRate, tvCalories, tvTrend, tvSteps;
    private Button btnStart, btnPause;
    private Button nav1, nav2, nav3;

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRunning = false;
    private int seconds = 0;
    private int steps = 0;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Layouts
        layoutMeasurement = findViewById(R.id.layout_measurement);
        layoutNotAvailable = findViewById(R.id.layout_not_available);
        layoutRecents = findViewById(R.id.layout_recents);

        // Measurement Views
        tvTimer = findViewById(R.id.tv_timer);
        tvHeartRate = findViewById(R.id.tv_heart_rate);
        tvCalories = findViewById(R.id.tv_calories);
        tvTrend = findViewById(R.id.tv_trend);
        tvSteps = findViewById(R.id.tv_steps);
        btnStart = findViewById(R.id.btn_start_end);
        btnPause = findViewById(R.id.btn_pause_resume);

        // Navigation Buttons
        nav1 = findViewById(R.id.nav_1);
        nav2 = findViewById(R.id.nav_2);
        nav3 = findViewById(R.id.nav_3);

        btnStart.setOnClickListener(v -> {
            if (btnStart.getText().toString().equals("START")) {
                startTimer();
                btnStart.setText("END");
                btnPause.setEnabled(true);
            } else {
                stopTimer();
                btnStart.setText("START");
                btnPause.setText("PAUSE");
                btnPause.setEnabled(false);
            }
        });
        
        btnPause.setOnClickListener(v -> pauseTimer());

        nav1.setOnClickListener(v -> showScreen(1));
        nav2.setOnClickListener(v -> showScreen(2));
        nav3.setOnClickListener(v -> showScreen(3));

        btnPause.setEnabled(false);
        updateUI();
    }

    private void showScreen(int screenNumber) {
        layoutMeasurement.setVisibility(screenNumber == 1 ? View.VISIBLE : View.GONE);
        layoutNotAvailable.setVisibility(screenNumber == 2 ? View.VISIBLE : View.GONE);
        layoutRecents.setVisibility(screenNumber == 3 ? View.VISIBLE : View.GONE);
    }

    private void startTimer() {
        if (!isRunning) {
            isRunning = true;
            handler.post(runnable);
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(runnable);
            btnPause.setText("RESUME");
        } else if (seconds > 0) {
            isRunning = true;
            handler.post(runnable);
            btnPause.setText("PAUSE");
        }
    }

    private void stopTimer() {
        isRunning = false;
        handler.removeCallbacks(runnable);
        seconds = 0;
        steps = 0;
        updateUI();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                seconds++;
                updateUI();
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void updateUI() {
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time = String.format(Locale.getDefault(), "%02dm %02ds", minutes, secs);
        tvTimer.setText(time);

        if (isRunning || (seconds > 0 && !isRunning)) {
            if (isRunning) {
                int hr = 70 + random.nextInt(110); 
                tvHeartRate.setText(String.valueOf(hr));

                int cal = seconds / 3;
                tvCalories.setText(cal + " cal");

                if (seconds % 2 == 0) {
                    steps += random.nextInt(3);
                }
                tvSteps.setText(String.valueOf(steps));
                
                double distance = steps * 0.0007; // 1 step ~ 0.7m
                tvTrend.setText(String.format(Locale.getDefault(), "%.2fkm", distance));
            }
        } else if (seconds == 0) {
            tvHeartRate.setText("-");
            tvCalories.setText("-");
            tvTrend.setText("-");
            tvSteps.setText("0");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
