package com.example.proyectomovil.UI;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.example.proyectomovil.R;

public class PackagesActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_packages);

        setupNavigation();
    }

    @Override
    protected int getNavigationIndex() {
        return 2;
    }
}