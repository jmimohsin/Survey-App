package com.znsoftech.www.surveyapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.znsoftech.network.JsonParsor;
import com.znsoftech.utils.Config;
import com.znsoftech.utils.ProgressDialogClass;
import com.znsoftech.utils.ToastClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    EditText user_name,password;
    CheckBox checkBox;
    boolean isChecked=false;
    LoginTask loginTask=null;
    SharedPreferences sp=null;
    Intent i=null;
    String user,pwd;
    JSONObject jobj=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        user_name=(EditText)findViewById(R.id.editText2);
        password=(EditText)findViewById(R.id.editText);
        checkBox=(CheckBox)findViewById(R.id.checkBox);

        sp=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
    }

    public void action(View v){

        user=user_name.getText().toString();
        if(user.length()<3){
            ToastClass.showShort(LoginActivity.this, getString(R.string.enter_correct_user_name));
            return;
        }

        pwd=password.getText().toString();
        if(pwd.length()<3){
            ToastClass.showShort(LoginActivity.this, getString(R.string.enter_correct_password));
            return;
        }

        isChecked=checkBox.isChecked();
        loginTask=new LoginTask();
        loginTask.execute();
    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            ProgressDialogClass.show(LoginActivity.this, getString(R.string.loading));
            jobj=null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("user_name",user));
            param.add(new BasicNameValuePair("password",pwd));
            jobj= JsonParsor.makeHttpRequest(Config.login_url, "POST", param);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused){
            if(jobj!=null){
                try {
                    Log.e("Json data", jobj.toString());
                    if(jobj.getBoolean("status")){
                        sp.edit().putBoolean("isChecked", isChecked).apply();
                        sp.edit().putString("user_name", user).apply();
                        sp.edit().putString("password", pwd).apply();

                        Config.user_name=user;
                        Config.user_id=jobj.getString("SurveyorId");

                        i=new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        ToastClass.showShort(LoginActivity.this, getString(R.string.please_enter_correct_info));
                    }
                } catch (JSONException e) {
                    Log.e("Json Exception", e.toString());
                    ToastClass.showShort(LoginActivity.this, getString(R.string.error_in_login));
                }
            }else{
                ToastClass.showShort(LoginActivity.this, getString(R.string.error_in_login));
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

}
