package com.jbnu.swe.capstone.casition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button signUp, login;
    private TextView findId, findPW, adminBtn;
    private EditText Id, PW;
    private String id, pw;
    private String server = "http://114.70.193.152:10111/hipowebserver_war/android/user/signin";

    private int count;

    SharedPreferences sf = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUp = (Button) findViewById(R.id.btn_signup);
        login = (Button) findViewById(R.id.btn_login);
        findId = (TextView) findViewById(R.id.btn_findid);
        findPW = (TextView) findViewById(R.id.btn_findpw);
        adminBtn = (TextView) findViewById(R.id.btn_admin);
        Id = (EditText) findViewById(R.id.user_id);
        PW = (EditText) findViewById(R.id.user_pw);

        sf = getSharedPreferences("user", Context.MODE_PRIVATE);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                id = Id.getText().toString();
                pw = PW.getText().toString();

                if(id.isEmpty()){       //??? ?????? ??????
                    Id.setError("???????????? ???????????????.");
                    Id.requestFocus();

                } else if(pw.isEmpty()){
                    PW.setError("??????????????? ???????????????.");
                    PW.requestFocus();

                } else {
                    checkExistUser();
                }
            }
        });

        findId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IDfindActivity.class);
                startActivity(intent);
            }
        });

        findPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PWfindActivity.class);
                startActivity(intent);
            }
        });

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;

                if(count % 6 == 0) {
                    Intent intent = new Intent(getApplicationContext(), AdminLoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void checkExistUser(){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();

            JSONObject postJsonData = new JSONObject();
            postJsonData.put("id", id);
            postJsonData.put("pw", pw);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),postJsonData.toString());
            Request request = new Request.Builder().url(server).post(requestBody).build();
            Response response = client.newCall(request).execute();

            Log.d("response","============"+response.code());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String data = response.body().string();
                    if (response.code() == 200) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);

                                    sf.edit().putString("id",id).apply();
                                    sf.edit().putString("name", jsonObject.getString("userName")).apply();
//                                    sf.edit().putString("plateNum", "38???0667").apply();
                                    if(jsonObject.getString("userCarNumber")!=null && !jsonObject.getString("userCarNumber").equals("")){
                                        sf.edit().putString("plateNum", jsonObject.getString("userCarNumber")).apply();
                                    }
                                    Log.d("response","============"+data);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }, 0);
                    } else {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Id.setError("???????????? ????????? ????????????.");
                                Id.requestFocus();
                            }
                        }, 0);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
