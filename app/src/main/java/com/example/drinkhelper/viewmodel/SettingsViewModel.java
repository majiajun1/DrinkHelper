package com.example.drinkhelper.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SettingsViewModel extends AndroidViewModel {

    private final SharedPreferences prefs;

    public static final String STRING_PREFS_NAME="drink_prefs";

    private final MutableLiveData<String> alertSetting=new MutableLiveData<>();

    public static final String STRING_ALERT_SETTING = "alertSetting";

    public static final String STRING_SHAKE_ONLY="shakeOnly";
    public static final String STRING_NOTIFICATION_ONLY="notificationOnly";
    public static final String STRING_SHAKE_NOTIFICATION="shakeAndNotification";
    public static final String STRING_ALERT_ONLY="alertOnly";
    public static final String STRING_ALERT_SHAKE="alertShake";


    public SettingsViewModel(Application application) {
        super(application);
        prefs = application.getSharedPreferences(STRING_PREFS_NAME, Context.MODE_PRIVATE);
        String saved = prefs.getString(STRING_ALERT_SETTING, STRING_SHAKE_ONLY);
        alertSetting.setValue(saved);
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public LiveData<String> getAlertSetting() {
        return alertSetting;
    }

    public void setAlertSetting(String alertSetting) {
        this.alertSetting.setValue(alertSetting);
        prefs.edit().putString(STRING_ALERT_SETTING, alertSetting).apply();
    }

}
