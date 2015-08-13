package com.znsoftech.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Mohsin on 12-08-2015.
 */
public class ToastClass {

    public static void showShort(Context context, String message){

        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String message){

        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
