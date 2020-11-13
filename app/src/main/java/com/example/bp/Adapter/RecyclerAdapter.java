package com.example.bp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bp.DataModel.RecyclerModel;
import com.example.bp.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.DepositRecyclerViewHolder> {
    private List<RecyclerModel> models;

    public RecyclerAdapter(List<RecyclerModel> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public RecyclerAdapter.DepositRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_item_design, parent, false);
        return new RecyclerAdapter.DepositRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.DepositRecyclerViewHolder holder, final int position) {
        //for show mill
        holder.name.setText(models.get(position).getData());
        holder.userBalance.setText(models.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class DepositRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView userBalance;

        public DepositRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_txt);
            userBalance = itemView.findViewById(R.id.user_balance);
        }
    }
}
