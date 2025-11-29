package com.example.proyectomovil.Services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiService {
    private static final String BASE_URL = "http://10.0.2.2:5090/api";
    private static final String TAG = "ApiService";

    public static class ApiResponse {
        public boolean success;
        public String data;
        public String error;

        public ApiResponse(boolean success, String data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
    }

    public static ApiResponse get(String endpoint) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode >= 200 && responseCode < 300) {
                return new ApiResponse(true, response.toString(), null);
            } else {
                return new ApiResponse(false, null, response.toString());
            }

        } catch (Exception e) {
            Log.e(TAG, "GET Error: " + endpoint, e);
            return new ApiResponse(false, null, e.getMessage());
        }
    }

    public static ApiResponse post(String endpoint, JSONObject body) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (body != null) {
                OutputStream os = conn.getOutputStream();
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.close();
            }

            int responseCode = conn.getResponseCode();
            
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode >= 200 && responseCode < 300) {
                return new ApiResponse(true, response.toString(), null);
            } else {
                return new ApiResponse(false, null, response.toString());
            }

        } catch (Exception e) {
            Log.e(TAG, "POST Error: " + endpoint, e);
            return new ApiResponse(false, null, e.getMessage());
        }
    }
}
