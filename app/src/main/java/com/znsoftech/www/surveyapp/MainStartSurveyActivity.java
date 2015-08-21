package com.znsoftech.www.surveyapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.znsoftech.fragments.GeneralInfoOne;
import com.znsoftech.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainStartSurveyActivity extends ActionBarActivity {

    int total_time=0;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survay_main_fragment);

        android.support.v7.app.ActionBar bar=getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.string.action_bar_color))));
        bar.setTitle(getString(R.string.app_name));

        TextView surveyName=(TextView)findViewById(R.id.textView4);
        surveyName.setText(Config.survey_name);

        final TextView timerText=(TextView)findViewById(R.id.textView5);

        total_time=Config.timer;

        int hours=0,minutes=0,seconds=0;
        int total=total_time;

        if(total>=60*60) {
            hours = total / (60 * 60);
            total=total-hours*60*60;
        }

        if(total>=60) {
            minutes = total / 60;
            total=total-minutes*60;
        }

        seconds=total;

        timerText.setText(hours+":"+minutes+":"+seconds);

        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int hours=0,minutes=0,seconds=0;
                        int total=total_time;

                        if(total>=60*60) {
                            hours = total / (60 * 60);
                            total=total-hours*60*60;
                        }

                        if(total>=60) {
                            minutes = total / 60;
                            total=total-minutes*60;
                        }

                        seconds=total;

                        timerText.setText(hours+":"+minutes+":"+seconds);

                        total_time++;
                        Config.timer++;
                    }
                });
            }
        }, 1000, 1000);


        JSONArray jsonArray=Config.survey_question_array;
        JSONObject jsonObject= null;
        Fragment fragment=null;

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
        }

        if(fragment!=null){
            FragmentManager fragmentManager=getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, null).commit();
        }

    }

    @Override
    protected void onDestroy(){
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        Config.next_question_index=0;
        super.onBackPressed();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_start_survey, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
