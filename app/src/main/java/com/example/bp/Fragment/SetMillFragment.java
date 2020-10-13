package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bp.Adapter.SetMillAdapter;
import com.example.bp.DataModel.User;
import com.example.bp.databinding.FragmentSetMillBinding;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SetMillFragment extends Fragment {

    private FragmentSetMillBinding binding;
    private FirebaseFirestore db ;
    private EventListener<QuerySnapshot> eventListener;
    private List<User> users = new ArrayList<>();
    private SetMillAdapter adapter;
    private final String path="Mill";
    private final String field="mill";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db= FirebaseFirestore.getInstance();
        binding=FragmentSetMillBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter=new SetMillAdapter(users,path,field);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(SetMillFragment.this.getContext()));
        binding.homeRecyclerView.setHasFixedSize(true);
        binding.homeRecyclerView.setAdapter(adapter);

        eventListener=new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                users.clear();
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        try {
                            users.add(doc.toObject(User.class));
                        }catch (Exception err){

                        }
                    }
                }

                adapter=new SetMillAdapter(users,path,field);
                binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(SetMillFragment.this.getContext()));
                binding.homeRecyclerView.setHasFixedSize(true);
                binding.homeRecyclerView.setAdapter(adapter);
            }
        };

        db.collection("Member")
                .addSnapshotListener(eventListener);
    }
}