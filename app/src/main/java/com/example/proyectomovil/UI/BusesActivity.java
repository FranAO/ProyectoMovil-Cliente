package com.example.proyectomovil.UI;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovil.Adapters.BusAdapter;
import com.example.proyectomovil.Models.Bus;
import com.example.proyectomovil.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BusesActivity extends BaseNavigationActivity {

    private RecyclerView recyclerViewBuses;
    private BusAdapter busAdapter;
    private ArrayList<Bus> busList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buses);

        recyclerViewBuses = findViewById(R.id.recyclerViewBuses);
        recyclerViewBuses.setLayoutManager(new LinearLayoutManager(this));

        busList = new ArrayList<>();
        busAdapter = new BusAdapter(busList);
        recyclerViewBuses.setAdapter(busAdapter);

        setupNavigation();
        cargarBuses();
    }

    private void cargarBuses() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:5090/api/bus");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder respuesta = new StringBuilder();
                    String linea;

                    while ((linea = reader.readLine()) != null) {
                        respuesta.append(linea);
                    }

                    JSONArray array = new JSONArray(respuesta.toString());
                    busList.clear();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Bus bus = new Bus();
                        bus.setId(obj.optString("id"));
                        bus.setBusCode(obj.optString("busCode"));
                        bus.setPlate(obj.optString("plate"));
                        bus.setCapacity(obj.optInt("capacity"));
                        bus.setStatus(obj.optString("status"));
                        bus.setDriverId(obj.optString("driverId"));
                        bus.setRouteId(obj.optString("routeId"));
                        bus.setCurrentPassengers((int) (Math.random() * bus.getCapacity()));
                        busList.add(bus);
                    }

                    runOnUiThread(() -> {
                        busAdapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al cargar buses", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    protected int getNavigationIndex() {
        return 1;
    }
}