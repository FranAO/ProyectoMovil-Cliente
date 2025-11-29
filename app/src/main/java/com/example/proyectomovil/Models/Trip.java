package com.example.proyectomovil.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Trip {
    public String id;
    public String busId;
    public String driverId;
    public String routeId;
    public Date startTime;
    public Date endTime;
    public String status;
    public int occupiedSeats;
    public int totalSeats;

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateStr = startTime != null ? dateFormat.format(startTime) : "Fecha no disponible";
        return "Viaje - " + dateStr + " (" + status + ")";
    }
}
