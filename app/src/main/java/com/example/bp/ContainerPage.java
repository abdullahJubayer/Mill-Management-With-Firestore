package com.example.bp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bp.MySharedPreferences.MySharedPreference;
import com.example.bp.databinding.ContainerPageBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ContainerPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        View decorView = getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.drawerNavigation, navController);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.setDepositFragment || destination.getId() ==
                        R.id.setMillFragment || destination.getId() == R.id.setTodayBazar) {
                    binding.bottomNavigation.setVisibility(View.GONE);
                } else {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.drawerItem.homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.homePage);
                drawerHandler();
            }
        });

        binding.drawerItem.depositLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.fragmentShowDeposit);
                drawerHandler();
            }
        });
        binding.drawerItem.expencesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.showExpenceFragment);
                drawerHandler();
            }
        });
        binding.drawerItem.millLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.showMillFragment);
                drawerHandler();
            }
        });

        binding.drawerItem.loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.loginFragment);
                drawerHandler();
            }
        });
        binding.drawerItem.pdfLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PDFActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                drawerHandler();
            }
        });

        binding.drawerItem.setDepositLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.setDepositFragment);
                drawerHandler();
            }
        });

        binding.drawerItem.setMillLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.setMillFragment);
                drawerHandler();
            }
        });

        binding.drawerItem.setBazarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.setTodayBazar);
                drawerHandler();
            }
        });


        binding.drawerItem.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showConformationDialog(ContainerPage.this, "You Want to Logout from this Account...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        if (auth != null) {
                            auth.signOut();
                        }
                        navController.navigate(R.id.loginFragment);
                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                drawerHandler();
            }
        });


        final EventListener<QuerySnapshot> admin = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("TAG", "Listen failed.", error);
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        if (doc.get("email") != null && doc.get("password") != null) {
                            String email = doc.get("email").toString();
                            String password = doc.get("password").toString();
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                binding.drawerItem.loginLayout.setVisibility(View.GONE);
                                binding.drawerItem.logoutLayout.setVisibility(View.VISIBLE);
                            } else {
                                binding.drawerItem.loginLayout.setVisibility(View.VISIBLE);
                                binding.drawerItem.logoutLayout.setVisibility(View.GONE);
                            }
                            if (user != null && email.equals(user.getEmail())) {
                                binding.drawerItem.navigationUserSigninSignupTxt.setText(email);
                                binding.drawerItem.adminLayout.setVisibility(View.VISIBLE);

                            } else {
                                binding.drawerItem.navigationUserSigninSignupTxt.setText("Admin");
                                binding.drawerItem.adminLayout.setVisibility(View.GONE);

                            }
                        }
                    }
                }
            }
        };
        db.collection("Admin").addSnapshotListener(admin);
    }

    private void showConformationDialog(Context context, String message, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Are You Sure ?")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok" ,positive)
                .setNegativeButton("No",negative).show();
    }

    private void drawerHandler() {
        if (binding.navDrawerLayout.isOpen()) {
            binding.navDrawerLayout.close();
        }
    }
}