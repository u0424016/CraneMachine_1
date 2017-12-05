package com.nkfust.edward_liao.cranemachine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import static com.nkfust.edward_liao.cranemachine.MainActivity.loginManager;

public class FunctionActivity extends AppCompatActivity {

    Button pay, balance, deposit, withdraw, transfer, changepassword, binding, url, logout;

    int money_total;

    String Status_temp;

    String account_FB,account_school,account_google;

    String cmtoken;
    String cmuid;

    String get_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        first();


        GlobalVariable gv = (GlobalVariable) getApplicationContext();


        String url = "http://163.18.2.157/getuser/";
        cmuid = gv.getCM_ID();
        cmtoken = gv.getCM_Token();
        get_url = url + cmuid + "/"+cmtoken;
        Log.d("TAG", get_url);

        getBalance();

    }

    //禁用系統返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }


    private void first() {
        pay = (Button) findViewById(R.id.button_pay);
        balance = (Button) findViewById(R.id.button_balance);
        changepassword = (Button) findViewById(R.id.button_changepassword);
        logout = (Button) findViewById(R.id.button_logout);
    }

    public void getBalance() {
        toServer();


    }

    //前往付款畫面
    public void setPay(View view) {
        finish();
        Intent PayActivity = new Intent(this, Pay.class);
        startActivity(PayActivity);

    }

    //前往餘額查詢畫面
    public void setBalance(View view) {
        finish();
        Intent Balance = new Intent(this, Balance.class);
        startActivity(Balance);

    }

    //前往登出畫面
    public void setLogout(View view) {


        new AlertDialog.Builder(FunctionActivity.this)
                .setTitle("")
                .setMessage("確定登出?")
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton("登出",
                        new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //確定登出
                                loginManager.logOut();
                                Logout();
                                // TODO Auto-generated method stub
                            }
                        })

                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();

    }

    public void setChangepassword(View view) {
        Uri uri = Uri.parse("https://ccms.nkfust.edu.tw/index.int.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void setURL(View view) {

        Uri uri = Uri.parse("http://163.18.2.157:8880");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public void setBinding(View view) {


        if(account_school.equals("null")||account_FB.equals("null")){

            if(account_FB.equals("null")){

                Intent binding_FB = new Intent(this, BindingFacebook.class);
                startActivity(binding_FB);



            }else if(account_school.equals("null")){
                finish();
                Intent binding = new Intent(this, Binding.class);
                startActivity(binding);
            }





        }else{
            Toast.makeText(this, "帳戶已綁定!", Toast.LENGTH_SHORT).show();

        }




    }

    public void setAboutApp(View view) {
        Intent aboutApp = new Intent(this, AboutApp.class);
        startActivity(aboutApp);
    }


    public void toServer() {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub

                try {


                    //建立POST Request
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet get = new HttpGet(get_url);


                    //執行Get
                    HttpResponse httpResponse = httpClient.execute(get);
                    //取得回傳的內容
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String responseString = EntityUtils.toString(httpEntity, "UTF-8");
                    //回傳的內容轉存為JSON物件
                    JSONObject responseJSON = new JSONObject(responseString);
                    //取得Balance的屬性
                    int Balance = responseJSON.getInt("balance");
                    account_FB = responseJSON.getString("account_FB");
                    account_google = responseJSON.getString("account_google");
                    account_school = responseJSON.getString("account_school");
                    Log.d("TAG", "FB帳戶狀態：" + account_FB);
                    Log.d("TAG", "Google帳戶狀態：" + account_google);
                    Log.d("TAG", "學校帳戶狀態：" + account_school);





                    GlobalVariable gv = (GlobalVariable) getApplicationContext();

                    money_total = Balance;

                    gv.setMoney_total(money_total);

                    Log.d("TAG", "帳戶餘額：" + money_total);


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);


    }


    //登出
    public void Logout() {


        finish();
        Intent Login = new Intent(this, MainActivity.class);
        startActivity(Login);

    }


}
