package com.znsoftech.www.surveyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.znsoftech.network.JsonParsor;
import com.znsoftech.utils.Config;
import com.znsoftech.utils.ProgressDialogClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity
{
    ArrayList<HashMap<String, String>> array_list = new ArrayList<HashMap<String, String>>();
    ListView listView=null;
    Add_Adapter add_adapter=null;
    LoginTask loginTask=null;
    JSONObject jobj=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar=getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.string.action_bar_color))));
        bar.setTitle(getString(R.string.app_name));

        try {
            ViewConfiguration config = ViewConfiguration.get(this);

            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            //Why multiple objects are used here
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }


        listView=(ListView)findViewById(R.id.listView);
        add_adapter=new Add_Adapter(MainActivity.this);

        listView.setAdapter(add_adapter);

        loginTask=new LoginTask();
        loginTask.execute();
    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            ProgressDialogClass.show(MainActivity.this, getString(R.string.loading));
            array_list.clear();
            jobj=null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("user_id", Config.user_id));
            jobj= JsonParsor.makeHttpRequest(Config.survay_main_page_url, "POST", param);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused){
            if(jobj!=null){
                try {
                    if(jobj.getBoolean("status")){
                        Log.e("Json data", jobj.toString());
                        JSONArray jsonArray=jobj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, String> hashmap = new HashMap<String, String>();
                            JSONObject j_obj = jsonArray.getJSONObject(i);

                            hashmap.put("SurveyName", j_obj.getString("SurveyName"));
                            hashmap.put("SurveyTypeName", j_obj.getString("SurveyTypeName"));
                            hashmap.put("SurveyId", j_obj.getString("SurveyId"));

                            array_list.add(hashmap);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("Json Exception", e.toString());
                }
            }else{
                Log.e("Json Error","Json object is null");
            }
            add_adapter.notifyDataSetChanged();
            ProgressDialogClass.hide();
        }
    }

    @Override
    protected void onDestroy(){
        if (loginTask != null && loginTask.getStatus() != AsyncTask.Status.FINISHED)
            loginTask.cancel(true);
        super.onDestroy();
    }

    static class ViewHolder {
        LinearLayout linearLayout;
        TextView textView1, textView2, textView3;
    }

    private class Add_Adapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public Add_Adapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (array_list.size() > 0) {
                return array_list.size();
            }

            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = mInflater.inflate(R.layout.survey_list_items, null);
                holder = new ViewHolder();
                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
                holder.textView3 = (TextView) convertView.findViewById(R.id.textView3);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView1.setText(array_list.get(position).get("SurveyName"));
            holder.textView2.setText(array_list.get(position).get("SurveyTypeName"));
            holder.textView3.setText(array_list.get(position).get("SurveyId"));

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Config.survey_id=holder.textView3.getText().toString();
                    Config.survey_name=holder.textView1.getText().toString();
                    Intent i=new Intent(MainActivity.this, StartSurvey.class);
                    startActivity(i);
                }
            });

            return convertView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPreferences sp=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            sp.edit().putString("user_name",null).apply();
            sp.edit().putString("password", null).apply();
            sp.edit().putBoolean("isChecked", false).apply();

            Intent i=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
