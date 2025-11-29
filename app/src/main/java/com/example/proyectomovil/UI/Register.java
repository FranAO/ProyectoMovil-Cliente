package com.example.proyectomovil.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomovil.Database.DBHelper;
import com.example.proyectomovil.Models.Student;
import com.example.proyectomovil.R;
import com.google.android.material.button.MaterialButton;

import java.util.Date;
import java.util.UUID;

public class Register extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, phoneEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private ImageView togglePasswordVisibility, toggleConfirmPasswordVisibility;
    private MaterialButton registerButton;
    private TextView loginTextView;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        toggleConfirmPasswordVisibility = findViewById(R.id.toggleConfirmPasswordVisibility);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        dbHelper = new DBHelper(this);

        registerButton.setOnClickListener(v -> handleRegister());
        loginTextView.setOnClickListener(v -> handleLogin());
        togglePasswordVisibility.setOnClickListener(v -> handlePasswordToggle(passwordEditText));
        toggleConfirmPasswordVisibility.setOnClickListener(v -> handlePasswordToggle(confirmPasswordEditText));
    }

    private void handleRegister() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validar que todos los campos estén completos
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Primero verificar en la base de datos local
        Student existente = dbHelper.obtenerStudentPorEmail(email);
        if (existente != null) {
            Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear nuevo estudiante con ID temporal
        final Student nuevoStudent = new Student(
                UUID.randomUUID().toString(),
                firstName,
                lastName,
                email,
                phone,
                password,
                "STUDENT",
                new Date()
        );

        // Primero guardar localmente para garantizar acceso inmediato
        boolean guardadoLocal = dbHelper.insertarStudent(nuevoStudent);
        
        if (!guardadoLocal) {
            Toast.makeText(this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
        
        // Intentar sincronizar con el servidor en segundo plano
        new Thread(() -> {
            try {
                com.example.proyectomovil.Database.DataSyncManager syncManager = 
                    new com.example.proyectomovil.Database.DataSyncManager(this);
                
                Student studentConIdWeb = syncManager.enviarEstudiante(nuevoStudent);
                
                if (studentConIdWeb != null) {
                    // Actualizar el ID local con el ID de la web
                    dbHelper.eliminarStudent(nuevoStudent.getId());
                    dbHelper.insertarStudent(studentConIdWeb);
                    android.util.Log.d("Register", "Estudiante sincronizado con servidor con ID: " + studentConIdWeb.getId());
                }
            } catch (Exception e) {
                android.util.Log.e("Register", "Error al sincronizar con servidor: " + e.getMessage());
            }
        }).start();
        
        // Ir al login inmediatamente
        goToLogin();
    }

    private void handleLogin() {
        goToLogin();
    }

    private void handlePasswordToggle(EditText passwordField) {
        if (passwordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
            passwordField.setTransformationMethod(null);
        } else {
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        passwordField.setSelection(passwordField.length());
    }

    private void goToLogin() {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }
}