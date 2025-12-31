package com.example.starsystem;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    final static String SavingKey = "APP_STATUS";
    private DrawView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawView = new DrawView(this);
        setContentView(drawView);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) { // Сохранение

        outState.putSerializable(SavingKey, drawView.SavingData());
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { // Загрузка
        super.onRestoreInstanceState(savedInstanceState);

        Data LoadData = (Data) savedInstanceState.getSerializable(SavingKey);
        drawView.LoadingData(LoadData);
    }
}