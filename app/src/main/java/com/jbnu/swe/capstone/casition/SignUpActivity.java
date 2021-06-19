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
import okhttp3.ResponseBody;

public class SignUpActivity extends AppCompatActivity {

    private Button signUp;
    private EditText userId, userPW, userCheckPW, userName, userEmail;
    private String server = "http://114.70.193.152:10111/hipowebserver_war/android/user/signup";     //아이디 서버 주세요...
//    private Boolean checkID = false;        //근데 만약 중복확인하고 아이디 바꾸고 다시 중복확인 안하면...?

    SharedPreferences sf = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUp = (Button) findViewById(R.id.btn_check);
        userId = (EditText) findViewById(R.id.user_id);
        userPW = (EditText) findViewById(R.id.user_pw);
        userCheckPW = (EditText) findViewById(R.id.user_check_pw);
        userName = (EditText) findViewById(R.id.user_name);
        userEmail = (EditText) findViewById(R.id.user_email);

        sf = getSharedPreferences("user", Context.MODE_PRIVATE);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSignUp();
            }
        });
    }


    private void checkSignUp(){
        boolean isSuccess = false;

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
            userEmail.setError("전화번호를 입력하세요.");
            userEmail.requestFocus();

        }else {
            if (!pw.equals(checkPW)) {
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
                    postJsonData.put("userName", name);
                    postJsonData.put("userPhoneNumber",email);

                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),postJsonData.toString());
                    Request request = new Request.Builder()
                            .url(server)
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String data = response.body().string();

                                Log.d("response","=============="+data);

                                if (response.code() == 200) {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                                sf.edit().putString("id",id).apply();
                                                sf.edit().putString("name", name).apply();
                                                Intent intent = new Intent(getApplicationContext(), CarNumRegisterActivity.class);
                                                startActivity(intent);
                                                finish();
                                        }
                                    }, 0);

                                } else if (response.code() == 400) {
                                    if (data.contains("IS_HAVE_KEY")) {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                userId.setError("이미 존재하는 아이디입니다.");
                                                userId.requestFocus();
                                            }
                                        }, 0);

                                    } else if (data.contains("DATA_NOT_RECOGNIZE")) {
                                        Log.d("SignUp", "=============오류 " + data);
                                    }
                                }

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                   /*Response response = client.newCall(request).execute();

                    if(response.code() == 200){
                        Log.d("SignUp", "=============성공" + response.code());
                        isSuccess = true;
                    }else if (response.code() == 409){
                        userId.setError("이미 존재하는 아이디입니다.");
                        userId.requestFocus();
                        Log.d("SignUp", "=============중복" + response.code());
                    }else{
                        Log.d("SignUp", "=============오류" + response.code());
                    }*/



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //이 경우 자동차 등록증 입력하는 화면으로 이동하는 게 좋을 것 같음. 로그인화면에서 들어가면 그냥 주차장 도면 나오게 하고
                //아니면 sharedPreference 쓰던가

                    if(isSuccess){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
        }
    }
}
