package com.znsoftech.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.znsoftech.utils.Config;
import com.znsoftech.utils.DBHelper;
import com.znsoftech.utils.GPSTracker;
import com.znsoftech.utils.ToastClass;
import com.znsoftech.www.surveyapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class checkbox_fragment extends Fragment {

    GPSTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dynamic_options_fragment, container, false);

        Button next=(Button)view.findViewById(R.id.next);
        LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.linearLayout);

        CheckBox checkBox=new CheckBox(getActivity());
        checkBox.setText("One");
        CheckBox checkBox1=new CheckBox(getActivity());
        checkBox1.setText("Two");
        linearLayout.addView(checkBox);
        linearLayout.addView(checkBox1);

        gps=new GPSTracker(getActivity());

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(gps.canGetLocation()){
                    gps.getLocation();
                }else{
                    gps.showSettingsAlert();
                    return;
                }

                String latitude=gps.getLatitude()+"";
                String longitude=gps.getLongitude()+"";

//                if(f_name.length()<2 && m_name.length()<2 && l_name.length()<2){
//                    ToastClass.showShort(getActivity(),getString(R.string.enter_consumer_name));
//                    return;
//                }

                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                DBHelper dbHelper=new DBHelper(getActivity(),"Survey.db",null,1);
//                dbHelper.insertIntoConsumer(f_name,m_name,l_name,_address,_city,_state,_country,_zipcode,mobile_number,phone_number,spinner_value,photoPath,email_id,date, Config.user_name,latitude,longitude,"");

                ToastClass.showShort(getActivity(),"Done!");
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        gps.stopUsingGPS();
    }

}
