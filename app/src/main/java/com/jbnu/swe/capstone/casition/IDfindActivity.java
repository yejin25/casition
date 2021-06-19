package com.jbnu.swe.capstone.casition;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IDfindActivity extends AppCompatActivity {
    private EditText Name, Email;
    private Button findId;
    private String name, email;
    private String server = "http://114.70.193.152:10111/hipowebserver_war/android/user/find/id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findid);

        Name = (EditText) findViewById(R.id.input_name);
        Email = (EditText) findViewById(R.id.input_email);
        findId = (Button) findViewById(R.id.btn_auth);

        findId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Name.getText().toString();
                email = Email.getText().toString();

                if (name.isEmpty()){       //빈 문항 처리
                    Name.setError("이름을 입력하세요.");
                    Name.requestFocus();

                } else if(email.isEmpty()){
                    Email.setError("이메일을 입력하세요.");
                    Email.requestFocus();

                } else {
                    if(isExistUser()){
                        Toast.makeText(IDfindActivity.this, "메일이 전송되었습니다. 메일을 확인해주세요.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(IDfindActivity.this, "일치하는 정보가 없습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private boolean isExistUser(){
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();

            JSONObject postJsonData = new JSONObject();
            postJsonData.put("userName", name);
            postJsonData.put("userEmail", email);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postJsonData.toString());
            Request request = new Request.Builder().url(server).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){        //존재하는 회원
                return true;
            }else{
                return false;        //일치 정보 없음
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

