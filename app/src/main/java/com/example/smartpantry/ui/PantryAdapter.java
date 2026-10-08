package com.example.smartpantry.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.logic.AppSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Custom adapter: binds the list of PantryItem rows to item_pantry.xml cards. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Lets the Activity react to taps (used for Edit). */
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private static final long ONE_DAY = 24L * 60 * 60 * 1000;
    private static final int RED = Color.parseColor("#C62828");

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public PantryItem getItemAt(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.name);
        holder.quantity.setText(formatQuantity(item.quantity) + " " + item.unit);

        // Reset the colour first, because RecyclerView reuses rows
        holder.expiry.setTextColor(holder.normalExpiryColor);

        if (item.expiryDate == null) {
            holder.expiry.setText("No expiry date");
        } else {
            String date = dateFormat.format(new Date(item.expiryDate));
            long now = System.currentTimeMillis();
            boolean alertsOn = AppSettings.isExpiryAlertOn(holder.itemView.getContext());

            if (alertsOn && item.expiryDate + ONE_DAY < now) {
                holder.expiry.setText("Expired: " + date);
                holder.expiry.setTextColor(RED);
            } else if (alertsOn && item.expiryDate <= now + 3 * ONE_DAY) {
                holder.expiry.setText("Expiring soon: " + date);
                holder.expiry.setTextColor(RED);
            } else {
                holder.expiry.setText("Expires: " + date);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** 2.0 -> "2", 2.5 -> "2.5" */
    private String formatQuantity(double q) {
        return (q == Math.floor(q)) ? String.valueOf((long) q) : String.valueOf(q);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final int normalExpiryColor;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            quantity = itemView.findViewById(R.id.textQuantity);
            expiry = itemView.findViewById(R.id.textExpiry);
            normalExpiryColor = expiry.getCurrentTextColor();
        }
    }
}
