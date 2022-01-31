package com.example.facedetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.droidsonroids.gif.GifImageView;

public class startActivity extends AppCompatActivity {
    GifImageView gif;
    TextView start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        gif=findViewById(R.id.gif_button);
        start= findViewById(R.id.textView);
        getSupportActionBar().hide();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(startActivity.this,MainActivity.class));
            }
        });

        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(startActivity.this,MainActivity.class));
            }
        });
    }
}