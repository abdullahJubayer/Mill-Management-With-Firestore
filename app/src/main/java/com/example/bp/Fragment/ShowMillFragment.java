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
import com.example.bp.Adapter.HomeRecyclerAdapter;
import com.example.bp.DataModel.Mill;
import com.example.bp.DataModel.User;
import com.example.bp.databinding.FragmentShowMillFragmentBinding;
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


public class ShowMillFragment extends Fragment {

    private FragmentShowMillFragmentBinding binding;
    private MutableLiveData<List<User>> liveData = new MutableLiveData<>();
    private double mill;
    private int size = 0;
    private FirebaseFirestore db;
    private HomeRecyclerAdapter adapter;
    private EventListener<QuerySnapshot> memberListner;
    private List<User> usersLive = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        binding = FragmentShowMillFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.progressBar.setVisibility(View.VISIBLE);
        adapter = new HomeRecyclerAdapter(usersLive);
        binding.millRecyclerView.setHasFixedSize(true);
        binding.millRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.millRecyclerView.setAdapter(adapter);

        arrayAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, names);
        binding.users.setAdapter(arrayAdapter);

        memberListner = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                usersLive.clear();
                mill = 0.0;
                names.clear();
                names.add("Select None");
                if (e != null) {
                    binding.progressBar.setVisibility(View.GONE);
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                    size = value.size();
                    if (doc != null) {
                        try {
                            final User currentUser = new User();
                            final String name = doc.getString("name");
                            names.add(name);
                            final String email = doc.getString("email");
                            currentUser.setName(name);
                            currentUser.setEmail(email);
                            db.collection("Member").document(email).collection("mill")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            double millAmount = 0.0;
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                String mill=doc.get("mill").toString();
                                                millAmount = millAmount + Double.parseDouble(mill);
                                            }
                                            currentUser.setMill(millAmount);
                                            usersLive.add(currentUser);
                                            mill = mill + millAmount;
                                            if (size == usersLive.size()) {
                                                liveData.postValue(usersLive);
                                            }
                                        }
                                    });
                        } catch (Exception err) {
                            Toast.makeText(requireContext(), "" + err.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        db.collection("Member")
                .addSnapshotListener(memberListner);

        liveData.observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                binding.progressBar.setVisibility(View.GONE);
                prepareSpinner();
                setListner(users);
                binding.mill.setText(new DecimalFormat("##.##").format(mill));
                adapter = new HomeRecyclerAdapter(ShowMillFragment.this, users);
                binding.millRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });

    }

    private void setListner(final List<User> users) {
        final List<Mill> mills=new ArrayList<>();
        binding.users.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.progressBar.setVisibility(View.VISIBLE);
                if (binding.users.getSelectedItemPosition() != 0) {
                    for (User user:users){
                        if (user.getName().equals(binding.users.getSelectedItem().toString())){
                            binding.mill.setText(new DecimalFormat("##.##").format(user.getMill()));
                            db.collection("Member").document(user.getEmail()).collection("mill")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    mills.clear();
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        mills.add(doc.toObject(Mill.class));
                                    }
                                    adapter=new HomeRecyclerAdapter(mills,binding.users.getSelectedItem().toString());
                                    binding.millRecyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    binding.progressBar.setVisibility(View.GONE);
                                }
                            });
                            return;
                        }
                    }
                }else {
                    adapter = new HomeRecyclerAdapter(ShowMillFragment.this, users);
                    binding.millRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void prepareSpinner() {
        arrayAdapter=new ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,names);
        binding.users.setAdapter(arrayAdapter);
    }
}