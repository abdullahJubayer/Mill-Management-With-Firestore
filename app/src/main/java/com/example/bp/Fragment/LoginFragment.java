package com.example.bp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bp.DataModel.User;
import com.example.bp.MySharedPreferences.MySharedPreference;
import com.example.bp.R;
import com.example.bp.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.bp.MySharedPreferences.MySharedPreference.admin;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseFirestore db ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db=FirebaseFirestore.getInstance();
        binding=FragmentLoginBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {


        binding.emailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null){
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    String email=editable.toString().replaceAll(" ","");
                    if (!email.matches(emailPattern) && !email.endsWith("@gmail.com")){
                        binding.loginBtn.setEnabled(false);
                    }else {
                        binding.loginBtn.setEnabled(true);
                    }
                }
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                    db.collection("Admin").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult() != null){
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    if (doc.get("email") != null && doc.get("password") != null){
                                        String email =doc.get("email").toString();
                                        String password =doc.get("password").toString();
                                        Editable enterEmail =binding.emailTxt.getText();
                                        Editable enterPassword =binding.passwordTxt.getText();
                                        if ( enterEmail != null && enterEmail.toString().equals(email) && enterPassword != null && enterPassword.toString().equals(password)){
                                            MySharedPreference.getInstance(requireContext()).edit()
                                                    .putString(MySharedPreference.admin,enterEmail.toString())
                                                    .putString(MySharedPreference.password,enterPassword.toString())
                                                    .apply();
                                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homePage);
                                        }else {
                                            Toast.makeText(requireContext(), "Email & Password Not Match", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(requireContext(), "Email & Password Not Match", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
            }
        });
    }
}