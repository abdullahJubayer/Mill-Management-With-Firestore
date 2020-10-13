package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.service.autofill.RegexValidator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.example.bp.DataModel.User;
import com.example.bp.databinding.FragmentSetDepositBinding;
import com.example.bp.util.DataUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SetDepositFragment extends Fragment {

    private FragmentSetDepositBinding binding;
    private FirebaseFirestore db ;
    private EventListener<QuerySnapshot> eventListener;
    private List<User> users = new ArrayList<>();
    private List<String> name = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        db= FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentSetDepositBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        arrayAdapter=new ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,name);
        binding.users.setAdapter(arrayAdapter);
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
                            User user=doc.toObject(User.class);
                            users.add(user);
                            name.add(user.getName());
                        }catch (Exception err){

                        }
                    }
                }

                arrayAdapter=new ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1,name);
                binding.users.setAdapter(arrayAdapter);
            }
        };

        db.collection("Member")
                .addSnapshotListener(eventListener);

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount=binding.amoutTxt.getText().toString();
                if (!amount.isEmpty() && amount.matches("^[0-9]*$")){
                    binding.saveButton.setEnabled(false);
                    Map<String ,String> deposit=new HashMap<>();
                    deposit.put("amount",amount);
                    deposit.put("date",new DataUtil().getDate());
                    db.collection("Member").document(users.get(binding.users.getSelectedItemPosition()).getEmail())
                            .collection("deposit")
                            .document(new DataUtil().getDate())
                            .set(deposit).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.saveButton.setEnabled(true);
                        }
                    });
                }else {
                    binding.amoutTxt.setError("Data Not Valid");
                }
            }
        });
    }
}