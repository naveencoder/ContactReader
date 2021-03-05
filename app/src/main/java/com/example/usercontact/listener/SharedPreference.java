package com.example.usercontact.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class SharedPreference {
    private SharedPreferences mySharedPreference;
    private SharedPreferences.Editor mySharedEditor;
    public SharedPreference(Context aContext) {
        mySharedPreference = PreferenceManager.getDefaultSharedPreferences(aContext);
        mySharedEditor = mySharedPreference.edit();
    }
    // ------code for clearing the session after logout-------
    public void clear() {
        mySharedEditor.clear();
        mySharedEditor.commit();
    }

    public void putPhoneNumber(ArrayList<String> aPhoneNumber) {
        mySharedEditor.putString("PHONE_NUMBER", new Gson().toJson(aPhoneNumber));
        mySharedEditor.commit();
    }

    public ArrayList<String> getPhoneNumber() {
        try {
            return new Gson().fromJson(mySharedPreference.getString("PHONE_NUMBER", ""),
                    new TypeToken<List<String>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
