package com.nkfust.edward_liao.cranemachine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Binding extends AppCompatActivity {

    String ID;
    Button yes, cancel, clean;
    String password, configpassword;
    EditText editText_id, editText_password, editText_configpassword;

    String cmtoken;
    String cmuid;

    String stauts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);

        first();
        GlobalVariable gv = (GlobalVariable) getApplicationContext();


        cmuid = gv.getCM_ID();
        cmtoken = gv.getCM_Token();

        Log.d("TAG", "Token: " + cmtoken);


        ID = editText_id.getText().toString();
    }

    public void first() {
        editText_id = (EditText) findViewById(R.id.editText_id);
        editText_password = (EditText) findViewById(R.id.editText_password);
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

        ID = editText_id.getText().toString();
        password = editText_password.getText().toString();
        configpassword = editText_configpassword.getText().toString();

//        Send_Student_ID();


        if (password.equals("") || configpassword.equals("")) {

            new AlertDialog.Builder(Binding.this)
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


        } else if (password.equals(configpassword)) {
            if (password.equals("") && configpassword.equals("")) {

                new AlertDialog.Builder(Binding.this)
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
                Send_Student_ID();


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        //過0.5秒後要做的事情
                        //跳出提示
                        hint();


                    }
                }, 500);


            }
        } else {
            new AlertDialog.Builder(Binding.this)
                    .setTitle("錯誤")
                    .setMessage("密碼與確認密碼不相同")
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setNegativeButton("確定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    editText_password.setText("");
                                    editText_configpassword.setText("");

                                    // TODO Auto-generated method stub
                                }
                            }).show();
        }


    }


    public void Send_Student_ID() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub

                try {
                    //建立要傳送的JSON物件
                    JSONObject json = new JSONObject();
                    json.put("NKID", ID);
                    json.put("NKpassword", password);
                    json.put("CMtoken", cmtoken);


                    //建立POST Request
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://163.18.2.157/registeredSchool");
                    //JSON物件放到POST Request
                    StringEntity stringEntity = new StringEntity(json.toString());
                    stringEntity.setContentType("application/json");
                    httpPost.setEntity(stringEntity);
                    //執行POST Request
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    //取得回傳的內容
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String responseString = EntityUtils.toString(httpEntity, "UTF-8");
                    //回傳的內容轉存為JSON物件
                    JSONObject responseJSON = new JSONObject(responseString);
                    //取得Message的屬性
                    stauts = responseJSON.getString("Status");

                    Log.d("TAG", "綁定狀態: " + stauts);


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);


    }

    public void hint() {
        if (stauts.equals("Success")) {
            new AlertDialog.Builder(Binding.this)
                    .setTitle("提示")
                    .setMessage("綁定成功")
                    .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            setBack();

                        }
                    })
                    .show();

        } else {
            new AlertDialog.Builder(Binding.this)
                    .setTitle("提示")
                    .setMessage("綁定失敗")
                    .setNegativeButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }


  /*  public void setYes(View view) {
        oldpassword = editText_oldpassword.getText().toString();
        newpassword = editText_newpassword.getText().toString();
        configpassword = editText_configpassword.getText().toString();


        if (oldpassword.equals(password)) {
            if (newpassword.equals(configpassword)) {
                if (newpassword.equals("") && configpassword.equals("")) {
                    new AlertDialog.Builder(Binding.this)
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
                new AlertDialog.Builder(Binding.this)
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
            new AlertDialog.Builder(Binding.this)
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

    */


    public void setBack() {


        finish();
        Intent back = new Intent(this, FunctionActivity.class);
        startActivity(back);

    }

    public void setCancel(View view) {
        finish();
        Intent back = new Intent(this, FunctionActivity.class);
        startActivity(back);

    }

    public void setClean(View view) {
        editText_id.setText("");
        editText_password.setText("");
        editText_configpassword.setText("");

    }

}