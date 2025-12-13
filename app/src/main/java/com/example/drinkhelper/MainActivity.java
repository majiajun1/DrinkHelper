package com.example.drinkhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.ScriptGroup;

import com.example.drinkhelper.databinding.MainpageBinding;


public class MainActivity extends AppCompatActivity{

    private MainpageBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
    }


}
