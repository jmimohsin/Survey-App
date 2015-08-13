package com.znsoftech.network;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Mohsin on 11-08-2015.
 */
public class JsonParsor {

    static JSONObject jobj=null;
    static InputStream in=null;

    public static JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params){
        HttpURLConnection conn=null;
        try {
            if (method == "GET") {
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
            }
            URL url_obj = new URL(url);
            conn=(HttpURLConnection)url_obj.openConnection();
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(method != "GET");

            if (method == "POST") {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();
            }

            conn.connect();
            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                in=conn.getInputStream();
            }else{
                Log.e("Response code",conn.getResponseCode()+"");
            }

        }catch (MalformedURLException e){
            Log.e("URL Exception", e.toString());
        } catch (ProtocolException e) {
            Log.e("Protocol Exception", e.toString());
        } catch (IOException e) {
            Log.e("IO Exception", e.toString());
        }

        if(in!=null){
            String json_string=convertStreamToString(in);
            try {
                jobj=new JSONObject(json_string);
            } catch (JSONException e) {
                Log.e("Json Exception", e.toString());
            }
        }

        return jobj;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e("IO Exception", e.toString());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("IO Exception", e.toString());
            }
        }
        return sb.toString();
    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
