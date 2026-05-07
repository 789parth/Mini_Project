package com.example.miniproject.ManagerClass;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private static final String IS_LOGIN_DONE = "isLoginDone";
    private static final String IS_OTP_VERIFIED = "isOtpVerified";
    private static final String IS_LOCATION_SELECTED = "isLocationSelected";

    public SessionManager(Context context) {
        sp = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void saveUser(String uid, String name, String location, String email) {
        editor.putString("userid", uid);
        editor.putString("username", name);
        editor.putString("location", location);
        editor.putString("email", email);
        editor.apply();
    }

    public void setLoginDone(boolean isDone) {
        editor.putBoolean(IS_LOGIN_DONE, isDone);
        editor.apply();
    }

    public boolean isLoginDone() {
        return sp.getBoolean(IS_LOGIN_DONE, false);
    }

    public void setOtpVerified(boolean isVerified) {
        editor.putBoolean(IS_OTP_VERIFIED, isVerified);
        editor.apply();
    }

    public boolean isOtpVerified() {
        return sp.getBoolean(IS_OTP_VERIFIED, false);
    }

    public void setLocationSelected(boolean isSelected) {
        editor.putBoolean(IS_LOCATION_SELECTED, isSelected);
        editor.apply();
    }

    public boolean isLocationSelected() {
        return sp.getBoolean(IS_LOCATION_SELECTED, false);
    }

    public String getUid() {
        return sp.getString("userid", "xyz@gmail.com");
    }

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