package com.jbnu.swe.capstone.casition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class AdminLoginActivity extends Activity {

    private String adminPW;

    private EditText editpw;
    private Button btn_adminLogin;

    private String server = "http://114.70.193.152:10111/hipowebserver_war/android/admin/signin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        editpw = findViewById(R.id.editpw);
        btn_adminLogin = findViewById(R.id.btn_adminlogin);

        btn_adminLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                adminPW = editpw.getText().toString();

                if(adminPW.isEmpty()){       //빈 문항 처리
                    editpw.setError("비밀번호를 입력하세요.");
                    editpw.requestFocus();

                }else {
                    if (checkIsAdmin()){
                        Toast.makeText(AdminLoginActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), AdministratorActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(AdminLoginActivity.this, "일치하는 정보가 없습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private Boolean checkIsAdmin(){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();

            JSONObject postJsonData = new JSONObject();
            postJsonData.put("pw", adminPW);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),postJsonData.toString());

            Request request = new Request.Builder().url(server).post(requestBody).build();

            Response response = client.newCall(request).execute();

            Log.d("response","============"+response.code());

            if(response.code() == 200){        //로그인 성공
                return true;
            }
            else{
                return false;        //로그인 실패
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}


