package com.example.proyectomovil.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovil.Models.Ticket;
import com.example.proyectomovil.R;
import com.example.proyectomovil.UI.TicketDetailActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private ArrayList<Ticket> ticketList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public TicketAdapter(Context context, ArrayList<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        holder.tvTicketId.setText(ticket.getId());

        if (ticket.getPurchaseDate() != null) {
            holder.tvTicketDate.setText(dateFormat.format(ticket.getPurchaseDate()));
        }

        String tipo = ticket.getPackageId() != null && !ticket.getPackageId().isEmpty() 
            ? "Paquete" : "Viaje sencillo";
        holder.tvTicketType.setText(tipo);

        String status = ticket.getStatus();
        if ("available".equals(status)) {
            holder.tvTicketStatus.setText("Disponible");
            holder.tvTicketStatus.setBackgroundResource(R.drawable.status_badge_available);
        } else {
            holder.tvTicketStatus.setText("Usado");
            holder.tvTicketStatus.setBackgroundResource(R.drawable.status_badge_used);
        }

        try {
            String qrData = ticket.getId() + "|" + ticket.getStudentId() + "|" + 
                           (ticket.getPurchaseDate() != null ? dateFormat.format(ticket.getPurchaseDate()) : "");
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 200, 200);
            holder.imgQRThumbnail.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("ticketId", ticket.getId());
            intent.putExtra("studentEmail", ticket.getStudentId());
            intent.putExtra("date", ticket.getPurchaseDate() != null ? 
                dateFormat.format(ticket.getPurchaseDate()) : "");
            intent.putExtra("type", tipo);
            intent.putExtra("status", ticket.getStatus());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        ImageView imgQRThumbnail;
        TextView tvTicketId, tvTicketDate, tvTicketType, tvTicketStatus;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            imgQRThumbnail = itemView.findViewById(R.id.imgQRThumbnail);
            tvTicketId = itemView.findViewById(R.id.tvTicketId);
            tvTicketDate = itemView.findViewById(R.id.tvTicketDate);
            tvTicketType = itemView.findViewById(R.id.tvTicketType);
            tvTicketStatus = itemView.findViewById(R.id.tvTicketStatus);
        }
    }
}
