package com.example.bp.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.example.bp.DataModel.User;
import com.example.bp.databinding.FragmentSetTodayBazarBinding;
import com.example.bp.util.DataUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SetTodayBazar extends Fragment {

    private FragmentSetTodayBazarBinding binding;
    private FirebaseFirestore db ;
    private EventListener<QuerySnapshot> eventListener;
    private List<User> users = new ArrayList<>();
    private List<String> name = new ArrayList<>();
    private final String path="Bazar";
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db= FirebaseFirestore.getInstance();
        binding =FragmentSetTodayBazarBinding.inflate(inflater,container,false);
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
                binding.saveButton.setEnabled(false);
                String amount=binding.amoutTxt.getText().toString();
                if (!amount.isEmpty() && amount.matches("^[0-9]*$")){
                    Map<String ,String> deposit=new HashMap<>();
                    deposit.put("name",users.get(binding.users.getSelectedItemPosition()).getName());
                    deposit.put("email",users.get(binding.users.getSelectedItemPosition()).getEmail());
                    deposit.put("amount",amount);
                    deposit.put("date",new DataUtil().getDate());
                    db.collection(path).document(new DataUtil().getDate())
                            .set(deposit).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }else {
                    binding.amoutTxt.setError("Data Not Valid");
                }
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}