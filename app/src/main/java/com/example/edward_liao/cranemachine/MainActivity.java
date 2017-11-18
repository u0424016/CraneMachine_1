package com.example.edward_liao.cranemachine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText ET_ID, ET_password;
    Button login, clean;
    String ID, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        first();


    }


    //防止按到返回鍵就直接關閉程式
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog1,
                                                    int which1) {
                                    finish();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog1,
                                                    int which1) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return true;
    }


    private void first() {
        ET_ID = (EditText) findViewById(R.id.editText_ID);
        ET_password = (EditText) findViewById(R.id.editText_password);
        login = (Button) findViewById(R.id.button_login);
        clean = (Button) findViewById(R.id.button_clean);

    }


    public void AmdinLogin(View view) {
        finish();
        Intent Next = new Intent(this, FunctionActivity.class);
        startActivity(Next);

    }


    public void setLogin(View view) {

        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        String password_GV = gv.getPassword();

        ID = ET_ID.getText().toString();
        password = ET_password.getText().toString();

        if (ID.equals("") && password.equals("")) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("錯誤")
                    .setMessage("帳號密碼不可為空請輸入正確的密碼!")
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setNegativeButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).show();


        } else if (ID.equals("nkfust") && password.equals(password_GV)) {
            finish();
            Intent Next = new Intent(this, FunctionActivity.class);
            startActivity(Next);
        } else if (ID.equals("nkfust") && !password.equals(password_GV)) {
            if (password.equals("")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("密碼錯誤")
                        .setMessage("密碼不可為空白。請輸入正確的密碼!")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setNegativeButton("確定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();
                ET_password.setText("");
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("密碼錯誤")
                        .setMessage("請輸入正確的密碼!")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setNegativeButton("確定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();
                ET_ID.setText("");
                ET_password.setText("");
            }
        } else if (!ID.equals("nkfust")) {
            if (password.equals("")) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("密碼錯誤")
                        .setMessage("密碼不可為空白。請輸入正確的密碼!")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setNegativeButton("確定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();
                ET_password.setText("");
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("錯誤")
                        .setMessage("請重新輸入正確的帳號密碼!")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setNegativeButton("確定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();
                ET_ID.setText("");
                ET_password.setText("");
            }
        }
    }

    public void setForgotPassword(View view) {
        finish();
        Intent Forgot = new Intent(this, ForgotPasswordActivity.class);
        startActivity(Forgot);

    }

    public void setClean(View view) {
        ET_ID.setText("");
        ET_password.setText("");
        ID = "";
        password = "";
    }
}
