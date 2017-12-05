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
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import static com.nkfust.edward_liao.cranemachine.MainActivity.callbackManager;
import static com.nkfust.edward_liao.cranemachine.MainActivity.loginManager;

public class BindingFacebook extends AppCompatActivity {

    // FB
    public static LoginManager loginManager;
    public static CallbackManager callbackManager;

    String fb_token;
    String fb_ID, nkfust_ID, nkfust_password;
    String type;
    String status;


    String cmtoken;
    String cmuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_facebook);

        // init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        // init LoginManager & CallbackManager
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        nkfust_ID = gv.getId();


        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Facebook Login
                loginFB();
            }
        });
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


                            go();


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





    public void go() {
        Send_fb_id();

        setBack();

    }

    public void Send_fb_id() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub

                try {
                    //建立要傳送的JSON物件
                    JSONObject json = new JSONObject();
                    json.put("NKID", nkfust_ID);
                    json.put("FBID", fb_ID);
                    json.put("FBtoken", fb_token);


                    //建立POST Request
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://163.18.2.157/registeredFB");
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

                    Log.d("TAG", "綁定狀態: " + status);



                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);


    }

    public void hint() {
        if (status.equals("Success")) {
            new AlertDialog.Builder(BindingFacebook.this)
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
            new AlertDialog.Builder(BindingFacebook.this)
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

    public void setBack() {


        finish();
        Intent back = new Intent(this, FunctionActivity.class);
        startActivity(back);

    }
}
