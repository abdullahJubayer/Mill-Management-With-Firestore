package com.example.bp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bp.DataModel.Expences;
import com.example.bp.DataModel.Mill;
import com.example.bp.DataModel.User;
import com.example.bp.Fragment.ShowMillFragment;
import com.example.bp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeRecyclerViewHolder> {
    private List<User> userList;
    private List<Expences> expencesList;
    private List<Deposit> depositList;
    private FirebaseFirestore db;
    private double millRate = 0.0;
    private double deposit = 0.0;
    private ShowMillFragment showMillFragment;
    private List<Mill> millList;

    public HomeRecyclerAdapter(List<User> userList, double millRate) {
        this.userList = userList;
        this.millRate = millRate;
        db = FirebaseFirestore.getInstance();
    }

    public HomeRecyclerAdapter(List<User> userList) {
        this.userList = userList;
        db = FirebaseFirestore.getInstance();
    }
    public HomeRecyclerAdapter(List<Deposit> depositList,float deposit) {
        this.depositList = depositList;
        db = FirebaseFirestore.getInstance();
    }
    public HomeRecyclerAdapter(List<Expences> expencesList, int i) {
        this.expencesList = expencesList;
        db = FirebaseFirestore.getInstance();
    }

    public HomeRecyclerAdapter(List<Mill> millList, String userName) {
        this.millList = millList;
        db = FirebaseFirestore.getInstance();
    }

    public HomeRecyclerAdapter(ShowMillFragment fragment, List<User> userList) {
        this.userList = userList;
        this.showMillFragment = fragment;
        db = FirebaseFirestore.getInstance();
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
        if (showMillFragment != null && userList != null) {
            holder.name.setText(userList.get(position).getName());
            holder.userBalance.setText(String.valueOf(userList.get(position).getMill()));
        }
        //for show expences List
        else if (expencesList != null) {
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expencesList.get(position).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newstring = new SimpleDateFormat("dd-MM-yyyy").format(date);
            holder.name.setText(newstring + "-" + expencesList.get(position).getName());
            holder.userBalance.setText(expencesList.get(position).getAmount());
        }
        //for show deposit List
        else if (depositList != null) {
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(depositList.get(position).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newstring = new SimpleDateFormat("dd-MM-yyyy").format(date);
            holder.name.setText(newstring);
            holder.userBalance.setText(depositList.get(position).getAmount());
        }
        //for show Mill List
        else if (millList != null) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(millList.get(position).getDate());
                String newstring = new SimpleDateFormat("dd-MM-yyyy").format(date);
                holder.name.setText(newstring);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.userBalance.setText(String.valueOf(millList.get(position).getMill()));
        }
        //for show User Mill List
        else if (showMillFragment == null) {
            holder.name.setText(userList.get(position).getName());
            if (millRate != 0.0) {
                holder.userBalance.setText(new DecimalFormat("##.##").format(Double.parseDouble(userList.get(position).getBalance()) - (userList.get(position).getMill() * millRate)));
            } else {
                holder.userBalance.setText(userList.get(position).getBalance());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
        } else if (millList != null) {
            return millList.size();
        } else if (expencesList != null) {
            return expencesList.size();
        }else if (depositList != null) {
            return depositList.size();
        }
        else return 0;
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
