package com.parking.parkingguide.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 37266 on 2017/4/30.
 */

public class ToastUtils {
    private static Toast toast;
    public static void showToastLong(Context context,String content){
        if(toast==null){
            toast=Toast.makeText(context,content,Toast.LENGTH_LONG);
            toast.show();
        }else {
            toast.setText(content);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }
    public static void showToastShort(Context context,String content){
        if(toast==null){
            toast=Toast.makeText(context,content,Toast.LENGTH_SHORT);
            toast.show();
        }else {
            toast.setText(content);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
