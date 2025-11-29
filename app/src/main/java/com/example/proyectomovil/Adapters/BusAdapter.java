package com.example.proyectomovil.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectomovil.Models.Bus;
import com.example.proyectomovil.R;
import com.example.proyectomovil.UI.TicketDetailActivity;
import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private ArrayList<Bus> busList;

    public BusAdapter(ArrayList<Bus> busList) {
        this.busList = busList;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus bus = busList.get(position);
        holder.tvBusNumber.setText(bus.getBusCode());
        holder.tvBusStatus.setText(bus.getCurrentPassengers() + "/" + bus.getCapacity() + " pasajeros");
        
        holder.btnComprarTicket.setOnClickListener(v -> {
            mostrarDialogTicketOptions(v);
        });
    }
    
    private void mostrarDialogTicketOptions(View view) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog_ticket_options);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        CardView optionBuyTicket = dialog.findViewById(R.id.optionBuyTicket);
        CardView optionUsePlan = dialog.findViewById(R.id.optionUsePlan);
        CardView btnCancel = dialog.findViewById(R.id.btnCancel);
        
        optionBuyTicket.setOnClickListener(v -> {
            dialog.dismiss();
            generarTicket(view.getContext(), "compra");
        });
        
        optionUsePlan.setOnClickListener(v -> {
            dialog.dismiss();
            generarTicket(view.getContext(), "plan");
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private void generarTicket(Context context, String tipo) {
        SharedPreferences prefs = context.getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
        String studentEmail = prefs.getString("LOGGED_IN_USER_EMAIL", "");

        if (studentEmail.isEmpty()) {
            Toast.makeText(context, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("plan".equals(tipo)) {
            com.example.proyectomovil.Services.TicketApiService.useTicketFromPackage(
                    studentEmail,
                    new com.example.proyectomovil.Services.TicketApiService.ApiCallback() {
                        @Override
                        public void onSuccess(String response) {
                            try {
                                org.json.JSONObject json = new org.json.JSONObject(response);
                                org.json.JSONObject ticket = json.getJSONObject("ticket");
                                int remaining = json.getInt("remainingInPackage");
                                
                                String ticketId = ticket.getString("id");
                                String status = ticket.optString("status", "available");
                                
                                Toast.makeText(context, 
                                    String.format("Ticket generado! Te quedan %d tickets en el paquete", remaining), 
                                    Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(context, TicketDetailActivity.class);
                                intent.putExtra("ticketId", ticketId);
                                intent.putExtra("studentEmail", studentEmail);
                                intent.putExtra("tipo", "Paquete");
                                intent.putExtra("status", status);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(context, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            if (error.contains("No available tickets")) {
                                Toast.makeText(context, "No tienes paquetes activos con tickets disponibles", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

        } else {
            com.example.proyectomovil.Services.TicketApiService.purchaseSingleTicket(
                    studentEmail,
                    "default-trip-id",
                    new com.example.proyectomovil.Services.TicketApiService.ApiCallback() {
                        @Override
                        public void onSuccess(String response) {
                            try {
                                org.json.JSONObject json = new org.json.JSONObject(response);
                                org.json.JSONObject ticket = json.getJSONObject("ticket");
                                String ticketId = ticket.getString("id");
                                String status = ticket.optString("status", "available");
                                
                                Toast.makeText(context, "Ticket generado exitosamente", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(context, TicketDetailActivity.class);
                                intent.putExtra("ticketId", ticketId);
                                intent.putExtra("studentEmail", studentEmail);
                                intent.putExtra("tipo", "Viaje sencillo");
                                intent.putExtra("status", status);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(context, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView tvBusNumber, tvBusStatus;
        CardView btnComprarTicket;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            tvBusStatus = itemView.findViewById(R.id.tvBusStatus);
            btnComprarTicket = itemView.findViewById(R.id.btnComprarTicket);
        }
    }
}
