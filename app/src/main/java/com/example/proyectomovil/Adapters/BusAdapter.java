package com.example.proyectomovil.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectomovil.Models.Bus;
import com.example.proyectomovil.R;
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
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView tvBusNumber, tvBusStatus;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            tvBusStatus = itemView.findViewById(R.id.tvBusStatus);
        }
    }
}
