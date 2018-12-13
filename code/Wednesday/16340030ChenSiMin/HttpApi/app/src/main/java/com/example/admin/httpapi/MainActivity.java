package com.example.admin.httpapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.admin.httpapi.BiliBili.BiliBiliActivity;
import com.example.admin.httpapi.Github.GithubActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void ButtonOnClick(View target) {
        Button button = (Button)target;
        if (button.getText().equals("BILIBILI API")) {
            Intent intent = new Intent(MainActivity.this, BiliBiliActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, GithubActivity.class);
            startActivity(intent);
        }
    }
}
