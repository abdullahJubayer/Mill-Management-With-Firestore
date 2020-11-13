package com.example.bp.Repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bp.DataModel.Expences;
import com.example.bp.DataModel.User;
import com.example.bp.Listener.ProgressbarListner;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeRepo {
    private FirebaseFirestore db ;
    private EventListener<QuerySnapshot> memberListner;
    private List<User> users=new ArrayList<>();
    private List<Expences> expences=new ArrayList<>();
    private MutableLiveData<List<User>> userLiveData=new MutableLiveData<>();
    private MutableLiveData<List<Expences>> expenceLiveData=new MutableLiveData<>();

    public HomeRepo(){
        db= FirebaseFirestore.getInstance();
    }

    public LiveData<List<User>> getUserList(final ProgressbarListner progressbarListner){
        memberListner =new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                progressbarListner.showProgress();
                users.clear();
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                    if (doc != null) {
                        String name=doc.getString("name") ==null?"":doc.getString("name");
                        String email=doc.getString("email") ==null?"":doc.getString("email");
                        Map<String,String> mills= (doc.get("mills") ==null?new HashMap<String,String>() : (Map<String, String>) doc.get("mills"));
                        Map<String,String> deposit= (doc.get("deposits") ==null?new HashMap<String,String>():(Map<String, String>) doc.get("deposits"));
                        users.add(new User(email,name,mills,deposit));
                    }
                }
                userLiveData.setValue(users);
                progressbarListner.hideProgress();
            }
        };

        db.collection("User").addSnapshotListener(memberListner);
        return userLiveData;
    }

    public MutableLiveData<List<Expences>> getExpences(final ProgressbarListner progressbarListner){
        memberListner =new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                progressbarListner.showProgress();
                expences.clear();
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                        String name=doc.getString("name") ==null?"":doc.getString("name");
                        String email=doc.getString("email") ==null?"":doc.getString("email");
                        String date=doc.getString("date") ==null?"":doc.getString("date");
                        String amount=doc.getString("amount") ==null?"":doc.getString("amount");
                        Expences exp=new Expences(name,email,date,amount);
                        expences.add(exp);
                }
                expenceLiveData.setValue(expences);
                progressbarListner.hideProgress();
            }
        };

        db.collection("Bazar").addSnapshotListener(memberListner);
        return expenceLiveData;
    }
}
