package com.example.yuxi.picmatching;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void DelayStart() {
        View thinkView = findViewById(R.id.delaying);
        thinkView.setVisibility(View.VISIBLE);
    }

    public void DelayEnd() {
        View thinkView = findViewById(R.id.delaying);
        thinkView.setVisibility(View.GONE);
    }


}
