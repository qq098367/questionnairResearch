package com.example.administrator.ttt_test.util.date;

import java.text.SimpleDateFormat;

/**
 * Created by Acer on 2017/4/25.
 */

public class MyDateTransform {
    public static String getDateFromLong(long time){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String temp=simpleDateFormat.format(time);
        String result=temp.substring(0,4)+"/"+temp.substring(5,7)+"/"+temp.substring(8,10);
        return result;
    }
}
