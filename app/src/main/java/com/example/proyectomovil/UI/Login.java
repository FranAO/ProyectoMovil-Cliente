package com.example.proyectomovil.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectomovil.Database.DataSyncManager;
import com.example.proyectomovil.Database.DBHelper;
import com.example.proyectomovil.Models.Student;
import com.example.proyectomovil.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ImageView togglePasswordVisibility;
    private MaterialButton loginButton;
    private TextView registerTextView;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "MiAppPrefs";
    private static final String BIOMETRIC_USER_ID = "BIOMETRIC_USER_ID";
    private static final String LOGGED_IN_USER_EMAIL = "LOGGED_IN_USER_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        setupBiometricLogic();

        String biometricUserId = prefs.getString(BIOMETRIC_USER_ID, null);
        if (biometricUserId != null) {
            authenticateWithBiometrics();
        }

        loginButton.setOnClickListener(v -> handleLogin());

        registerTextView.setOnClickListener(v -> handleRegister());

        togglePasswordVisibility.setOnClickListener(v -> handlePasswordToggle());
    }

    private void setupBiometricLogic() {
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(Login.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        String userId = prefs.getString(BIOMETRIC_USER_ID, null);
                        Toast.makeText(getApplicationContext(), "Bienvenido " + userId, Toast.LENGTH_SHORT).show();
                        saveLoggedInUser(userId);
                        goToMain();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Inicio de sesión biométrico")
                .setSubtitle("Inicia sesión usando tu huella dactilar")
                .setNegativeButtonText("Usar contraseña")
                .build();
    }

    private void authenticateWithBiometrics() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG;

        if (biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "Huella no disponible, usa tu contraseña.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:5090/api/Auth/login");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                JSONObject loginData = new JSONObject();
                loginData.put("email", email);
                loginData.put("password", password);

                con.getOutputStream().write(loginData.toString().getBytes());

                int responseCode = con.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    DataSyncManager syncManager = new DataSyncManager(this);
                    Student student = syncManager.sincronizarEstudiantePorEmail(email);

                    runOnUiThread(() -> {
                        if (student != null) {
                            saveLoggedInUser(email);
                            goToMain();
                        } else {
                            Toast.makeText(this, "Error al sincronizar datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void handleRegister() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }

    private void handlePasswordToggle() {
        if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            passwordEditText.setTransformationMethod(null);
        } else {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        passwordEditText.setSelection(passwordEditText.length());
    }

    private void saveLoggedInUser(String email) {
        prefs.edit().putString(LOGGED_IN_USER_EMAIL, email).apply();
    }

    private void goToMain() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}