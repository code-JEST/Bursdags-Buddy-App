package com.example.s374946;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;
    private OnItemClickListener listener;
    private DatabaseHjelper dbHelper;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(List<Item> items, OnItemClickListener listener, DatabaseHjelper dbHelper) {
        this.items = items;
        this.listener = listener;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.textViewName.setText(item.getName());
        holder.textViewPhone.setText(item.getPhone());
        holder.textViewBirthdate.setText(item.getBirthdate());

        // Edit knappen
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddUserActivity.class);
            intent.putExtra("id", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("phone", item.getPhone());
            intent.putExtra("birthdate", item.getBirthdate());
            v.getContext().startActivity(intent);
        });

        // Delete knappen
        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deletePerson(item);
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPhone;
        TextView textViewBirthdate;
        ImageButton deleteButton;
        ImageButton editButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPhone = itemView.findViewById(R.id.text_view_phone);
            textViewBirthdate = itemView.findViewById(R.id.text_view_birthdate);
            deleteButton = itemView.findViewById(R.id.button_delete);
            editButton = itemView.findViewById(R.id.button_edit);
        }
    }
}

