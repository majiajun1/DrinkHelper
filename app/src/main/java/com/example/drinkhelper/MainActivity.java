package com.example.drinkhelper;

import static android.app.ProgressDialog.show;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drinkhelper.databinding.MainpageBinding;
import com.example.drinkhelper.databinding.TimeModuleBinding;
import com.example.drinkhelper.databinding.WaterCountModuleBinding;


public class MainActivity extends AppCompatActivity{

    private MainpageBinding mainpageBinding;


    private Context context;
    private WaterCountModuleBinding waterCountModuleBinding;
    private TimeModuleBinding timeModuleBinding;
    private int currentWater=0;
    private int targetWater=2000; //默认目标是2000ml


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainpageBinding=MainpageBinding.inflate(getLayoutInflater());

        setContentView(mainpageBinding.getRoot());
        timeModuleBinding=mainpageBinding.includeCountdown;
        waterCountModuleBinding=mainpageBinding.includeWaterCount;
        context=this;
        initWaterProgressBar();


        waterCountModuleBinding.addWaterButton.setOnClickListener(v -> increaseWaterEvent());
        waterCountModuleBinding.setTargetButton.setOnClickListener(v -> setWaterTargetEvent());
        waterCountModuleBinding.setWaterButton.setOnClickListener(v -> setCurrentWaterEvent());

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


    private void increaseWaterEvent(){
         showInputDialog(this, "增加喝水（ml）", "请输入增加量", 200, new OnInputConfirmListener() {
             @Override
             public void onConfirm(int inputValue) {
                 // 输入确认，更新目标值
                 currentWater+=inputValue;
                 setCurrentWater(currentWater);
                 Toast.makeText(MainActivity.this, "已增加" + inputValue + "ml", Toast.LENGTH_SHORT).show();
             }
         });
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
            public void onConfirm(int inputValue) {
                // 输入确认，更新目标值
                targetWater=inputValue;
                setTargetWater(targetWater);
                Toast.makeText(MainActivity.this, "目标已更新为" + targetWater, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCurrentWaterEvent()
    {
        showInputDialog(this, "设置当前值（ml）", "请输入当前值", currentWater, new OnInputConfirmListener() {

            @Override
            public void onConfirm(int inputValue) {
                // 输入确认，更新目标值
                currentWater=inputValue;
                setCurrentWater(currentWater);
                Toast.makeText(MainActivity.this, "当前已更新为" + currentWater, Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void showInputDialog(Context context, String title, String hint, int defaultValue, OnInputConfirmListener listener) {
        // 1. 创建EditText输入框
        EditText editText = new EditText(context);
        // 设置输入类型为数字（仅允许输入整数）
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                        int inputValue;
                        try {
                            // 转换为整数
                            inputValue = Integer.parseInt(inputStr);
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
        void onConfirm(int inputValue);
    }


}
