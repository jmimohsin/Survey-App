package com.znsoftech.www.surveyapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;;

import com.znsoftech.network.CheckNetwork;
import com.znsoftech.network.JsonParsor;
import com.znsoftech.utils.Config;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {

    private int time_out=2000;
    Intent i=null;
    SharedPreferences sp=null;
    LoginTask loginTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        boolean isChecked=sp.getBoolean("isChecked",false);
        if(isChecked){
            if(CheckNetwork.isAvailable(SplashActivity.this)){
                loginTask=new LoginTask();
                loginTask.execute();
            }else{
                delay_method();
            }

        }else{
            delay_method();
        }
    }

    private void delay_method(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                i=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, time_out);
    }

    private class LoginTask extends AsyncTask<Void, Void, Void>{

        JSONObject jobj=null;

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("user_name",sp.getString("user_name",null)));
            param.add(new BasicNameValuePair("password",sp.getString("password", null)));
            jobj= JsonParsor.makeHttpRequest(Config.login_url,"POST",param);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused){
            if(jobj!=null){
                try {
                    if(jobj.getBoolean("status")){

                        Config.user_name=sp.getString("user_name",null);
                        Config.user_id=jobj.getString("SurveyorId");

                        i=new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    Log.e("Json Exception", e.toString());
                    i=new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }else{
                i=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy(){
        if (loginTask != null && loginTask.getStatus() != AsyncTask.Status.FINISHED)
            loginTask.cancel(true);
        super.onDestroy();
    }

}
