package com.example.crunchr;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ListView lv = findViewById(R.id.historyLV);

        ArrayAdapter<String> aa = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                CalcHistory.items
        );

        lv.setAdapter(aa);
    }
}