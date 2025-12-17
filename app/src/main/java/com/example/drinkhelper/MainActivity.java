package com.example.drinkhelper;

import static android.app.ProgressDialog.show;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drinkhelper.databinding.MainpageBinding;
import com.example.drinkhelper.databinding.TimeModuleBinding;
import com.example.drinkhelper.databinding.WaterCountModuleBinding;


public class MainActivity extends AppCompatActivity{

    private MainpageBinding mainpageBinding;
    private CountDownTimer countDownTimer;
    private int curLeftTime;

    private int timeLength=30*60*1000;
    private Context context;
    private WaterCountModuleBinding waterCountModuleBinding;
    private TimeModuleBinding timeModuleBinding;
    private int currentWater=0;
    private int targetWater=2000; //默认目标是2000ml

    private boolean isPaused=false;

    private Vibrator vibrator;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainpageBinding=MainpageBinding.inflate(getLayoutInflater());

        setContentView(mainpageBinding.getRoot());
        timeModuleBinding=mainpageBinding.includeCountdown;
        waterCountModuleBinding=mainpageBinding.includeWaterCount;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        context=this;
        initWaterProgressBar();
        initTimer(timeLength);
        checkAndRequestVibratePermission();


        waterCountModuleBinding.addWaterButton.setOnClickListener(v -> increaseWaterEvent(null));
        waterCountModuleBinding.setTargetButton.setOnClickListener(v -> setWaterTargetEvent());
        waterCountModuleBinding.setWaterButton.setOnClickListener(v -> setCurrentWaterEvent());



        timeModuleBinding.StartButton.setOnClickListener(v -> startTimer());
        timeModuleBinding.ResetButton.setOnClickListener(v -> resetTimer());
        timeModuleBinding.SetTimeButton.setOnClickListener(v -> setTimeLength());
        timeModuleBinding.PauseButton.setOnClickListener(v -> pauseTimer());
    }


    private void updateCountdownText(long millis) {
        // 转换毫秒为 分:秒（比如5分钟=300000毫秒 → 05:00）
        int minutes = (int) (millis / 1000 / 60);
        int seconds = (int) (millis / 1000 % 60);

        // 格式化：保证两位数（比如1秒→01，5分→05）
        String timeText = String.format("%02d:%02d", minutes, seconds);
        // 更新TextView
        timeModuleBinding.tvCountdownTime.setText(timeText);
    }

    //countdown的问题是：countdown的计时器不能暂停，只能重新开始

    private void initTimer(int inputTime)
    {
        curLeftTime=inputTime;
        countDownTimer=new CountDownTimer(curLeftTime,1000) {
            @Override
            public void onFinish() {
                initTimer(timeLength);
                Toast.makeText(MainActivity.this, "该喝水了", Toast.LENGTH_SHORT).show();
                timesUpAlertSoundEvent();
                confirmDrinkWaterEvent();



            }

            @Override
            public void onTick(long millisUntilFinished) {
                curLeftTime= Math.toIntExact(millisUntilFinished);
                updateCountdownText(curLeftTime);
            }
        };
        updateCountdownText(curLeftTime);
    }

    private void resetTimer(){
        curLeftTime=timeLength;
        updateCountdownText(curLeftTime);
        isPaused=false;
        pauseTimer();
    }

    private void startTimer(){
        initTimer(curLeftTime);
        countDownTimer.start();
        Toast.makeText(this, "倒计时已开始", Toast.LENGTH_SHORT).show();
        isPaused=false;
    }

    private void pauseTimer(){
        if(countDownTimer!=null && !isPaused)
        {
            countDownTimer.cancel();
            isPaused=true;
            Toast.makeText(MainActivity.this, "已暂停", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTimeLength(){
        showInputDialog(this, "设定时间间隔（分钟）", "请输入时间间隔（注意：时间会重置）", 30, new OnInputConfirmListener() {
            @Override
            public void onConfirm(double inputValue) {
                // 输入确认，更新目标值

                timeLength= (int) (inputValue*60*1000);

                resetTimer();
            }



        });
    }


    private void timesUpAlertSoundEvent()  // 震动功能
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


    private void initWaterProgressBar(){
        setTargetWater(targetWater);
        setCurrentWater(currentWater);
        updateWaterDisplay(currentWater, targetWater);
    }

    private void setTargetWater(int targetWater){
        this.targetWater=targetWater;

        waterCountModuleBinding.pbWater.setMax(targetWater);
        updateWaterDisplay(currentWater, targetWater);
    }

    private void setCurrentWater(int currentWater){
        this.currentWater=currentWater;

        waterCountModuleBinding.pbWater.setProgress(currentWater);
        updateWaterDisplay(currentWater, targetWater);
    }


    private void increaseWaterEvent(OnWaterIncreasedListener listener){
         showInputDialog(this, "增加喝水（ml）", "请输入增加量", 200, new OnInputConfirmListener() {
             @Override
             public void onConfirm(double inputValue) {
                 // 输入确认，更新目标值
                 currentWater+=(int)  inputValue;
                 setCurrentWater(currentWater);
                 Toast.makeText(MainActivity.this, "已增加" + inputValue + "ml", Toast.LENGTH_SHORT).show();
                 if(currentWater>=targetWater)
                 {
                     Toast.makeText(MainActivity.this, "已喝满！", Toast.LENGTH_SHORT).show();

                 }

                 if(listener!=null)
                 {
                     listener.onWaterIncreased();
                 }
             }
         });
    }

    private void confirmDrinkWaterEvent(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 弹窗标题和提示语（可根据你的需求修改）
        builder.setTitle("喝水提醒")
                .setMessage("倒计时结束啦，该喝水了！")
                // 选项1：下次再喝
                .setNeutralButton("下次再喝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 1. 先关闭弹窗（可选：也可以等输入完成后关，看你交互需求）
                        dialog.dismiss();
                        startTimer();


                    }
                })
                // 选项2：马上喝水
                .setPositiveButton("马上喝水", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // --------------------------
                        // 你需要实现的逻辑：马上喝水
                        // 示例：更新喝水量、刷新UI、重置倒计时等
                        // 2. 调用增加水量方法，通过回调执行倒计时逻辑（解耦）
                        increaseWaterEvent(new OnWaterIncreasedListener() {
                            @Override
                            public void onWaterIncreased() {
                                // 输入水量确认后，再重启倒计时
                                startTimer();
                            }
                        });

                        // --------------------------
                        // 关闭弹窗（必须保留）
                        dialog.dismiss();
                    }
                })
                // 选项3：暂停倒计时
                .setNegativeButton("暂停倒计时", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // --------------------------
                        // 你需要实现的逻辑：暂停倒计时
                        // 示例：调用pauseTimer()等
                        pauseTimer();
                        // --------------------------
                        // 关闭弹窗（必须保留）
                        dialog.dismiss();
                    }
                })
                // 禁止点击外部关闭弹窗（可选，根据你的需求决定是否保留）
                .setCancelable(false);


        // 2. 创建并显示对话框
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



    private void setWaterTargetEvent()
    {
        showInputDialog(this, "设置目标（ml）", "请输入目标值", targetWater, new OnInputConfirmListener() {

            @Override
            public void onConfirm(double inputValue) {
                // 输入确认，更新目标值
                targetWater=(int)   inputValue;
                setTargetWater(targetWater);
                Toast.makeText(MainActivity.this, "目标已更新为" + targetWater, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCurrentWaterEvent()
    {
        showInputDialog(this, "设置当前值（ml）", "请输入当前值", currentWater, new OnInputConfirmListener() {

            @Override
            public void onConfirm(double    inputValue) {
                // 输入确认，更新目标值
                currentWater=(int) inputValue;
                setCurrentWater(currentWater);
                Toast.makeText(MainActivity.this, "当前已更新为" + currentWater, Toast.LENGTH_SHORT).show();
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
                            if (inputValue <= 0) {
                                Toast.makeText(context, "请输入大于0的数字！", Toast.LENGTH_SHORT).show();
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
