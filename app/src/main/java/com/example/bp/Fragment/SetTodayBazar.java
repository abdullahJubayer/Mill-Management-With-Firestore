package com.example.bp.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.bp.DataModel.Expences;
import com.example.bp.DataModel.User;
import com.example.bp.Listener.ProgressbarListner;
import com.example.bp.R;
import com.example.bp.ViewModel.HomeViewModel;
import com.example.bp.databinding.FragmentSetTodayBazarBinding;
import com.example.bp.util.DataPicker;
import com.example.bp.util.DataUtil;
import com.example.bp.util.PassDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SetTodayBazar extends DialogFragment implements ProgressbarListner {

    private FragmentSetTodayBazarBinding binding;
    private HomeViewModel homeViewModel;
    private FirebaseFirestore db ;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db= FirebaseFirestore.getInstance();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding= FragmentSetTodayBazarBinding.inflate(inflater,container,false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.rootLayout).popBackStack();
            }
        });

        binding.toolbar.setTitle("Add Today Bazar");
        binding.toolbar.inflateMenu(R.menu.save_menu);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId()==R.id.action_save){

                    final String name=binding.selectedDepositor.getSelectedItem().toString();
                    final String email=binding.depositorEmail.getText().toString();
                    final String date=binding.depositDate.getText().toString();
                    final String amount=binding.amountEt.getText().toString();
                    showConformationDialog(requireContext(), "You Are Saving :: Name: " + name + " " + "Amount :" + amount+"  for Date:"+date, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            if (name != null)
                                if (date != null)
                                    if (amount !=null){
                                        Expences expences=new Expences(name,email,date,amount);
                                        db.collection("Bazar").document()
                                                .set(expences).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                showSnackbar("Data save",false);
                                                Navigation.findNavController(binding.rootLayout).popBackStack();
                                                dialogInterface.dismiss();
                                            }
                                        });
                                    }
                                    else showSnackbar("Amount Null",true);
                                else showSnackbar("Date Null",true);
                            else showSnackbar("Name Null",true);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }
                return true;
            }
        });

        binding.depositDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DataPicker(new PassDate() {
                    @Override
                    public void date(int year, int month, int day) {
                        binding.depositDate.setText(day+"/"+month+"/"+year);
                    }
                });
                newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        homeViewModel.getUserList(this).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(final List<User> users) {
                List<String> name=new ArrayList<>();
                name.clear();
                name.add("");
                for (User nn:users){
                    name.add(nn.getName());
                }

                ArrayAdapter<String> adapter=new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,name);
                binding.selectedDepositor.setAdapter(adapter);
                binding.selectedDepositor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (binding.selectedDepositor.getSelectedItemPosition() !=0){
                            binding.depositorEmail.setText(users.get(i-1).getEmail());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
    }

    private void showSnackbar(String s, boolean isError) {
        Snackbar snackbar = Snackbar.make(binding.rootLayout, s, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_light));
        } else {
            snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_green_light));
        }
        snackbar.show();
    }

    private void showConformationDialog(Context context, String message, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Are You Sure ?")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok" ,positive)
                .setNegativeButton("No",negative).show();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }
}