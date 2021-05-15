package com.jbnu.swe.capstone.casition;

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

public class PWfindActivity extends AppCompatActivity {
    private EditText Name, Id;
    private Button findPW;
    private String name, id;
    private String server = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw);

        Name = (EditText) findViewById(R.id.inputpw_name);
        Id = (EditText) findViewById(R.id.inputpw_id);
        findPW = (Button) findViewById(R.id.btn_auth);

        findPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Name.getText().toString();
                id = Id.getText().toString();

                if (name.isEmpty()){       //빈 문항 처리
                    Name.setError("이름을 입력하세요.");
                    Name.requestFocus();

                } else if(id.isEmpty()){
                    Id.setError("이메일을 입력하세요.");
                    Id.requestFocus();

                } else {
                    if(isExistUser()){
                        //아이디 어떻게 알려주지...?
                    }else{
                        Toast.makeText(PWfindActivity.this, "일치하는 정보가 없습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private boolean isExistUser(){
        try {
            //여기도 서버에서 아이디 비밀번호 일치 확인해서 t/f 보내주세요...ㅎㅎ
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();

            JSONObject postJsonData = new JSONObject();
            postJsonData.put("name", name);
            postJsonData.put("id", id);

            RequestBody requestBody = RequestBody.create(postJsonData.toString(), MediaType.parse("application/json; charset=utf-8"));
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
