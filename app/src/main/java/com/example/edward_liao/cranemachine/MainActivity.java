package com.example.edward_liao.cranemachine;

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
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText ET_ID, ET_password;
    Button login, clean;
    String ID, password;

    private AccessToken accessToken;
    // FB
    public static LoginManager loginManager;
    public static CallbackManager callbackManager;

    String fb_token;
    String fb_ID;
    String type;


    String cmtoken;
    String cmuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        // init LoginManager & CallbackManager
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Facebook Login
                loginFB();
            }
        });


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


            finish();
            Intent Next = new Intent(this, FunctionActivity.class);
            startActivity(Next);
        }

        // method_2.判斷用戶是否登入過
        /*if (AccessToken.getCurrentAccessToken() != null) {
            Log.d(TAG, "Facebook getApplicationId: " + AccessToken.getCurrentAccessToken().getApplicationId());
            Log.d(TAG, "Facebook getUserId: " + AccessToken.getCurrentAccessToken().getUserId());
            Log.d(TAG, "Facebook getExpires: " + AccessToken.getCurrentAccessToken().getExpires());
            Log.d(TAG, "Facebook getLastRefresh: " + AccessToken.getCurrentAccessToken().getLastRefresh());
            Log.d(TAG, "Facebook getToken: " + AccessToken.getCurrentAccessToken().getToken());
            Log.d(TAG, "Facebook getSource: " + AccessToken.getCurrentAccessToken().getSource());
        }*/


        first();


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
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
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
                                String email = object.getString("email");
                                Log.d("TAG", "Facebook id:" + id);
                                Log.d("TAG", "Facebook name:" + name);
                                Log.d("TAG", "Facebook email:" + email);
                                // 此時如果登入成功，就可以順便取得用戶大頭照
                                Profile profile = Profile.getCurrentProfile();
                                // 設定大頭照大小
                                Uri userPhoto = profile.getProfilePictureUri(300, 300);
//                                Glide.with(MainActivity.this)
//                                        .load(userPhoto.toString())
//                                        .crossFade()
//                                        .into(mImgPhoto);
//                                mTextDescription.setText(String.format(Locale.TAIWAN, "Name:%s\nE-mail:%s", name, email));
                            }

                            Login();
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


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);

    }
}
