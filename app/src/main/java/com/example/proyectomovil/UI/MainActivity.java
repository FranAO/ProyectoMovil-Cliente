package com.example.proyectomovil.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;

import com.example.proyectomovil.Database.DBHelper;
import com.example.proyectomovil.Models.Student;
import com.example.proyectomovil.R;

public class MainActivity extends BaseNavigationActivity {
    private CardView settingsButton, mapIconContainer;
    private TextView tvUserName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        settingsButton = findViewById(R.id.SettingsCard);
        mapIconContainer = findViewById(R.id.mapIconContainer);
        tvUserName = findViewById(R.id.tvUserName);
        
        SharedPreferences prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("LOGGED_IN_USER_EMAIL", "Usuario");
        
        DBHelper dbHelper = new DBHelper(this);
        Student student = dbHelper.obtenerStudentPorEmail(userEmail);
        
        if (student != null) {
            String firstName = student.getFirstName() != null ? student.getFirstName().trim() : "";
            String lastName = student.getLastName() != null ? student.getLastName().trim() : "";
            
            String fullName = firstName + " " + lastName;
            fullName = fullName.trim();
            
            if (fullName.isEmpty()) {
                tvUserName.setText("Usuario");
            } else {
                tvUserName.setText(fullName);
            }
        } else {
            tvUserName.setText("Usuario");
        }
        
        setupNavigation();

        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        mapIconContainer.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RouteActivity.class));
        });
    }



    @Override
    protected int getNavigationIndex() {
        return 0;
    }
}