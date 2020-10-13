package com.example.bp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataUtil {

   private  Date c ;

    private  Date getDateUtils(){
        if (c==null){
            c= Calendar.getInstance().getTime();
        }
        return c;
    }

    public  String getDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return df.format(getDateUtils());
    }

    public  String getDayName(){
        SimpleDateFormat df = new SimpleDateFormat("EEE", Locale.getDefault());
        return df.format(getDateUtils());
    }

    public  String getDay(){
        SimpleDateFormat df3 = new SimpleDateFormat("dd", Locale.getDefault());
        return df3.format(getDateUtils());
    }

    public  String getMonth(){
        SimpleDateFormat df2 = new SimpleDateFormat("MM", Locale.getDefault());
        return  df2.format(getDateUtils());
    }
    public  String getYear(){
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy", Locale.getDefault());
        return df1.format(getDateUtils());
    }
}
