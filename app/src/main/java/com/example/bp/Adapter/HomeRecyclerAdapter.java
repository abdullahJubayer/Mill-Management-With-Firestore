package com.example.bp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bp.DataModel.User;
import com.example.bp.R;

import java.util.List;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeRecyclerViewHolder> {
    private List<User> userList;
    private double millRate;

    public HomeRecyclerAdapter(List<User> userList, double millRate) {
        this.userList=userList;
        this.millRate=millRate;
    }

    public HomeRecyclerAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public HomeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycler_item_design, parent, false);
        return new HomeRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeRecyclerViewHolder holder, final int position) {
        //for show mill
        holder.name.setText(userList.get(position).getName());
        double totalMill=0.0;
        for (String mill:userList.get(position).getMills().values()){
            totalMill+=Double.parseDouble(mill);
        }
        double totalBalance=0.0;
        for (String balance:userList.get(position).getDeposits().values()){
            totalBalance+=Double.parseDouble(balance);
        }
        holder.userBalance.setText("$ "+ (totalBalance - (totalMill * millRate)));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class HomeRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView userBalance;

        public HomeRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_txt);
            userBalance = itemView.findViewById(R.id.user_balance);
        }
    }
}
