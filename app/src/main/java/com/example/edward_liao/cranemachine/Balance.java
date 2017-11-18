package com.example.edward_liao.cranemachine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Balance extends AppCompatActivity {
    TextView cash;
    Button back;

    int money_balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        first();

        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        money_balance = gv.getMoney_total();
        cash.setText(String.valueOf(money_balance));


    }


    //禁用系統返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    public void first() {
        cash = (TextView) findViewById(R.id.textView_balance);
        back = (Button) findViewById(R.id.button_back);


    }

    public void setBack(View view) {
        finish();
        Intent back = new Intent(this, FunctionActivity.class);
        startActivity(back);
    }
}