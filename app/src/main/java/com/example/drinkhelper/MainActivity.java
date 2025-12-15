package com.example.drinkhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.renderscript.ScriptGroup;

import com.example.drinkhelper.databinding.MainpageBinding;
import com.example.drinkhelper.databinding.TimeModuleBinding;
import com.example.drinkhelper.databinding.WaterCountModuleBinding;


public class MainActivity extends AppCompatActivity{

    private MainpageBinding mainpageBinding;


    private Context context;
    private WaterCountModuleBinding waterCountModuleBinding;
    private TimeModuleBinding timeModuleBinding;
    private int currentWater=0;
    private int targetWater=1000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainpageBinding=MainpageBinding.inflate(getLayoutInflater());

        setContentView(mainpageBinding.getRoot());
        timeModuleBinding=mainpageBinding.includeCountdown;
        waterCountModuleBinding=mainpageBinding.includeWaterCount;
        context=this;




    }








}
