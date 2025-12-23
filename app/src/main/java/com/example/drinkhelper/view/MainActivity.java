package com.example.drinkhelper.view;

import static android.app.ProgressDialog.show;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.drinkhelper.R;
import com.example.drinkhelper.databinding.MainpageBinding;
import com.example.drinkhelper.databinding.TimeModuleBinding;
import com.example.drinkhelper.databinding.WaterCountModuleBinding;
import com.example.drinkhelper.viewmodel.TimerViewModel;
import com.example.drinkhelper.viewmodel.WaterViewModel;
import com.example.drinkhelper.viewmodel.SettingsViewModel;

public class MainActivity extends AppCompatActivity {

    private MainpageBinding mainpageBinding;
    private Context context;
    private WaterCountModuleBinding waterCountModuleBinding;
    private TimeModuleBinding timeModuleBinding;
    private Vibrator vibrator;

    private WaterViewModel waterViewModel;
    private TimerViewModel timerViewModel;
    private SettingsViewModel settingsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainpageBinding = MainpageBinding.inflate(getLayoutInflater());

        setContentView(mainpageBinding.getRoot());
        timeModuleBinding = mainpageBinding.includeCountdown;
        waterCountModuleBinding = mainpageBinding.includeWaterCount;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        context = this;
        checkAndRequestVibratePermission();

        waterViewModel = new ViewModelProvider(this).get(WaterViewModel.class);
        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        timeModuleBinding.btnSettings.setOnClickListener(v -> openSettings());

        waterViewModel.getTargetWater().observe(this, value -> {
            if (value != null) {
                waterCountModuleBinding.pbWater.setMax(value);
                Integer cur = waterViewModel.getCurrentWater().getValue();
                if (cur == null) {
                    cur = 0;
                }
                updateWaterDisplay(cur, value);
            }
        });
        waterViewModel.getCurrentWater().observe(this, value -> {
            if (value != null) {
                waterCountModuleBinding.pbWater.setProgress(value);  //更新进度条
                Integer target = waterViewModel.getTargetWater().getValue();
                if (target == null) {
                    target = 0;
                }
                updateWaterDisplay(value, target); //更新数字
            }
        });

        waterViewModel.getChickenSoup().observe(this, text -> {
            if (text != null) {
                waterCountModuleBinding.tvScrollText.setText(text);
            }
        });

        timerViewModel.getRemainingMillis().observe(this, millis -> {
            if (millis != null) {
                updateCountdownText(millis);
            }
        });
        timerViewModel.getTimerFinishedEvent().observe(this, finished -> {
            if (Boolean.TRUE.equals(finished)) {
                Toast.makeText(MainActivity.this, "该喝水了", Toast.LENGTH_SHORT).show();
                timesUpAlertRingEvent();
                confirmDrinkWaterEvent();
                timerViewModel.ackFinishEvent();
            }
        });

        waterCountModuleBinding.addWaterButton.setOnClickListener(v -> increaseWaterEvent(null));
        waterCountModuleBinding.setTargetButton.setOnClickListener(v -> setWaterTargetEvent());
        waterCountModuleBinding.setWaterButton.setOnClickListener(v -> setCurrentWaterEvent());

        timeModuleBinding.StartButton.setOnClickListener(v -> {
            timerViewModel.start();
            Toast.makeText(this, "倒计时已开始", Toast.LENGTH_SHORT).show();
        });
        timeModuleBinding.ResetButton.setOnClickListener(v -> {
            timerViewModel.reset();
        });
        timeModuleBinding.SetTimeButton.setOnClickListener(v -> setTimeLength());
        timeModuleBinding.PauseButton.setOnClickListener(v -> {
            timerViewModel.pause();
            Toast.makeText(MainActivity.this, "已暂停", Toast.LENGTH_SHORT).show();
        });

        timeModuleBinding.btnSettings.setOnClickListener(v -> openSettings());
    }

    private void openSettings() {
//        Toast.makeText(this, "设置功能开发中", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }


    private void updateCountdownText(int millis) {
        int minutes = (int) (millis / 1000 / 60);
        int seconds = (int) (millis / 1000 % 60);
        String timeText = String.format("%02d:%02d", minutes, seconds);
        timeModuleBinding.tvCountdownTime.setText(timeText);
    }

    private void setTimeLength() {
//        showInputDialog(this, "设定时间间隔（分钟）", "请输入时间间隔（注意：时间会重置）", 30, new OnInputConfirmListener() {
//            @Override
//            public void onConfirm(double inputValue) {
//
//                timerViewModel.setTimeLengthMinutes(inputValue);
//                timerViewModel.reset();
//            }
//        });
        setTimeLengthByNumberPicker();
    }


    private void setTimeLengthByNumberPicker() {
        String[] items = new String[]{"0.1", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "70", "80", "90", "100", "110", "120"};
        GeneralPickerDialog.show(this, "选择时间间隔（分钟）", items
                , 5, new GeneralPickerDialog.PickedListener() {
                    @Override
                    public void onPicked(int selectionIndex) {
                        double value = Double.parseDouble(items[selectionIndex]);
                        timerViewModel.setTimeLengthMinutes(value);
                        timerViewModel.reset();
                    }
                });


    }


    private void shakeEvent()  // 震动功能
    {
        if (vibrator == null || !vibrator.hasVibrator()) {
            Toast.makeText(this, "设备不支持震动", Toast.LENGTH_SHORT).show();
            return;
        }

        // 模式1：简单短震动（震动500毫秒，适合提醒）【推荐】
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0+ 新API（推荐）
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(vibrationEffect);
        } else {
            // 兼容8.0以下系统
            vibrator.vibrate(500); // 参数：震动时长（毫秒）
        }
    }

    private void timesUpAlertRingEvent() {
        SharedPreferences sp = settingsViewModel.getPrefs();
        String curSetting = sp.getString(SettingsViewModel.STRING_ALERT_SETTING, SettingsViewModel.STRING_SHAKE_ONLY);
        if (curSetting == null) {
            curSetting = SettingsViewModel.STRING_SHAKE_ONLY;

        }
        if (SettingsViewModel.STRING_SHAKE_ONLY.equals(curSetting)) {
            shakeEvent();
            Toast.makeText(this, "已震动", Toast.LENGTH_SHORT).show();
            return;

        }

        if (SettingsViewModel.STRING_NOTIFICATION_ONLY.equals(curSetting)) {
            Uri curUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (curUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, curUri);
                ringtone.play();
                Toast.makeText(this, "已播放系统通知声", Toast.LENGTH_SHORT).show();
            } else {
                shakeEvent();
                Toast.makeText(this, "系统无通知声资源，只震动", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (SettingsViewModel.STRING_SHAKE_NOTIFICATION.equals(curSetting)) {
            Uri curUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (curUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, curUri);
                ringtone.play();
                Toast.makeText(this, "已播放系统通知声和震动", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "系统无通知声资源，只震动", Toast.LENGTH_SHORT).show();
            }
            shakeEvent();
            return;
        }

        if (SettingsViewModel.STRING_ALERT_ONLY.equals(curSetting)) {
            Uri curUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (curUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, curUri);
                ringtone.play();
                Toast.makeText(this, "已播放系统闹钟声", Toast.LENGTH_SHORT).show();
            } else {
                shakeEvent();
                Toast.makeText(this, "系统无闹钟声资源，只震动", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (SettingsViewModel.STRING_ALERT_SHAKE.equals(curSetting)) {
            Uri curUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (curUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, curUri);
                ringtone.play();
                Toast.makeText(this, "已播放系统闹钟声和震动", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "系统无闹钟声资源，只震动", Toast.LENGTH_SHORT).show();
            }
            shakeEvent();
            return;
        }

    }

    private void checkAndRequestVibratePermission() {
        // Android 13+ 需要动态申请VIBRATE权限，低版本无需
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU = API 33（Android 13）
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 未授权，动态申请
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.VIBRATE},
                        1001
                );
            }
        }
    }

    //删除原来的初始化进度条的接口 因为改用了viewmodel的架构


    private void increaseWaterEvent(OnWaterIncreasedListener listener) {

        String[] gap = {"0", "50", "100", "150", "200", "250", "300", "350", "400", "450", "500",};
        GeneralPickerDialog.show(this, "增加喝水（ml）", gap, 2, new GeneralPickerDialog.PickedListener() {
            @Override
            public void onPicked(int selectedIndex) {
                int value = Integer.parseInt(gap[selectedIndex]);
                waterViewModel.increase(value);
                waterViewModel.randomizeChickenSoup();
                Toast.makeText(MainActivity.this, "已增加" + value + "ml", Toast.LENGTH_SHORT).show();
                Integer cur = waterViewModel.getCurrentWater().getValue();
                Integer target = waterViewModel.getTargetWater().getValue();
                if (cur != null && target != null && cur >= target) {
                    Toast.makeText(MainActivity.this, "已喝满！", Toast.LENGTH_SHORT).show();
                }
                if (listener != null) {
                    listener.onWaterIncreased();
                }
            }
        });
    }

    private void confirmDrinkWaterEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("喝水提醒")
                .setMessage("倒计时结束啦，该喝水了！")
                .setNeutralButton("下次再喝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        timerViewModel.start();


                    }
                })
                .setPositiveButton("马上喝水", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        increaseWaterEvent(new OnWaterIncreasedListener() {
                            @Override
                            public void onWaterIncreased() {
                                timerViewModel.start();
                            }
                        });

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("暂停倒计时", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timerViewModel.pause();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);


        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void updateWaterDisplay(int a, int b) {
        // 核心修改：用waterCountModuleBinding.getString()替代Activity的getString()
        String displayText = String.format(
                waterCountModuleBinding.tvWaterValue.getContext().getString(R.string.water_value),
                a,  // 对应模板里的%1$d（currentWater）
                b   // 对应模板里的%2$d（targetWater）
        );
        waterCountModuleBinding.tvWaterValue.setText(displayText);
    }


    private void setWaterTargetEvent() {
//        Integer defaultTarget = waterViewModel.getTargetWater().getValue();
//        if (defaultTarget == null) {
//            defaultTarget = 2000;
//        }
//        showInputDialog(this, "设置目标（ml）", "请输入目标值", defaultTarget, new OnInputConfirmListener() {
//
//            @Override
//            public void onConfirm(double inputValue) {
//                int target = (int) inputValue;
//                waterViewModel.setTarget(target);
//                Integer cur = waterViewModel.getCurrentWater().getValue();
//                if (cur == null) {
//                    cur = 0;
//                }
//                updateWaterDisplay(cur, target);
//                Toast.makeText(MainActivity.this, "目标已更新为" + target, Toast.LENGTH_SHORT).show();
//            }
//        }
        setWaterTargetByNumberPicker();
    }

    public void setWaterTargetByNumberPicker() {
        String[] target = {"100", "200", "300", "400", "500", "600", "700"
                , "800", "900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700", "1800", "1900", "2000", "2100", "2200", "2300", "2400", "2500",};
        GeneralPickerDialog.show(this, "增加喝水（ml）", target, 14, new GeneralPickerDialog.PickedListener() {
            @Override
            public void onPicked(int selectedIndex) {
                int value = Integer.parseInt(target[selectedIndex]);
                waterViewModel.setTarget(value);
                Integer cur = waterViewModel.getCurrentWater().getValue();
                if (cur == null) {
                    cur = 0;
                }
                updateWaterDisplay(cur, value);

                Toast.makeText(MainActivity.this, "目标已更新为" + value, Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void setCurrentWaterEvent() {
        Integer defaultCurrent = waterViewModel.getCurrentWater().getValue();
        if (defaultCurrent == null) {
            defaultCurrent = 0;
        }
        showInputDialog(this, "设置当前值（ml）", "请输入当前值", defaultCurrent, new OnInputConfirmListener() {

            @Override
            public void onConfirm(double inputValue) {
                int cur = (int) inputValue;
                waterViewModel.setCurrent(cur);
                Integer target = waterViewModel.getTargetWater().getValue();
                if (target == null) {
                    target = 0;
                }
                updateWaterDisplay(cur, target);
                Toast.makeText(MainActivity.this, "当前已更新为" + cur, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showInputDialog(Context context, String title, String hint, int defaultValue, OnInputConfirmListener listener) {
        // 1. 创建EditText输入框
        EditText editText = new EditText(context);
        // 设置输入类型为数字（仅允许输入整数）
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        // 设置提示文字和默认值
        editText.setHint(hint);
        editText.setText(String.valueOf(defaultValue));
        // 选中默认值，方便直接修改
        editText.selectAll();

        // 2. 构建AlertDialog弹窗
        new AlertDialog.Builder(context)
                .setTitle(title)          // 弹窗标题
                .setView(editText)        // 嵌入输入框
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 3. 获取输入内容并校验
                        String inputStr = editText.getText().toString().trim();
                        if (inputStr.isEmpty()) {
                            Toast.makeText(context, "输入不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        double inputValue;
                        try {
                            // 转换为整数
                            inputValue = Double.parseDouble(inputStr);
                            // 业务校验：比如不能小于0
                            if (inputValue < 0) {
                                Toast.makeText(context, "请输入大于等于0的数字！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            // 输入非数字的情况
                            Toast.makeText(context, "请输入有效的数字！", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 4. 校验通过，回调传递数值
                        listener.onConfirm(inputValue);
                        dialog.dismiss(); // 关闭弹窗
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 关闭弹窗
                    }
                })
                .setCancelable(false) // 点击外部不关闭弹窗
                .show();
    }


    public interface OnInputConfirmListener {
        void onConfirm(double inputValue);


    }


    // 定义回调接口：输入水量确认后触发
    public interface OnWaterIncreasedListener {
        void onWaterIncreased(); // 水量增加完成后的回调
    }


}
