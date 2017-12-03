package com.nkfust.edward_liao.cranemachine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import static com.nkfust.edward_liao.cranemachine.MainActivity.loginManager;

public class FunctionActivity extends AppCompatActivity {

    Button pay, balance, deposit, withdraw, transfer, changepassword,binding, logout;

    int money_total;

    String Status_temp;

    String cmtoken;
    String cmuid;

    String get_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        first();


        GlobalVariable gv = (GlobalVariable) getApplicationContext();


        String url = "http://163.18.2.157/balance/ID/";
        cmuid = gv.getCM_ID();

        get_url = url + cmuid;
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

    /*
        //前往存款畫面
        public void setDeposit(View view) {
            finish();
            Intent Deposit = new Intent(this, Deposit.class);
            startActivity(Deposit);

        }

        //前往提款畫面
        public void setWithdraw(View view) {
            finish();
            Intent Withdraw = new Intent(this, Withdraw.class);
            startActivity(Withdraw);

        }

        //前往轉帳畫面
        public void setTransfer(View view) {
            finish();
            Intent Transfer = new Intent(this, Transfer.class);
            startActivity(Transfer);
        }
    */
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
        finish();
        Intent changepassword = new Intent(this, ChangePasswordActivity.class);
        startActivity(changepassword);
    }

    public void setBinding(View view) {
        finish();
        Intent binding = new Intent(this, Binding.class);
        startActivity(binding);
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


                    GlobalVariable gv = (GlobalVariable) getApplicationContext();

                    money_total = Balance;

                    gv.setMoney_total(money_total);


                    System.out.print("帳戶餘額：");
                    System.out.println(money_total);


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
