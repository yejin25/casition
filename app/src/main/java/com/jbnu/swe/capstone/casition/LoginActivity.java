package com.jbnu.swe.capstone.casition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Button signUp, login;
    private TextView findId, findPW;
    private EditText Id, PW;
    private String id, pw;
    private String server = "http://114.70.193.152:10111/hipowebserver_war/android/user/signin";

    SharedPreferences sf = null;
    SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUp = (Button) findViewById(R.id.btn_signup);
        login = (Button) findViewById(R.id.btn_login);
        findId = (TextView) findViewById(R.id.btn_findid);
        findPW = (TextView) findViewById(R.id.btn_findpw);
        Id = (EditText) findViewById(R.id.user_id);
        PW = (EditText) findViewById(R.id.user_pw);

        sf = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sf.edit();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
//                finish();     //회원가입 하려다가 아이디 생각났을 수도 있잖아...?
            }
        });

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                id = Id.getText().toString();
                pw = PW.getText().toString();

                if(id.isEmpty()){       //빈 문항 처리
                    Id.setError("아이디를 입력하세요.");
                    Id.requestFocus();

                } else if(pw.isEmpty()){
                    PW.setError("비밀번호를 입력하세요.");
                    PW.requestFocus();

                } else {
                    if(isExistUser()){
                        editor.putString("id",id);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "일치하는 정보가 없습니다.", Toast.LENGTH_LONG).show();
                    }
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
    }

    private boolean isExistUser(){
        try {
           StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();
//            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new BasicAuthInterceptor(id, pw)).build();

            JSONObject postJsonData = new JSONObject();
            postJsonData.put("id", id);
            postJsonData.put("pw", pw);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),postJsonData.toString());

            Request request = new Request.Builder().url(server).post(requestBody).build();

            Response response = client.newCall(request).execute();

            Log.d("response","============"+response.code());

            if(response.code() == 200){        //로그인 성공
                return true;
            }else{
                return false;        //로그인 실패
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
