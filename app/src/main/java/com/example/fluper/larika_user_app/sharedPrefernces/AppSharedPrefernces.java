package com.example.fluper.larika_user_app.sharedPrefernces;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by rohit on 6/6/17.
 */

public class AppSharedPrefernces {
    private static AppSharedPrefernces appSharedPrefrence;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public AppSharedPrefernces(Context context) {
        this.appSharedPrefs = context.getSharedPreferences("sharedpref", Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }
    private List<String> saveSubject(Context context)
    {
        List<String>saveSubjectId=new ArrayList<>();

        return saveSubjectId;
    }
    public String getSubjectSelection() {
        return appSharedPrefs.getString("subject_selection", "");
    }

    public void setSubjectSelection(String id) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString("subject_selection", id);
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public String getSelection() {
        return appSharedPrefs.getString("selection", "");
    }

    public void setSelection(String id) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString("selection", id);
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public static AppSharedPrefernces getsharedprefInstance(Context con) {
        if (appSharedPrefrence == null) {
            appSharedPrefrence = new AppSharedPrefernces(con);
        }
        return appSharedPrefrence;
    }
    public void setYear(int year) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt("year", year);
        //   prefsEditor.clear();
        prefsEditor.commit();

    }


    public void setMonth(int month) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt("month", month);
        // prefsEditor.clear();
        prefsEditor.commit();
    }


    public void setDay(int day) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt("day", day);
        // prefsEditor.clear();
        prefsEditor.commit();
    }

    public int getDay() {
        Calendar cc = Calendar.getInstance();
        int day=cc.get(Calendar.DAY_OF_MONTH);
        return appSharedPrefs.getInt("day", day);
    }
    public int getMonth()
    {
        Calendar cc = Calendar.getInstance();
        int month=cc.get(Calendar.MONTH);
        return appSharedPrefs.getInt("month",month);
    }
    public int getYear() {
        Calendar cc = Calendar.getInstance();
        int year=cc.get(Calendar.YEAR);
        return appSharedPrefs.getInt("year",year);

    }

    public SharedPreferences getAppSharedPrefs() {
        return appSharedPrefs;
    }

    public void setAppSharedPrefs(SharedPreferences appSharedPrefs) {
        this.appSharedPrefs = appSharedPrefs;
    }

    public SharedPreferences.Editor getPrefsEditor() {
        return prefsEditor;
    }

    public void Commit() {
        prefsEditor.commit();
    }

    public void clearallSharedPrefernce() {
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public Boolean getSession() {

        return appSharedPrefs.getBoolean("WiseOakSession", false);
    }

    public void setSession(Boolean bool) {
        this.prefsEditor = appSharedPrefs.edit();
        prefsEditor.putBoolean("WiseOakSession", bool);
        prefsEditor.commit();
    }

  /*  public void getSelectedDay(){
        return appSharedPrefrence.getSelectedDay();
    }
    */

    public void setSelectedDay(Date dateClicked) {
        this.prefsEditor=appSharedPrefs.edit();
        prefsEditor.putInt("date", Integer.parseInt(dateClicked.toString()));
        prefsEditor.commit();
    }
}
