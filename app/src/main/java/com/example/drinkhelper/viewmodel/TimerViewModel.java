package com.example.drinkhelper.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TimerViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> timeLengthMillis = new MutableLiveData<>();
    private final MutableLiveData<Integer> remainingMillis = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> timerFinishedEvent = new MutableLiveData<>(false);

    private CountDownTimer countDownTimer;
    private final SharedPreferences prefs;

    public static final String STRING_TIME_LENGTH_MILLIS = "timeLengthMillis";

    public TimerViewModel(Application application) {
        super(application);
        prefs = application.getSharedPreferences(SettingsViewModel.STRING_PREFS_NAME, Context.MODE_PRIVATE);
        int savedLen = prefs.getInt(STRING_TIME_LENGTH_MILLIS, 30 * 60 * 1000);
        timeLengthMillis.setValue(savedLen);
        remainingMillis.setValue(savedLen);
    }

    public LiveData<Integer> getTimeLengthMillis() {
        return timeLengthMillis;
    }

    public LiveData<Integer> getRemainingMillis() {
        return remainingMillis;
    }

    public LiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    public LiveData<Boolean> getTimerFinishedEvent() {
        return timerFinishedEvent;
    }

    public void setTimeLengthMinutes(double minutes) {
        if (minutes <= 0) {
            return;
        }
        int millis = (int) (minutes * 60 * 1000);
        timeLengthMillis.setValue(millis);
        prefs.edit().putInt(STRING_TIME_LENGTH_MILLIS, millis).apply();
        resetInternal(millis);
    }

    public void start() {
        Integer curLeft = remainingMillis.getValue();
        if (curLeft == null) {
            curLeft = timeLengthMillis.getValue();
        }
        if (curLeft == null) {
            curLeft = 30 * 60 * 1000;
        }
        cancelTimer();
        countDownTimer = new CountDownTimer(curLeft, 1000) {
            @Override
            public void onFinish() {
                Integer len = timeLengthMillis.getValue();
                if (len == null) {
                    len = 30 * 60 * 1000;
                }
                resetInternal(len);
                isRunning.setValue(false);
                timerFinishedEvent.setValue(true);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis.setValue((int) millisUntilFinished);
            }
        };
        countDownTimer.start();
        isRunning.setValue(true);
    }

    public void pause() {
        if (Boolean.TRUE.equals(isRunning.getValue())) {
            cancelTimer();
            isRunning.setValue(false);
        }
    }

    public void reset() {
        Integer len = timeLengthMillis.getValue();
        if (len == null) {
            len = 30 * 60 * 1000;
        }
        resetInternal(len);
        isRunning.setValue(false);
        pause();
    }

    public void ackFinishEvent() {
        timerFinishedEvent.setValue(false);
    }

    private void resetInternal(int millis) {
        remainingMillis.setValue(millis);
        cancelTimer();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelTimer();
    }
}
