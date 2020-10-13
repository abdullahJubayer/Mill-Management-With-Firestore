package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.bp.DataModel.Day;
import com.example.bp.DataModel.User;
import com.example.bp.databinding.FragmentSetUserBazarDayBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SetUserBazarDayFragment extends Fragment {

    private FragmentSetUserBazarDayBinding binding;
    private FirebaseFirestore db ;
    private EventListener<QuerySnapshot> eventListener;
    private List<User> users = new ArrayList<>();
    private List<String> name = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private boolean isAllOk=true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db= FirebaseFirestore.getInstance();
        binding=FragmentSetUserBazarDayBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        arrayAdapter=new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_1,name);
        binding.fridaySelection.setAdapter(arrayAdapter);

        eventListener=new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                users.clear();
                name.clear();
                name.add("Select One");
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        try {
                            User user=doc.toObject(User.class);
                            users.add(user);
                            name.add(user.getName());
                        }catch (Exception err){

                        }
                    }
                }
                Toast.makeText(requireContext(), ""+name.size()+"  "+users.size(), Toast.LENGTH_SHORT).show();
                arrayAdapter=new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_1,name);
                binding.saturdaySelection.setAdapter(arrayAdapter);
                binding.sundaySelection.setAdapter(arrayAdapter);
                binding.mondaySelection.setAdapter(arrayAdapter);
                binding.tuesdaySelection.setAdapter(arrayAdapter);
                binding.wednesdaySelection.setAdapter(arrayAdapter);
                binding.thursdaySelection.setAdapter(arrayAdapter);
                binding.fridaySelection.setAdapter(arrayAdapter);
            }
        };

        db.collection("Member")
                .addSnapshotListener(eventListener);

        AdapterView.OnItemSelectedListener itemSelectedListener=new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checker();
                if (isAllOk){
                    binding.saveBtn.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        binding.saturdaySelection.setOnItemSelectedListener(itemSelectedListener);
        binding.sundaySelection.setOnItemSelectedListener(itemSelectedListener);
        binding.mondaySelection.setOnItemSelectedListener(itemSelectedListener);
        binding.tuesdaySelection.setOnItemSelectedListener(itemSelectedListener);
        binding.wednesdaySelection.setOnItemSelectedListener(itemSelectedListener);
        binding.thursdaySelection.setOnItemSelectedListener(itemSelectedListener);
        binding.fridaySelection.setOnItemSelectedListener(itemSelectedListener);

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllOk){
                    saveData();
                }
            }
        });

    }

    private void saveData() {
        List<Day> days=new ArrayList<>();
        Day saturday=new Day("Saturday",binding.saturdaySelection.getSelectedItem().toString().trim(),users.get(binding.saturdaySelection.getSelectedItemPosition()-1).getEmail());
        Day sunday=new Day("Sunday",binding.sundaySelection.getSelectedItem().toString().trim(),users.get(binding.sundaySelection.getSelectedItemPosition()-1).getEmail());
        Day monday=new Day("Monday",binding.mondaySelection.getSelectedItem().toString().trim(),users.get(binding.mondaySelection.getSelectedItemPosition()-1).getEmail());
        Day tuesday=new Day("Tuesday",binding.tuesdaySelection.getSelectedItem().toString().trim(),users.get(binding.tuesdaySelection.getSelectedItemPosition()-1).getEmail());
        Day wednesday=new Day("Wednesday",binding.wednesdaySelection.getSelectedItem().toString().trim(),users.get(binding.wednesdaySelection.getSelectedItemPosition()-1).getEmail());
        Day thursday=new Day("Thursday",binding.thursdaySelection.getSelectedItem().toString().trim(),users.get(binding.thursdaySelection.getSelectedItemPosition()-1).getEmail());
        Day friday=new Day("Friday",binding.fridaySelection.getSelectedItem().toString().trim(),users.get(binding.fridaySelection.getSelectedItemPosition()-1).getEmail());

        days.add(saturday);
        days.add(sunday);
        days.add(monday);
        days.add(tuesday);
        days.add(wednesday);
        days.add(thursday);
        days.add(friday);
        for (Day day:days){
            db.collection("Week").document().set(day)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }

    }

    private void checker() {
       if (binding.saturdaySelection.getSelectedItem() == null || binding.saturdaySelection.getSelectedItemPosition() ==0){
           isAllOk=false;
       }
        else if (binding.sundaySelection.getSelectedItem() == null || binding.sundaySelection.getSelectedItemPosition() ==0){
            isAllOk=false;
        }
        else if (binding.mondaySelection.getSelectedItem() == null || binding.mondaySelection.getSelectedItemPosition() ==0){
            isAllOk=false;
        }
        else if (binding.tuesdaySelection.getSelectedItem() == null || binding.tuesdaySelection.getSelectedItemPosition() ==0){
            isAllOk=false;
        }
        else if (binding.wednesdaySelection.getSelectedItem() == null || binding.wednesdaySelection.getSelectedItemPosition() ==0){
            isAllOk=false;
        }
        else if (binding.thursdaySelection.getSelectedItem() == null || binding.thursdaySelection.getSelectedItemPosition() ==0){
            isAllOk=false;
        }else if (binding.fridaySelection.getSelectedItem() == null || binding.fridaySelection.getSelectedItemPosition() ==0){
            isAllOk=false;
        }
        else {
            isAllOk=true;
       }

    }
}