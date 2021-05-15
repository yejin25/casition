package com.jbnu.swe.capstone.casition;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    private Button checkId, signUp;
    private EditText userId, userPW, userCheckPW, userName, userEmail;
    private String server = "";     //아이디 서버 주세요...
    private Boolean checkID = false;        //근데 만약 중복확인하고 아이디 바꾸고 다시 중복확인 안하면...?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        checkId = (Button) findViewById(R.id.btn_overlap);
        signUp = (Button) findViewById(R.id.btn_check);
        userId = (EditText) findViewById(R.id.user_id);
        userPW = (EditText) findViewById(R.id.user_pw);
        userCheckPW = (EditText) findViewById(R.id.user_check_pw);
        userName = (EditText) findViewById(R.id.user_name);
        userEmail = (EditText) findViewById(R.id.user_email);

        checkId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOverlapId()){
                    Toast.makeText(SignUpActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(SignUpActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                    checkID = true;
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSignUp();
            }
        });
    }

    private boolean isOverlapId(){
        String id = userId.getText().toString();

        //아이디 중복 확인
        //여기서 아이디 보내주면 서버에서 있는지 확인 후 T/F 전달

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();

            JSONObject postJsonData = new JSONObject();
            postJsonData.put("id", id);

            RequestBody requestBody = RequestBody.create(postJsonData.toString(), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(server).post(requestBody).build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){        //사용가능한 아이디
                return false;
            }else{
                return true;        //이미 있는 아이디
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    private void checkSignUp(){
        String id = userId.getText().toString();
        String pw = userPW.getText().toString();
        String checkPW = userCheckPW.getText().toString();
        String name = userName.getText().toString();
        String email = userEmail.getText().toString();

        if(id.isEmpty()){       //빈 문항 처리
            userId.setError("아이디를 입력하세요.");
            userId.requestFocus();

        } else if(pw.isEmpty()){
            userPW.setError("비밀번호를 입력하세요.");
            userPW.requestFocus();

        } else if(checkPW.isEmpty()){
            userCheckPW.setError("비밀번호를 다시 한번 입력하세요.");
            userCheckPW.requestFocus();

        } else if(name.isEmpty()){
            userName.setError("이름을 입력하세요.");
            userName.requestFocus();

        } else if(email.isEmpty()){
            userEmail.setError("이메일을 입력하세요.");
            userEmail.requestFocus();

        }else {
            if(!checkID){
                Toast.makeText(SignUpActivity.this, "아이디 중복확인을 해주세요.", Toast.LENGTH_LONG).show();

            } else if (!pw.equals(checkPW)) {
                Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();

            } else {
                //json 형식으로 서버에 데이터 전송
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    OkHttpClient client = new OkHttpClient();

                    JSONObject postJsonData = new JSONObject();
                    postJsonData.put("id", id);
                    postJsonData.put("pw", pw);
                    postJsonData.put("name", name);
                    postJsonData.put("email", email);

                    RequestBody requestBody = RequestBody.create(postJsonData.toString(), MediaType.parse("application/json; charset=utf-8"));
                    Request request = new Request.Builder().url(server).post(requestBody).build();

//                            Response response = client.newCall(request).execute();
                    client.newCall(request).execute();


                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);   //이 경우 자동차 등록증 입력하는 화면으로 이동하는 게 좋을 것 같음. 로그인화면에서 들어가면 그냥 주차장 도면 나오게 하고
                startActivity(intent);
                finish();
            }
        }
    }
}
