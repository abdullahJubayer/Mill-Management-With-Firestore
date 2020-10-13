package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
import com.example.bp.DataModel.Expences;
import com.example.bp.DataModel.Mill;
import com.example.bp.DataModel.User;
import com.example.bp.R;
import com.example.bp.databinding.FragmentShowDepositExpenceBinding;
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

public class ShowDepositExpenceFragment extends Fragment {

    private FragmentShowDepositExpenceBinding binding;
    private EventListener<QuerySnapshot> memberListner;
    private List<User> usersLive = new ArrayList<>();
    private List<Expences> expencesList = new ArrayList<>();
    private MutableLiveData<List<User>> liveData=new MutableLiveData<>();
    private double deposit,expence;
    private   int size=0;
    private FirebaseFirestore db ;
    private HomeRecyclerAdapter adapter;
    private List<String> names = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db= FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentShowDepositExpenceBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.progressBar.setVisibility(View.VISIBLE);
        adapter=new HomeRecyclerAdapter(usersLive);
        binding.homeRecyclerView.setHasFixedSize(true);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.homeRecyclerView.setAdapter(adapter);

        arrayAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, names);
        binding.usersSelector.setAdapter(arrayAdapter);

        memberListner =new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                usersLive.clear();
                names.clear();
                names.add("Select None");
                deposit=0.0;
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
                            names.add(name);
                            final String email=doc.getString("email");
                            currentUser.setName(name);
                            currentUser.setEmail(email);
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
                                            usersLive.add(currentUser);
                                            deposit=deposit+balanc;
                                            if (size == usersLive.size()){
                                                liveData.postValue(usersLive);
                                            }
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
        db.collection("Member")
                .addSnapshotListener(memberListner);

        liveData.observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                binding.progressBar.setVisibility(View.GONE);
                prepareSpinner();
                setListner(users);
                binding.balanceAmount.setText(deposit+"");
                binding.millRate.setText(new DecimalFormat("##.##").format(expence));
                adapter=new HomeRecyclerAdapter(users);
                binding.homeRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });

        binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expencesList != null){
                    adapter=new HomeRecyclerAdapter(expencesList,0);
                    binding.homeRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        binding.cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usersLive != null){
                    adapter=new HomeRecyclerAdapter(usersLive);
                    binding.homeRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getExpance(){
        expence=0.0;
        expencesList.clear();
        db.collection("Bazar")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : value) {
                            Expences expences=doc.toObject(Expences.class);
                            expencesList.add(expences);
                            expence+=Double.parseDouble(expences.getAmount());
                        }
                    }
                });
    }

    private void prepareSpinner() {
        arrayAdapter=new ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,names);
        binding.usersSelector.setAdapter(arrayAdapter);
    }

    private void setListner(final List<User> users) {
        final List<Deposit> deposits=new ArrayList<>();
        binding.usersSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.progressBar.setVisibility(View.VISIBLE);
                if (binding.usersSelector.getSelectedItemPosition() != 0) {
                    for (User user:users){
                        if (user.getName().equals(binding.usersSelector.getSelectedItem().toString())){
                            db.collection("Member").document(user.getEmail()).collection("deposit")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    deposits.clear();
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        deposits.add(doc.toObject(Deposit.class));
                                    }
                                    adapter=new HomeRecyclerAdapter(deposits,0.1f);
                                    binding.homeRecyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    binding.progressBar.setVisibility(View.GONE);
                                }
                            });
                            return;
                        }
                    }
                }else {
                    adapter=new HomeRecyclerAdapter(users);
                    binding.homeRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}