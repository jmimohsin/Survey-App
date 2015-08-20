package com.znsoftech.utils;

import android.app.Fragment;
import android.content.Context;

import com.znsoftech.fragments.checkbox_fragment;
import com.znsoftech.fragments.edittext_fragment;
import com.znsoftech.fragments.radiobutton_fragment;

import org.json.JSONArray;

/**
 * Created by Mohsin on 11-08-2015.
 */
public class Config {

    public static String user_id=null;
    public static String user_name=null;
    public static String survey_name=null;
    public static String survey_id=null;
    public static int timer=0;
    public static int DBConsumerId=0;

    public static int total_survey_question=0;
    public static int next_question_index=0;
    public static JSONArray survey_question_array=null;

    public static String website="http://survey.znsoftech.com/";
    public static String login_url=website+"mobileapp/login.php";
    public static String question_url=website+"mobileapp/question.php";
    public static String survay_main_page_url=website+"mobileapp/survey.php";

    public static Fragment getQuestionTypeFragment(int questionTypeId){
        Fragment fragment=null;
        switch (questionTypeId){
            case 1:
                fragment=new edittext_fragment();
                break;
            case 2:
                fragment=new checkbox_fragment();
                break;
            case 3:
                fragment=new radiobutton_fragment();
                break;
        }
        return fragment;
    }
}
