package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.bp.Adapter.RecyclerAdapterWithMap;
import com.example.bp.Adapter.RecyclerAdapter;
import com.example.bp.DataModel.Expences;
import com.example.bp.DataModel.RecyclerModel;
import com.example.bp.DataModel.User;
import com.example.bp.Listener.ProgressbarListner;
import com.example.bp.R;
import com.example.bp.ViewModel.HomeViewModel;
import com.example.bp.databinding.FragmentShowDepositBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FragmentShowDeposit extends Fragment implements ProgressbarListner {

    private FragmentShowDepositBinding binding;
    private HomeViewModel homeViewModel;
    private double totalExpences = 0,totalUserBalance=0.0;
    private RecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShowDepositBinding.inflate(inflater, container, false);
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
                totalUserBalance=0.0;
                for (User u:users){
                    for (String b:u.getDeposits().values()){
                        totalUserBalance+=Double.parseDouble(b);
                    }
                }

                binding.banalceId.setText(String.valueOf("$ "+totalUserBalance));
                binding.expencesRate.setText("$ "+totalExpences);
                setRecyclerData(users);
                setDropDown(users);
            }
        });
    }

    private void setRecyclerData(List<User> users) {
        List<RecyclerModel> models=new ArrayList<>();
        models.clear();
        for (User u:users){
            RecyclerModel model=new RecyclerModel();
            model.setData(u.getName());
            double balance=0.0;
            for (String b:u.getDeposits().values()){
                balance+=Double.parseDouble(b);
            }
            model.setValue("$ "+balance);
            models.add(model);
        }

        setRecyclerView(models);
    }

    private void setDropDown(final List<User> users) {
        List<String> name=new ArrayList<>();
        name.clear();
        name.add("");
        for (User nn:users){
            name.add(nn.getName());
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,name);
        binding.selectedUserId.setAdapter(adapter);
        binding.selectedUserId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (binding.selectedUserId.getSelectedItemPosition() !=0){
                    double deposit=0.0;
                    for (String d:users.get(i-1).getDeposits().values()){
                        deposit+=Double.parseDouble(d);
                    }
                    binding.currentUserBalanceAmount.setText("$ "+deposit);
                    setRecyclerView(users.get(i-1).getDeposits());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setRecyclerView(List<RecyclerModel> models){
        adapter = new RecyclerAdapter(models);
        binding.homeRecyclerView.setHasFixedSize(true);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.homeRecyclerView.setAdapter(adapter);
    }

    private void setRecyclerView(Map<String,String> models){
        RecyclerAdapterWithMap adapter= new RecyclerAdapterWithMap(models);
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
        //binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
       // binding.progressBar.setVisibility(View.GONE);
    }
}