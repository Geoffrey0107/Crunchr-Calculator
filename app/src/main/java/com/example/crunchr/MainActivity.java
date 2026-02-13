package com.example.crunchr;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        TextView displayTv = findViewById(R.id.display_tv);
        displayTv.setText("0");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button historyBtn = findViewById(R.id.btn_history);
        historyBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, HistoryActivity.class));
        });

        CalcOnClickListener listener = new CalcOnClickListener(displayTv);

        int[] btnIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
                R.id.btn_plus, R.id.btn_minus, R.id.btn_multiply, R.id.btn_divide,
                R.id.btn_dot, R.id.btn_clear, R.id.btn_enter,R.id.btn_exponent
        };

        for (int id : btnIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }
}