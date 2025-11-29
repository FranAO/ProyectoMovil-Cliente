package com.example.proyectomovil.UI;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;

import com.example.proyectomovil.R;
import com.google.android.material.button.MaterialButton;

public class PackagesActivity extends BaseNavigationActivity {

    private CardView package1Card, package2Card, package3Card, activePackageCard;
    private TextView price1, price2, price3, tvActivePackageName, tvActivePackageTickets;
    private String studentEmail;
    
    private final double PRICE_5 = 25.0;
    private final double PRICE_10 = 45.0;
    private final double PRICE_15 = 60.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_packages);

        setupNavigation();
        initViews();
        loadUserPackages();
    }

    private void initViews() {
        SharedPreferences prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE);
        studentEmail = prefs.getString("LOGGED_IN_USER_EMAIL", "");

        package1Card = findViewById(R.id.package1Card);
        package2Card = findViewById(R.id.package2Card);
        package3Card = findViewById(R.id.package3Card);
        activePackageCard = findViewById(R.id.activePackageCard);
        
        price1 = findViewById(R.id.price1);
        price2 = findViewById(R.id.price2);
        price3 = findViewById(R.id.price3);
        tvActivePackageName = findViewById(R.id.tvActivePackageName);
        tvActivePackageTickets = findViewById(R.id.tvActivePackageTickets);

        price1.setText(String.format("Bs. %.2f", PRICE_5));
        price2.setText(String.format("Bs. %.2f", PRICE_10));
        price3.setText(String.format("Bs. %.2f", PRICE_15));

        package1Card.setOnClickListener(v -> showConfirmationDialog("5 Tickets", 5, PRICE_5, 30));
        package2Card.setOnClickListener(v -> showConfirmationDialog("10 Tickets", 10, PRICE_10, 45));
        package3Card.setOnClickListener(v -> showConfirmationDialog("15 Tickets", 15, PRICE_15, 60));
    }

    private void loadUserPackages() {
        if (studentEmail.isEmpty()) {
            Toast.makeText(this, "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.proyectomovil.Services.TicketApiService.getPackageSummary(
                studentEmail,
                new com.example.proyectomovil.Services.TicketApiService.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            org.json.JSONObject json = new org.json.JSONObject(response);
                            org.json.JSONArray packages = json.getJSONArray("packages");
                            
                            boolean hasActivePackage = false;
                            
                            for (int i = 0; i < packages.length(); i++) {
                                org.json.JSONObject pkg = packages.getJSONObject(i);
                                int available = pkg.getInt("availableTickets");
                                int total = pkg.getInt("totalTickets");
                                
                                if (available > 0) {
                                    activePackageCard.setVisibility(View.VISIBLE);
                                    
                                    String packageName = total + " Tickets";
                                    tvActivePackageName.setText(packageName);
                                    tvActivePackageTickets.setText(String.format("%d/%d tickets disponibles", available, total));
                                    hasActivePackage = true;
                                    break;
                                }
                            }
                            
                            if (!hasActivePackage) {
                                activePackageCard.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            activePackageCard.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        activePackageCard.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void showConfirmationDialog(String packageName, int ticketCount, double price, int durationDays) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_package_confirmation);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvPackageName = dialog.findViewById(R.id.tvPackageName);
        TextView tvPackagePrice = dialog.findViewById(R.id.tvPackagePrice);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = dialog.findViewById(R.id.btnConfirm);

        tvPackageName.setText(packageName);
        tvPackagePrice.setText(String.format("Bs. %.2f", price));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            proceedToPayment(packageName, ticketCount, price, durationDays);
        });

        dialog.show();
    }

    private void proceedToPayment(String packageName, int ticketCount, double price, int durationDays) {
        Intent intent = new Intent(this, PaymentQRActivity.class);
        intent.putExtra("packageId", "PKG-" + ticketCount);
        intent.putExtra("packageName", packageName);
        intent.putExtra("ticketCount", ticketCount);
        intent.putExtra("price", price);
        intent.putExtra("durationDays", durationDays);
        intent.putExtra("studentEmail", studentEmail);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserPackages();
    }

    @Override
    protected int getNavigationIndex() {
        return 2;
    }
}