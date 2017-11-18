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
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    String password;
    Button yes, cancel, clean;
    String oldpassword, newpassword, configpassword;
    EditText editText_oldpassword, editText_newpassword, editText_configpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        first();
        GlobalVariable gv = (GlobalVariable) getApplicationContext();

        password = gv.getPassword();
    }

    public void first() {
        editText_oldpassword = (EditText) findViewById(R.id.editText_oldpassword);
        editText_newpassword = (EditText) findViewById(R.id.editText_newpassword);
        editText_configpassword = (EditText) findViewById(R.id.editText_configpassword);
        yes = (Button) findViewById(R.id.button_yes);
        cancel = (Button) findViewById(R.id.button_cancel);
        clean = (Button) findViewById(R.id.button_clean);
    }

    //禁用系統返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }


    public void setYes(View view) {
        oldpassword = editText_oldpassword.getText().toString();
        newpassword = editText_newpassword.getText().toString();
        configpassword = editText_configpassword.getText().toString();


        if (oldpassword.equals(password)) {
            if (newpassword.equals(configpassword)) {
                if (newpassword.equals("") && configpassword.equals("")) {
                    new AlertDialog.Builder(ChangePasswordActivity.this)
                            .setTitle("錯誤")
                            .setMessage("密碼不可為空白。請重新輸入!")
                            .setIcon(R.mipmap.ic_launcher_round)
                            .setNegativeButton("確定",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    }).show();
                } else {
                    GlobalVariable gv = (GlobalVariable) getApplicationContext();
                    gv.setPassword(configpassword);
                    Toast.makeText(this, "密碼已變更", Toast.LENGTH_LONG).show();
                    first();
                    Intent back = new Intent(this, FunctionActivity.class);
                    startActivity(back);
                }

            } else if (!newpassword.equals(configpassword)) {
                new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setTitle("錯誤")
                        .setMessage("新密碼與確認密碼不相同")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setNegativeButton("確定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        editText_newpassword.setText("");
                                        editText_configpassword.setText("");

                                        // TODO Auto-generated method stub
                                    }
                                }).show();
            }
        } else if (!oldpassword.equals(password)) {
            new AlertDialog.Builder(ChangePasswordActivity.this)
                    .setTitle("錯誤")
                    .setMessage("舊密碼不正確。請重新輸入！")
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setNegativeButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    editText_oldpassword.setText("");
                                    editText_newpassword.setText("");
                                    editText_configpassword.setText("");

                                    // TODO Auto-generated method stub
                                }
                            }).show();

        }


    }

    public void setCancel(View view) {
        finish();
        Intent back = new Intent(this, FunctionActivity.class);
        startActivity(back);

    }

    public void setClean(View view) {
        editText_oldpassword.setText("");
        editText_newpassword.setText("");
        editText_configpassword.setText("");

    }

}