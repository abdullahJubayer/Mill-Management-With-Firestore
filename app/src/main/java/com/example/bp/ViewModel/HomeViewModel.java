package com.example.bp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bp.DataModel.Expences;
import com.example.bp.DataModel.User;
import com.example.bp.Listener.ProgressbarListner;
import com.example.bp.Repository.HomeRepo;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private HomeRepo homeRepo;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        homeRepo=new HomeRepo();
    }

    public LiveData<List<User>> getUserList(ProgressbarListner progressbarListner){
        return homeRepo.getUserList(progressbarListner);
    }

    public MutableLiveData<List<Expences>> getExpences(ProgressbarListner progressbarListner){
        return homeRepo.getExpences(progressbarListner);
    }
}
