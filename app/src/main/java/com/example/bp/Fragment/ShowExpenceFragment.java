package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.bp.Adapter.Deposit;
import com.example.bp.Adapter.HomeRecyclerAdapter;
import com.example.bp.Adapter.RecyclerAdapterWithMap;
import com.example.bp.Adapter.ShowDepositAdapter;
import com.example.bp.DataModel.Expences;
import com.example.bp.DataModel.RecyclerModel;
import com.example.bp.DataModel.User;
import com.example.bp.Listener.ProgressbarListner;
import com.example.bp.ViewModel.HomeViewModel;
import com.example.bp.databinding.FragmentShowExpenceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowExpenceFragment extends Fragment implements ProgressbarListner {

    private FragmentShowExpenceBinding binding;
    private HomeViewModel homeViewModel;
    private double totalExpences = 0,totalUserBalance=0.0;
    private ShowDepositAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentShowExpenceBinding.inflate(inflater,container,false);
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
                setRecyclerData(expences);
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

                binding.banalceId.setText(String.valueOf("$ "+totalExpences));
                binding.expencesRate.setText("$ "+totalUserBalance);
                binding.currentUserBalanceAmount.setText("$ "+(totalUserBalance-totalExpences));
            }
        });
    }

    private void setRecyclerData(List<Expences> expences) {
        List<RecyclerModel> models=new ArrayList<>();
        models.clear();
        for (Expences e:expences){
            RecyclerModel model=new RecyclerModel(e.getDate()+"  "+e.getName(),e.getAmount());
            models.add(model);
        }
        setRecyclerView(models);
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