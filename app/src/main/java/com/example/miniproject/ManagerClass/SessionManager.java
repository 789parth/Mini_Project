package com.example.miniproject.ManagerClass;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sp = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void saveUser(String uid,String name, String location, String email) {
        editor.putString("userid", uid);
        editor.putString("username", name);
        editor.putString("location", location);
        editor.putString("email", email);
        editor.apply();
    }

    public String getUid(){return sp.getString("uid", "xyz@gmail.com");};
    public String getUsername() {
        return sp.getString("username", "Unknown");
    }

    public String getLocation() {
        return sp.getString("location", "Location");
    }

    public String getEmail() {
        return sp.getString("email", "xyz@gmail.com");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}