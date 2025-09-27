package com.example.proyectomovil;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;

public class MainActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupNavigation();
    }

    @Override
    protected int getNavigationIndex() {
        return 0;
    }
}