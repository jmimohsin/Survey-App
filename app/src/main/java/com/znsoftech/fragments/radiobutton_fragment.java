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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.znsoftech.utils.Config;
import com.znsoftech.utils.DBHelper;
import com.znsoftech.utils.GPSTracker;
import com.znsoftech.utils.ToastClass;
import com.znsoftech.www.surveyapp.MainStartSurveyActivity;
import com.znsoftech.www.surveyapp.R;
import com.znsoftech.www.surveyapp.StartSurvey;
import com.znsoftech.www.surveyapp.Thanks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class radiobutton_fragment extends Fragment {

    GPSTracker gps;
    int checked_radio_button_id=0;
    String checked_radio_button=null;
    Fragment fragment=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dynamic_options_fragment, container, false);

        Button next=(Button)view.findViewById(R.id.next);
        LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.linearLayout);

        RadioGroup radioGroup=new RadioGroup(getActivity());
        linearLayout.addView(radioGroup);

        TextView question=(TextView)view.findViewById(R.id.textView6);
        question.setTag(Config.survey_question_id);
        question.setText(Config.survey_question);

        String[] options_array=Config.survey_option.split(",");
        String[] options_id=Config.survey_option_id.split(",");


        for (int i=0; i<options_array.length; i++){
            final RadioButton radioButton=new RadioButton(getActivity());
            radioButton.setText(options_array[i]);
            radioButton.setTag(options_id[i]);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checked_radio_button_id=Integer.parseInt(radioButton.getTag().toString());
                    checked_radio_button=radioButton.getText().toString();
                }
            });

            radioGroup.addView(radioButton);
        }

        //for new fragment
        JSONArray jsonArray=Config.survey_question_array;
        JSONObject jsonObject= null;

        if(Config.next_question_index<Config.total_survey_question){
            try {
                //get next fragment question details
                jsonObject = jsonArray.getJSONObject(Config.next_question_index);
                JSONArray jsonArray1=jsonObject.getJSONArray("options");
                JSONObject jsonObject1=jsonArray1.getJSONObject(0);

                //get question type and select which fragment will open
                String question_type_id=jsonObject1.getString("QuestionOptionTypeId");
                int id=Integer.parseInt(question_type_id);
                fragment=Config.getQuestionTypeFragment(id);

                //next question
                Config.survey_question=jsonObject.getString("Question");
                Config.survey_question_id=jsonObject.getString("SurveyQuestionId");

                //question option(s)
                Config.survey_option=jsonObject1.getString("QuestionOption");
                Config.survey_option_id=jsonObject1.getString("QuestionOptionId");

                Config.next_question_index++;
            } catch (JSONException e) {
                Log.e("Json Exception", e.toString());
            }
        }else{
            next.setText(getString(R.string.done));
        }


        //for current fragment
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
//              dbHelper.insertIntoConsumer(f_name,m_name,l_name,_address,_city,_state,_country,_zipcode,mobile_number,phone_number,spinner_value,photoPath,email_id,date, Config.user_name,latitude,longitude,"");

                ToastClass.showShort(getActivity(),checked_radio_button+" Done!");
                if(fragment!=null){
                    FragmentManager fragmentManager=getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, null).commit();
                }else{
                    Intent intent=new Intent(getActivity(), Thanks.class);
                    intent.putExtra("total_time",Config.timer+"");
                    Config.timer=0;
                    startActivity(intent);
                    getActivity().finish();
                }
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
