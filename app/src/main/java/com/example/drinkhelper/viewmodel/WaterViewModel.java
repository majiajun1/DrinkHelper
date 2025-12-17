package com.example.drinkhelper.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WaterViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> currentWater = new MutableLiveData<>();
    private final MutableLiveData<Integer> targetWater = new MutableLiveData<>();

    private final SharedPreferences prefs;

    public WaterViewModel(Application application) {
        super(application);
        prefs = application.getSharedPreferences("drink_prefs", Context.MODE_PRIVATE);
        int savedCurrent = prefs.getInt("currentWater", 0);
        int savedTarget = prefs.getInt("targetWater", 2000);
        currentWater.setValue(savedCurrent);
        targetWater.setValue(savedTarget);
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
        prefs.edit().putInt("targetWater", target).apply();
    }

    public void setCurrent(int current) {
        if (current < 0) {
            current = 0;
        }
        currentWater.setValue(current);
        prefs.edit().putInt("currentWater", current).apply();
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
