package com.example.bp.MySharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MySharedPreference {

    public static final String admin = "ADMIN";
    public static final String password = "PASSWORD";
    private static SharedPreferences sharedPreferences=null;
    private MySharedPreference(){

    }

    public static synchronized SharedPreferences getInstance(Context context){
        if (sharedPreferences==null){
            return sharedPreferences=context.getSharedPreferences("com.utils.MySharedPreferences",Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static SharedPreferences.Editor editor() throws  Exception{
        if (sharedPreferences != null){
            return sharedPreferences.edit();
        }else {
            throw new IllegalAccessException ("SharedPreferences Should Not be Null");
        }
    }

    public static void saveArrayList(SharedPreferences.Editor editor, ArrayList<String> list, String key){

        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<String> getArrayList(SharedPreferences prefs,String key){
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
