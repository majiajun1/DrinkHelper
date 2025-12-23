package com.example.drinkhelper.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.drinkhelper.R;
import com.example.drinkhelper.viewmodel.SettingsViewModel;

public class SettingActivity extends AppCompatActivity {

    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        Spinner spinner = findViewById(R.id.spinner_alert_type);
        String current = settingsViewModel.getPrefs()
                .getString(SettingsViewModel.STRING_ALERT_SETTING, SettingsViewModel.STRING_SHAKE_ONLY);
        int initPosition;
        if (SettingsViewModel.STRING_SHAKE_ONLY.equals(current)) {
            initPosition = 0;
        } else if (SettingsViewModel.STRING_ALERT_SHAKE.equals(current)) {
            initPosition = 1;
        } else if (SettingsViewModel.STRING_ALERT_ONLY.equals(current)) {
            initPosition = 2;
        } else if (SettingsViewModel.STRING_NOTIFICATION_ONLY.equals(current)) {
            initPosition = 3;
        } else if (SettingsViewModel.STRING_SHAKE_NOTIFICATION.equals(current)) {
            initPosition = 4;
        } else {
            initPosition = 0;
        }
        spinner.setSelection(initPosition, false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value;
                switch (position) {
                    case 0:
                        value = SettingsViewModel.STRING_SHAKE_ONLY;
                        break;
                    case 1:
                        value = SettingsViewModel.STRING_ALERT_SHAKE;
                        break;
                    case 2:
                        value = SettingsViewModel.STRING_ALERT_ONLY;
                        break;
                    case 3:
                        value = SettingsViewModel.STRING_NOTIFICATION_ONLY;
                        break;
                    case 4:
                        value = SettingsViewModel.STRING_SHAKE_NOTIFICATION;
                        break;
                    default:
                        value = SettingsViewModel.STRING_SHAKE_ONLY;
                        break;
                }
                settingsViewModel.setAlertSetting(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 不做处理
            }
        });
    }
}
