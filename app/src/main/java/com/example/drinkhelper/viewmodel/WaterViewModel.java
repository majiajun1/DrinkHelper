package com.example.drinkhelper.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.example.drinkhelper.R;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WaterViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> currentWater = new MutableLiveData<>();
    private final MutableLiveData<Integer> targetWater = new MutableLiveData<>();

    private final SharedPreferences prefs;

    private final MutableLiveData<String> chickenSoup= new MutableLiveData<>();


    public static final String STRING_TARGET_WATER = "targetWater";
    public static final String STRING_CURRENT_WATER = "currentWater";
    public static final String STRING_CHICKEN_SOUP = "chickenSoup";

    public WaterViewModel(Application application) {
        super(application);
        prefs = application.getSharedPreferences(SettingsViewModel.STRING_PREFS_NAME, Context.MODE_PRIVATE);
        int savedCurrent = prefs.getInt(STRING_CURRENT_WATER, 0);
        int savedTarget = prefs.getInt(STRING_TARGET_WATER, 2000);
        currentWater.setValue(savedCurrent);
        targetWater.setValue(savedTarget);
        initChickenSoup();
    }

    public LiveData<Integer> getCurrentWater() {
        return currentWater;
    }

    public LiveData<Integer> getTargetWater() {
        return targetWater;
    }

    public void setTarget(int target) {
        if (target <= 0) {
            return;
        }
        targetWater.setValue(target);
        prefs.edit().putInt(STRING_TARGET_WATER, target).apply();
    }

    public void setChickenSoup(String chickenSoup) {
        this.chickenSoup.setValue(chickenSoup);
    }

    public LiveData<String> getChickenSoup() {
        return chickenSoup;
    }

    private void initChickenSoup() {
        randomizeChickenSoup();
    }

    public void randomizeChickenSoup() {
        Resources res = getApplication().getResources();
        String[] list = res.getStringArray(R.array.chicken_soup_list);
        if (list == null || list.length == 0) {
            return;
        }
        int index = (int) (Math.random() * list.length);
        chickenSoup.setValue(list[index]);
    }



    public void setCurrent(int current) {
        if (current < 0) {
            current = 0;
        }
        currentWater.setValue(current);
        prefs.edit().putInt(STRING_CURRENT_WATER, current).apply();
    }

    public void increase(int delta) {
        if (delta <= 0) {
            return;
        }
        Integer cur = currentWater.getValue();
        if (cur == null) {
            cur = 0;
        }
        setCurrent(cur + delta);
    }
}
