package com.example.bp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bp.MySharedPreferences.MySharedPreference;
import com.example.bp.databinding.ContainerPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.bp.MySharedPreferences.MySharedPreference.admin;

public class ContainerPage extends AppCompatActivity {

    private ContainerPageBinding binding;
    private NavController navController;
    private FirebaseFirestore db ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ContainerPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db= FirebaseFirestore.getInstance();

        navController= Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.drawerNavigation,navController);
        NavigationUI.setupWithNavController(binding.bottomNavigation,navController);

        binding.drawerItem.homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.homePage);
                drawerHandler();
            }
        });

//        binding.drawerItem.depositLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.depositFragment);
//                drawerHandler();
//            }
//        });
//        binding.drawerItem.setMillLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.setMillFragment);
//                drawerHandler();
//            }
//        });
//        binding.drawerItem.setBazarLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.setTodayBazar);
//                drawerHandler();
//            }
//        });
//
//        binding.drawerItem.loginLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.action_homePage_to_loginFragment);
//                drawerHandler();
//            }
//        });
//        binding.drawerItem.pdfLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(),PDFActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getApplicationContext().startActivity(intent);
//                drawerHandler();
//            }
//        });
//        binding.drawerItem.logoutLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MySharedPreference.getInstance(ContainerPage.this).edit().clear().apply();
//                Toast.makeText(ContainerPage.this, "Log Out", Toast.LENGTH_SHORT).show();
//                drawerHandler();
//            }
//        });

        final EventListener<QuerySnapshot> admin=new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("TAG", "Listen failed.", error);
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        if (doc.get("email") != null && doc.get("password") != null){
                            String email =doc.get("email").toString();
                            String password =doc.get("password").toString();
                            String storeEmail =MySharedPreference.getInstance(ContainerPage.this).getString(MySharedPreference.admin,null);
                            String storePassword =MySharedPreference.getInstance(ContainerPage.this).getString(MySharedPreference.password,null);
                            if ( storeEmail != null && storeEmail.equals(email) && storePassword != null && storePassword.equals(password)){
                                binding.drawerItem.depositLayout.setVisibility(View.VISIBLE);
                                binding.drawerItem.setMillLayout.setVisibility(View.VISIBLE);
                                binding.drawerItem.setBazarLayout.setVisibility(View.VISIBLE);
                                binding.drawerItem.logoutLayout.setVisibility(View.VISIBLE);
                                binding.drawerItem.loginLayout.setVisibility(View.GONE);
                                binding.drawerItem.navigationUserSigninSignupTxt.setText(email);
                            }else {
                                binding.drawerItem.depositLayout.setVisibility(View.GONE);
                                binding.drawerItem.setMillLayout.setVisibility(View.GONE);
                                binding.drawerItem.setBazarLayout.setVisibility(View.GONE);
                                binding.drawerItem.logoutLayout.setVisibility(View.GONE);
                                binding.drawerItem.loginLayout.setVisibility(View.VISIBLE);
                                binding.drawerItem.navigationUserSigninSignupTxt.setText("Guest");
                                MySharedPreference.getInstance(ContainerPage.this).edit().clear().apply();
                                Toast.makeText(ContainerPage.this, "Log Out", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        };
        db.collection("Admin").addSnapshotListener(admin);
    }

    private void drawerHandler(){
        if (binding.navDrawerLayout.isOpen()){
            binding.navDrawerLayout.close();
        }
    }
}