package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bp.Adapter.HomeRecyclerAdapter;
import com.example.bp.DataModel.Deposit;
import com.example.bp.DataModel.User;
import com.example.bp.MySharedPreferences.MySharedPreference;
import com.example.bp.R;
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
import java.util.List;

public class HomePage extends Fragment {

    private FragmentHomePageBinding binding;
    private FirebaseFirestore db ;
    private EventListener<QuerySnapshot> memberListner;
    private List<User> users = new ArrayList<>();
    private List<User> usersLive = new ArrayList<>();
    private HomeRecyclerAdapter adapter;
    private MutableLiveData<List<User>> liveData=new MutableLiveData<>();
    private   int size=0;
    private double deposit,expence,balance,totalMill,millRate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db= FirebaseFirestore.getInstance();
        binding=FragmentHomePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        binding.progressBar.setVisibility(View.VISIBLE);
        adapter=new HomeRecyclerAdapter(users,0.0);
        binding.homeRecyclerView.setHasFixedSize(true);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.homeRecyclerView.setAdapter(adapter);

        binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_homePage_to_showMillFragment);
            }
        });

        binding.cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_homePage_to_showDepositExpenceFragment);
            }
        });
        memberListner =new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                binding.progressBar.setVisibility(View.VISIBLE);
                users.clear();
                usersLive.clear();
                deposit=0.0;
                totalMill=0.0;
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    binding.progressBar.setVisibility(View.GONE);
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                    size=value.size();
                    if (doc != null) {
                        try {
                            final User currentUser=new User();
                            final String name=doc.getString("name");
                            final String email=doc.getString("email");
                            currentUser.setName(name);
                            currentUser.setEmail(email);
                            users.add(currentUser);
                            db.collection("Member").document(email).collection("deposit")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            double balanc=0.0;
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                balanc=balanc+Double.parseDouble(doc.get("amount").toString());
                                            }
                                            currentUser.setBalance(balanc+"");
                                            deposit=deposit+balanc;
                                            //Toast.makeText(requireContext(), ""+currentUser.getBalance(), Toast.LENGTH_SHORT).show();
                                            db.collection("Member").document(email).collection("mill")
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            double mill=0.0;
                                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                mill=mill+Double.parseDouble(doc.get("mill").toString());
                                                            }
                                                            totalMill+=mill;
                                                            currentUser.setMill(mill);
                                                            users.add(currentUser);
                                                            usersLive.add(currentUser);
                                                            if (size == usersLive.size()){
                                                                liveData.postValue(usersLive);
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }catch (Exception err){
                            Toast.makeText(requireContext(), ""+err.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        getExpance();

        liveData.observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                binding.progressBar.setVisibility(View.GONE);
                binding.balanceAmount.setText(String.valueOf(new DecimalFormat("##.##").format(deposit-expence)));
                    binding.millRate.setText(new DecimalFormat("##.##").format(expence/totalMill));
                    adapter=new HomeRecyclerAdapter(users,expence/totalMill);
                    binding.homeRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

            }
        });
    }

    private void getExpance(){
        binding.progressBar.setVisibility(View.VISIBLE);
        expence=0.0;
        db.collection("Bazar")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : value) {
                            expence+=Double.parseDouble(doc.get("amount").toString());
                        }
                        binding.progressBar.setVisibility(View.GONE);
                        db.collection("Member")
                                .addSnapshotListener(memberListner);
                        }
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}