package com.example.proyectomovil.Services;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicketApiService {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void purchasePackage(String studentEmail, int ticketCount, int durationDays, ApiCallback callback) {
        executor.execute(() -> {
            try {
                android.util.Log.d("TicketApiService", "Attempting to purchase package for: " + studentEmail);
                String endpoint = "/Ticket/purchase-package?studentId=" + studentEmail + 
                                "&ticketCount=" + ticketCount + 
                                "&durationDays=" + durationDays;
                android.util.Log.d("TicketApiService", "Endpoint: " + endpoint);
                
                ApiService.ApiResponse response = ApiService.post(endpoint, null);

                mainHandler.post(() -> {
                    if (response.success) {
                        android.util.Log.d("TicketApiService", "Purchase successful: " + response.data);
                        callback.onSuccess(response.data);
                    } else {
                        android.util.Log.e("TicketApiService", "Purchase failed: " + response.error);
                        callback.onError(response.error != null ? response.error : "Error desconocido");
                    }
                });
            } catch (Exception e) {
                android.util.Log.e("TicketApiService", "Exception in purchasePackage", e);
                final String errorMsg = e.getMessage() != null ? e.getMessage() : "Error de conexión";
                mainHandler.post(() -> callback.onError("Error de conexión: " + errorMsg));
            }
        });
    }

    public static void getPackageSummary(String studentEmail, ApiCallback callback) {
        executor.execute(() -> {
            try {
                String endpoint = "/Ticket/package-summary/" + studentEmail;
                ApiService.ApiResponse response = ApiService.get(endpoint);

                mainHandler.post(() -> {
                    if (response.success) {
                        callback.onSuccess(response.data);
                    } else {
                        callback.onError(response.error != null ? response.error : "Error desconocido");
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void useTicketFromPackage(String studentEmail, ApiCallback callback) {
        executor.execute(() -> {
            try {
                String endpoint = "/Ticket/use-from-package?studentId=" + studentEmail;
                ApiService.ApiResponse response = ApiService.post(endpoint, null);

                mainHandler.post(() -> {
                    if (response.success) {
                        callback.onSuccess(response.data);
                    } else {
                        callback.onError(response.error != null ? response.error : "Error desconocido");
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void purchaseSingleTicket(String studentEmail, String tripId, ApiCallback callback) {
        executor.execute(() -> {
            try {
                String endpoint = "/Ticket/purchase-single?studentId=" + studentEmail + "&tripId=" + tripId;
                ApiService.ApiResponse response = ApiService.post(endpoint, null);

                mainHandler.post(() -> {
                    if (response.success) {
                        callback.onSuccess(response.data);
                    } else {
                        callback.onError(response.error != null ? response.error : "Error desconocido");
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void getAvailableTickets(String studentEmail, ApiCallback callback) {
        executor.execute(() -> {
            try {
                String endpoint = "/Ticket/available/" + studentEmail;
                ApiService.ApiResponse response = ApiService.get(endpoint);

                mainHandler.post(() -> {
                    if (response.success) {
                        callback.onSuccess(response.data);
                    } else {
                        callback.onError(response.error != null ? response.error : "Error desconocido");
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}
