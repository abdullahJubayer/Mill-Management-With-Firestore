package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bp.Adapter.HomeRecyclerAdapter;
import com.example.bp.Adapter.ShowDepositAdapter;
import com.example.bp.DataModel.Deposit;
import com.example.bp.DataModel.Expences;
import com.example.bp.DataModel.Mill;
import com.example.bp.DataModel.RecyclerModel;
import com.example.bp.DataModel.User;
import com.example.bp.Listener.ProgressbarListner;
import com.example.bp.MySharedPreferences.MySharedPreference;
import com.example.bp.R;
import com.example.bp.ViewModel.HomeViewModel;
import com.example.bp.databinding.FragmentHomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends Fragment implements ProgressbarListner {

    private FragmentHomePageBinding binding;
    private ShowDepositAdapter adapter;
    private HomeViewModel homeViewModel;
    private double totalExpences = 0,totalMill = 0,millRate = 0,totalUserBalance=0.0,currentBalance=0.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        homeViewModel.getExpences(this).observe(getViewLifecycleOwner(), new Observer<List<Expences>>() {
            @Override
            public void onChanged(List<Expences> expences) {
                totalExpences = 0;
                for (Expences exp : expences) {
                    totalExpences += Double.parseDouble(exp.getAmount());
                }
                getUserData();
            }
        });

        binding.drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout= requireActivity().findViewById(R.id.nav_drawer_layout);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

    }

    private void getUserData() {

        homeViewModel.getUserList(this).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                totalMill=0.0;
                millRate=0.0;
                totalUserBalance=0.0;
                currentBalance=0.0;
                for (User u:users){
                    for (String m:u.getMills().values()){
                        totalMill+=Double.parseDouble(m);
                        binding.totalMill.setText(String.valueOf(totalMill));
                    }
                    for (String b:u.getDeposits().values()){
                        totalUserBalance+=Double.parseDouble(b);
                    }
                }
                millRate=totalExpences/totalMill;
                currentBalance=totalUserBalance-totalExpences;

                binding.millRate.setText(String.valueOf("$ "+millRate));
                binding.banalceId.setText(String.valueOf("$ "+currentBalance));


                List<RecyclerModel> models=new ArrayList<>();
                for (User uu:users){
                    double totalMill=0.0;
                    for (String mill:uu.getMills().values()){
                        totalMill+=Double.parseDouble(mill);
                    }
                    double totalBalance=0.0;
                    for (String balance:uu.getDeposits().values()){
                        totalBalance+=Double.parseDouble(balance);
                    }
                    RecyclerModel model=new RecyclerModel(uu.getName(),"$ "+ (totalBalance - (totalMill * millRate)));
                    models.add(model);
                }
                setRecyclerView(models);
            }
        });
    }

    private void setRecyclerView(List<RecyclerModel> models){
        adapter = new ShowDepositAdapter(models);
        binding.homeRecyclerView.setHasFixedSize(true);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.homeRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }
}