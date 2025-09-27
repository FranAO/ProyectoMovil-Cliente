package com.example.proyectomovil;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

public class HistoryActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        setupNavigation();
    }

    @Override
    protected int getNavigationIndex() {
        return 3;
    }
}