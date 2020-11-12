package com.example.bp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bp.DataModel.RecyclerModel;
import com.example.bp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecyclerAdapterWithMap extends RecyclerView.Adapter<RecyclerAdapterWithMap.RecyclerAdapterWithMapViewHolder> {
    private Map<String,String> data;

    public RecyclerAdapterWithMap(Map<String,String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerAdapterWithMap.RecyclerAdapterWithMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_item_design, parent, false);
        return new RecyclerAdapterWithMapViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterWithMap.RecyclerAdapterWithMapViewHolder holder, final int position) {
        //for show mill
        ArrayList<String> key=new ArrayList<String>(data.keySet());

        holder.name.setText(key.get(position));
        holder.userBalance.setText(data.get(key.get(position)));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecyclerAdapterWithMapViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView userBalance;

        public RecyclerAdapterWithMapViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_txt);
            userBalance = itemView.findViewById(R.id.user_balance);
        }
    }
}

