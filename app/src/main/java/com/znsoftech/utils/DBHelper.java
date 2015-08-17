package com.znsoftech.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mohsin on 17-08-2015.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists Consumer(ConsumerId INTEGER PRIMARY KEY AUTOINCREMENT, FirstName varchar, MiddleName varchar, LastName varchar, Address varchar, City varchar, State varchar, Country varchar, ZipCode varchar, Mobile varchar, Phone varchar, Gender varchar, Photo varchar, Email varchar, CreatedOn varchar, CreatedBy varchar, Latitude varhcar, Longitude varchar)");
        db.execSQL("create table if not exists Survey(ConsumerId INTEGER, SurveyId INTEGER, QuestionId INTEGER,Question varchar,AnswerID INTEGER,Answer varchar, latitude varchar, longitude varchar)");
    }

    public boolean insertIntoConsumer(String FirstName, String MiddleName, String LastName,String Address, String City, String State, String Country, String ZipCode, String Mobile, String Phone, String Gender, String Photo, String Email, String CreatedOn, String CreatedBy, String Latitude, String Longitude)
    {
        SQLiteDatabase sdb=this.getWritableDatabase();

        sdb.execSQL("insert into Consumer(FirstName,MiddleName,LastName,LastName,Address,City,State,Country,ZipCode,Mobile,Phone, Gender,Photo,Email,CreatedOn, CreatedBy,Latitude, Longitude ) values('" +
                FirstName + "','" + MiddleName + "','" + LastName + "','" + Address + "','" + City + "','" + State + "','" + Country + "','" + ZipCode + "','" + Mobile + "','" + Phone + "','" + Gender + "','" + Photo + "','" + Email + "','" + CreatedOn + "','" + CreatedBy + "','" + Latitude + "','" + Longitude + "')");

        return true;
    }

    public boolean insertIntoSurvey(int ConsumerId, int SurveyId, int QuestionId,String Question, int AnswerID, String Answer, String latitude, String longitude)
    {
        SQLiteDatabase sdb=this.getWritableDatabase();
        sdb.execSQL("insert into Survey values('"+ConsumerId+"','"+SurveyId+"','"+QuestionId+"','"+Question+"','"+AnswerID+"','"+Answer+"','"+latitude+"','"+longitude+"')");
        return true;
    }

    public Cursor getCunsumerData()
    {
        SQLiteDatabase sdb=this.getReadableDatabase();
        Cursor c=sdb.rawQuery("select * from Consumer", null);
        return c;
    }

    public Cursor getSurveyData(int ConsumerId)
    {
        SQLiteDatabase sdb=this.getReadableDatabase();
        Cursor c=sdb.rawQuery("select * from Survey where ConsumerId='"+ConsumerId+"'", null);
        return c;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Consumer");
        db.execSQL("DROP TABLE IF EXISTS Survey");
        onCreate(db);
    }
}
