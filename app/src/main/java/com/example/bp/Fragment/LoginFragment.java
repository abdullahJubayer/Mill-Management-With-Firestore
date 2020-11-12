package com.example.bp.Fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.TransitionManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bp.DataModel.User;
import com.example.bp.MySharedPreferences.MySharedPreference;
import com.example.bp.R;
import com.example.bp.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import static com.example.bp.MySharedPreferences.MySharedPreference.admin;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        binding.signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSignUpLayout(R.layout.login_layout_2);
            }
        });

        binding.backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSignUpLayout(R.layout.fragment_login);
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSignUpLayout(R.layout.fragment_login);
            }
        });

        binding.signUpButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSignUpLayout(R.layout.login_loading);
                signUp();
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSignUpLayout(R.layout.login_loading);
                signIn();
            }
        });


    }

    private void signUp() {
        final String name = binding.userEt.getText().toString();
        final String email = binding.emailEt.getText().toString();
        String pass = binding.passEt.getText().toString();

        if (validateEmail(email) && !name.replaceAll(" ","").isEmpty())
            if (validatePassword(pass))
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful() && task.isComplete()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            setSignUpLayout(R.layout.fragment_login);
                                            showSnackbar("A Verification Email Send to Your Gmail Account.Please Check and Verify Your Account",false);
                                        }
                                    });
                                    User user=new User(email,name,new HashMap<String, String>(),new HashMap<String, String>());
                                    db.collection("User").document(email)
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    showSnackbar("User Created",false);
                                                }
                                            });
                                } else {
                                    showSnackbar("Error !",true);
                                }
                            }
                        });
            else showSnackbar("Password Not Valid!",true);

        else showSnackbar("Email Not Valid!",true);
    }

    private void signIn(){
        String email = binding.emailEt.getText().toString();
        String pass = binding.passEt.getText().toString();
        if (validateEmail(email))
            if (validatePassword(pass))
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful() && task.isComplete()) {
                                    if (mAuth.getCurrentUser().isEmailVerified()){
                                        Navigation.findNavController(binding.rootLayout).navigate(R.id.homePage);
                                        setSignUpLayout(R.layout.fragment_login);
                                    }else {
                                        showSnackbar("Email Not Verified",true);
                                    }
                                } else {
                                    showSnackbar("Error !",true);
                                }
                            }
                        });
            else showSnackbar("Password Not Valid!",true);

        else showSnackbar("Email Not Valid!",true);
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

    private boolean validateEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.replaceAll(" ", "").matches(emailPattern) && email.replaceAll(" ", "").endsWith("@gmail.com");
    }

    private boolean validatePassword(String pass) {
        String passPattern = "^[A-Za-z0-9_.]+$";
        return pass.matches(passPattern) && pass.length() >= 6;
    }

    private void setSignUpLayout(int layout_id) {
        ConstraintSet set = new ConstraintSet();
        set.clone(requireActivity(), layout_id);
        TransitionManager.beginDelayedTransition(binding.rootLayout);
        set.applyTo(binding.rootLayout);
    }

}