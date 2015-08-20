package com.znsoftech.www.surveyapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.znsoftech.network.JsonParsor;
import com.znsoftech.utils.Config;
import com.znsoftech.utils.ProgressDialogClass;
import com.znsoftech.utils.ToastClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StartSurvey extends ActionBarActivity {

    TextView textView2;
    LoginTask loginTask=null;
    SharedPreferences sp=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_survey);

        android.support.v7.app.ActionBar bar=getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.string.action_bar_color))));
        bar.setTitle(getString(R.string.app_name));

        TextView textView1=(TextView)findViewById(R.id.textView1);
        textView2=(TextView)findViewById(R.id.textView2);

        textView1.setText(Config.survey_name);
        textView2.setVisibility(View.INVISIBLE);

        sp=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String jsonArray=sp.getString(Config.survey_id,null);

        if(jsonArray==null){
            loginTask=new LoginTask();
            loginTask.execute();
        }else{
            JSONArray jsonArray1= null;
            try {
                jsonArray1 = new JSONArray(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Config.total_survey_question=jsonArray.length();
            Config.next_question_index=0;
            Config.survey_question_array=jsonArray1;
        }

    }


    private class LoginTask extends AsyncTask<Void, Void, Void> {

        JSONObject jobj=null;

        @Override
        protected void onPreExecute(){
            ProgressDialogClass.show(StartSurvey.this, getString(R.string.loading));
            jobj=null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("SurveyId",Config.survey_id));
            jobj= JsonParsor.makeHttpRequest(Config.question_url, "POST", param);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused){
            if(jobj!=null){
                try {
                    if(jobj.getBoolean("status")){
                        JSONArray jsonArray=jobj.getJSONArray("data");

                        Config.total_survey_question=jsonArray.length();
                        Config.next_question_index=0;
                        Config.survey_question_array=jsonArray;
                        sp.edit().putString(Config.survey_id,jsonArray.toString()).apply();

//                        JSONObject jsonObject=jsonArray.getJSONObject(Config.next_question_index);
//                        JSONArray jsonArray1=jsonObject.getJSONArray("options");
//                        JSONObject jsonObject1=jsonArray1.getJSONObject(0);
//                        String question_type=jsonObject1.getString("QuestionOptionTypeId");
//

                    }else{
                        Log.e("Status false", jobj.toString());
                        ToastClass.showShort(StartSurvey.this,getString(R.string.no_question_available));
                        finish();
                    }
                } catch (JSONException e) {
                    Log.e("Json Exception", e.toString());
                    finish();
                }
            }else{
                Log.e("Json null", "Json object null");
                finish();
            }
            ProgressDialogClass.hide();
        }
    }

    @Override
    protected void onDestroy(){
        if (loginTask != null && loginTask.getStatus() != AsyncTask.Status.FINISHED)
            loginTask.cancel(true);
        super.onDestroy();
    }

    public void action(View view){
        if(view.getId()==R.id.button2){
            Config.timer=0;
            Intent i=new Intent(StartSurvey.this, MainStartSurveyActivity.class);
            startActivity(i);
        }
    }
}
