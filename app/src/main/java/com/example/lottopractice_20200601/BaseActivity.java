package com.example.lottopractice_20200601;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext = this;
    public abstract void setupEvents();
    public abstract void setValues();

}
