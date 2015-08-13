package com.znsoftech.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Mohsin on 12-08-2015.
 */
public class ProgressDialogClass {
    static ProgressDialog pd;

    public static void show(Context context, String message) {
        if (context != null) {
            pd = new ProgressDialog(context);
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(message);
            pd.show();
        }
    }

    public static void hide() {
        pd.dismiss();
    }
}
