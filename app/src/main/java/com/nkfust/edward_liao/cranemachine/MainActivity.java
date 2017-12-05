package com.nkfust.edward_liao.cranemachine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText ET_ID, ET_password;
    Button login, clean, button_newuser;

    private AccessToken accessToken;
    // FB
    public static LoginManager loginManager;
    public static CallbackManager callbackManager;

    String fb_token;
    String fb_ID, nkfust_ID, nkfust_password;
    String type;
    String status;


    String cmtoken;
    String cmuid;

    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        // init LoginManager & CallbackManager
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        // method_2.判斷用戶是否登入過
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("TAG", "Facebook 應用程式ID: " + AccessToken.getCurrentAccessToken().getApplicationId());
            Log.d("TAG", "Facebook 使用者ID: " + AccessToken.getCurrentAccessToken().getUserId());
            Log.d("TAG", "Facebook getExpires: " + AccessToken.getCurrentAccessToken().getExpires());
            Log.d("TAG", "Facebook getLastRefresh: " + AccessToken.getCurrentAccessToken().getLastRefresh());
            Log.d("TAG", "Facebook 使用者Token: " + AccessToken.getCurrentAccessToken().getToken());
            Log.d("TAG", "Facebook getSource: " + AccessToken.getCurrentAccessToken().getSource());


            fb_token = AccessToken.getCurrentAccessToken().getToken();
            fb_ID = AccessToken.getCurrentAccessToken().getUserId();
            type = "FB";

            loginFB();

        }


        first();

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Facebook Login
                loginFB();
            }
        });

/*
        // method_1.判斷用戶是否登入過
        if (Profile.getCurrentProfile() != null) {
            Profile profile = Profile.getCurrentProfile();
            // 取得用戶大頭照
            Uri userPhoto = profile.getProfilePictureUri(300, 300);
            String id = profile.getId();
            String name = profile.getName();
            Log.d("TAG", "Facebook userPhoto: " + userPhoto);
            Log.d("TAG", "Facebook id: " + id);
            Log.d("TAG", "Facebook name: " + name);

            fb_token = loginResult.getAccessToken().getToken();
            fb_ID = loginResult.getAccessToken().getUserId();
            type = "FB";

            toServer();
            finish();
            Intent Next = new Intent(this, FunctionActivity.class);
            startActivity(Next);
        }*/


    }


    private void loginFB() {
        // 設定FB login的顯示方式 ; 預設是：NATIVE_WITH_FALLBACK
        /**
         * 1. NATIVE_WITH_FALLBACK
         * 2. NATIVE_ONLY
         * 3. KATANA_ONLY
         * 4. WEB_ONLY
         * 5. WEB_VIEW_ONLY
         * 6. DEVICE_AUTH
         */
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_ONLY);
        // 設定要跟用戶取得的權限，以下3個是基本可以取得，不需要經過FB的審核
        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_friends");
        // 設定要讀取的權限
        loginManager.logInWithReadPermissions(this, permissions);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // 登入成功
                //可以取得相關資訊，這裡就請各位自行打印出來
                Log.d("TAG", "Facebook getApplicationId: " + loginResult.getAccessToken().getApplicationId());
                Log.d("TAG", "Facebook getUserId: " + loginResult.getAccessToken().getUserId());
                Log.d("TAG", "Facebook getExpires: " + loginResult.getAccessToken().getExpires());
                Log.d("TAG", "Facebook getLastRefresh: " + loginResult.getAccessToken().getLastRefresh());
                Log.d("TAG", "Facebook getToken: " + loginResult.getAccessToken().getToken());
                Log.d("TAG", "Facebook getSource: " + loginResult.getAccessToken().getSource());
                Log.d("TAG", "Facebook getRecentlyGrantedPermissions: " + loginResult.getRecentlyGrantedPermissions());
                Log.d("TAG", "Facebook getRecentlyDeniedPermissions: " + loginResult.getRecentlyDeniedPermissions());

                fb_token = loginResult.getAccessToken().getToken();
                fb_ID = loginResult.getAccessToken().getUserId();
                type = "FB";


                // 透過GraphRequest來取得用戶的Facebook資訊
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (response.getConnection().getResponseCode() == 200) {
                                long id = object.getLong("id");
                                String name = object.getString("name");
//                                String email = object.getString("email");
                                Log.d("TAG", "Facebook id:" + id);
                                Log.d("TAG", "Facebook name:" + name);
//                                Log.d("TAG", "Facebook email:" + email);
                                // 此時如果登入成功，就可以順便取得用戶大頭照
//                                Profile profile = Profile.getCurrentProfile();
//                                // 設定大頭照大小
//                                Uri userPhoto = profile.getProfilePictureUri(300, 300);
//                                Glide.with(MainActivity.this)
//                                        .load(userPhoto.toString())
//                                        .crossFade()
//                                        .into(mImgPhoto);
//                                mTextDescription.setText(String.format(Locale.TAIWAN, "Name:%s\nE-mail:%s", name, email));
                            }


                            toServer();


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // https://developers.facebook.com/docs/android/graph?locale=zh_TW
                // 如果要取得email，需透過添加參數的方式來獲取(如下)
                // 不添加只能取得id & name
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
                // 用戶取消
                Log.d("TAG", "Facebook onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                // 登入失敗
                Log.d("TAG", "Facebook onError:" + error.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

        button_newuser.setVisibility(View.INVISIBLE);

        finish();
        Intent Next = new Intent(this, FunctionActivity.class);
        startActivity(Next);

    }

    public void Login() {
        finish();
        Intent Next = new Intent(this, FunctionActivity.class);
        startActivity(Next);

    }


    public void setLogin(View view) {

        GlobalVariable gv = (GlobalVariable) getApplicationContext();

        //取得帳號
        nkfust_ID = ET_ID.getText().toString();
        //儲存帳號
        gv.setId(nkfust_ID);

        //取得密碼
        nkfust_password = ET_password.getText().toString();
        //儲存密碼
        gv.setPassword(nkfust_password);


        if (nkfust_ID.equals("") || nkfust_password.equals("")) {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("錯誤")
                    .setMessage("帳號及密碼不可為空請輸入正確的密碼!")
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

            //將帳號密碼傳送至伺服器檢查
            login_to_server();


        }
    }

    public void setForgotPassword(View view) {
        Uri uri = Uri.parse("https://ccms.nkfust.edu.tw/password_forget.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public void setClean(View view) {

        i = i + 1;

        if (i == 7) {
            button_newuser = (Button) findViewById(R.id.button_newuser);

            button_newuser.setVisibility(View.VISIBLE);
        }


        ET_ID.setText("");
        ET_password.setText("");
        nkfust_ID = "";
        nkfust_password = "";
    }

    public void no_service() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("無法連接伺服器")
                .setMessage("請確認網路或稍後再試")
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public void fail() {

        ET_password.setText("");

        ET_password.setHint("密碼錯誤 請重新輸入");


    }


    public void toServer() {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub

                try {
                    //建立要傳送的JSON物件
                    JSONObject json = new JSONObject();
                    json.put("Token", fb_token);
                    json.put("ID", fb_ID);
                    json.put("Type", type);


                    //建立POST Request
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://163.18.2.157:80/gettoken");
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
                    cmtoken = responseJSON.getString("CMtoken");
                    cmuid = responseJSON.getString("CMUID");

                    Log.d("TAG", "CMtoken: " + cmtoken);
                    Log.d("TAG", "CMUID: " + cmuid);

                    GlobalVariable gv = (GlobalVariable) getApplicationContext();
                    gv.setCM_Token(cmtoken);
                    gv.setCM_ID(cmuid);

                    if (cmuid.equals("") || cmtoken.equals("")) {
                        no_service();
                    } else {
                        Login();

                    }


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);

    }

    public void login_to_server() {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub

                try {
                    //建立要傳送的JSON物件
                    JSONObject json = new JSONObject();
                    json.put("NKID", nkfust_ID);
                    json.put("NKpassword", nkfust_password);
                    json.put("Type", "NK");


                    //建立POST Request
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://163.18.2.157:80/gettoken");
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
                    status = responseJSON.getString("Status");


                    if (status.equals("Success")) {

                        cmtoken = responseJSON.getString("CMtoken");
                        cmuid = responseJSON.getString("CMUID");

                        GlobalVariable gv = (GlobalVariable) getApplicationContext();
                        gv.setCM_Token(cmtoken);
                        gv.setCM_ID(cmuid);


                        Log.d("TAG", "CMtoken: " + cmtoken);
                        Log.d("TAG", "CMUID: " + cmuid);
                        Log.d("TAG", "登入狀態: " + status);

                        if (cmuid.equals("null") && cmtoken.equals("null")) {
                            no_service();
                        } else {
                            Login();

                        }


                    } else if (status.equals("PasswordError")) {

                        Log.d("TAG", "登入狀態: " + status);

                        Log.d("TAG", "密碼錯誤");

                        ET_password.setText("");


                    } else {
                        no_service();

                    }


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);

    }


}
